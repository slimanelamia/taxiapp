package com.pcd.taxiapp;

import static com.pcd.taxiapp.MainActivity.SERVER_IP;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

public class LocationService extends IntentService {
	

	

	public LocationService() {
		super("LocationService");
		// TODO Auto-generated constructor stub
	}

	public static final String BROADCAST_ACTION = "Hello World";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	private static String username=new String();
	public Location loc;

	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);      
	}

	@Override
	public void onStart(Intent intent, int startId) {      
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		String provider= locationManager.getBestProvider(new Criteria(), true);
		username=intent.getExtras().getString("D_USERNAME");
		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 5000,listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 5000,listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {       
		// handler.removeCallbacks(sendUpdatesToUI);     
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates((android.location.LocationListener) listener);        
	}   

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}


	public class MyLocationListener implements android.location.LocationListener
	{

		public void onLocationChanged(final Location loc)
		{
			Log.i("**************************************", "Location changed");
			if(isBetterLocation(loc, previousBestLocation)) {
				loc.getLatitude();
				loc.getLongitude();
				ArrayList<String> params=new ArrayList<String>();
				String longitude=Double.toString(loc.getLongitude());
				String latitude=Double.toString(loc.getLatitude());
				params.add(longitude);
				params.add(latitude);
				params.add(username);
				new storeLocation().execute(params);
				Log.i("latitude longitude", loc.getLatitude()+" "+loc.getLongitude());
				intent.putExtra("Latitude", loc.getLatitude());
				intent.putExtra("Longitude", loc.getLongitude());     
				intent.putExtra("Provider", loc.getProvider());                 
				sendBroadcast(intent);          

			}                               
		}

		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}


		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}


		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}

	}
	
	private class storeLocation extends AsyncTask<ArrayList<String>, Void, Void> {
		
		@Override
		protected Void doInBackground(ArrayList<String>... arg0) {
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			ArrayList<String> passed= arg0[0];
			params.add(new BasicNameValuePair("longitude", passed.get(0)));
			params.add(new BasicNameValuePair("latitude", passed.get(1)));
			params.add(new BasicNameValuePair("username", passed.get(2)));
			ServiceHandler sh=new ServiceHandler();
			sh.makeServiceCall("http://"+SERVER_IP+"/RestfulProject/REST/WebService/StoreLocation", ServiceHandler.POST, params);
			return null;
		}
		
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		String provider= locationManager.getBestProvider(new Criteria(), true);
		username=intent.getExtras().getString("D_USERNAME");
		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 5000,listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 5000,listener);
		
	}


}
