package com.pcd.taxiapp;

import static com.pcd.taxiapp.MainActivity.SERVER_IP;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.location.LocationListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainDriverActivity extends ActionBarActivity {
	public static String d_username;
	private LocationService mLocationService;
	Intent locatorService = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_driver);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		String username=getIntent().getExtras().getString("D_USERNAME");
		TextView welcome=(TextView) findViewById(R.id.username);
		welcome.append(username);
		d_username=username;
		Intent intent= new Intent(this, LocationService.class);
		intent.putExtra("D_USERNAME", username);
		startService();
		//startService(intent);
		/*String latitude=intent.getExtras().getString("Latitude");
		String longitude=intent.getExtras().getString("Longitude");
		Toast.makeText(this, "long: "+longitude+"\n lat: "+latitude, Toast.LENGTH_SHORT);
		LocationManager lm= new LocationManager();
		Location currentLocation=(Location) lm.getLocation(this);
		double lat=currentLocation.getLatitude();
		double longitude=currentLocation.getLongitude();
		Toast.makeText(this, "long: "+longitude+" lat: "+lat, Toast.LENGTH_SHORT).show();*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_driver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_driver,
					container, false);
			return rootView;
		}
	}
	public boolean stopService() {
        if (this.locatorService != null) {
            this.locatorService = null;
        }
        return true;
    }

    public boolean startService() {
        try {
            // this.locatorService= new
            // Intent(FastMainActivity.this,LocatorService.class);
            // startService(this.locatorService);

            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }

    }
    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        ProgressDialog progDailog = null;

        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new VeggsterLocationListener();
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 6000, 5000,
                    mVeggsterLocationListener);

            progDailog = new ProgressDialog(MainDriverActivity.this);
            progDailog.setOnDismissListener(new OnDismissListener() {
               
            	@Override
				public void onDismiss(DialogInterface dialog) {
					FetchCordinates.this.cancel(true);
					
				}
            });
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        @Override
        protected void onCancelled(){
            System.out.println("Cancelled by user!");
            progDailog.dismiss();
           // mLocationManager.removeUpdates(mVeggsterLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            progDailog.dismiss();
            ArrayList<String> params=new ArrayList<String>();
			String longitude=Double.toString(longi);
			String latitude=Double.toString(lati);
			String username=getIntent().getExtras().getString("D_USERNAME");
			params.add(longitude);
			params.add(latitude);
			params.add(username);
			new storeLocation().execute(params);
            Toast.makeText(MainDriverActivity.this,
                    "LATITUDE :" + lati + " LONGITUDE :" + longi,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... arg0) {
        	/*ServiceHandler sh=new ServiceHandler();
        	List<NameValuePair> params=new ArrayList<NameValuePair>();
        	String username=getIntent().getExtras().getString("D_USERNAME");
        	params.add(new BasicNameValuePair("longitude", Double.toString(longi)));
			params.add(new BasicNameValuePair("latitude",Double.toString(lati)));
			params.add(new BasicNameValuePair("username", username));
			sh.makeServiceCall("http://"+SERVER_IP+"/RestfulProject/REST/WebService/StoreLocation", ServiceHandler.POST, params);*/
            return null;
        }

        public class VeggsterLocationListener implements LocationListener, android.location.LocationListener {

            @Override
            public void onLocationChanged(Location location) {

                int lat = (int) location.getLatitude(); // * 1E6);
                int log = (int) location.getLongitude(); // * 1E6);
                int acc = (int) (location.getAccuracy());

                String info = location.getProvider();
                try {

                    // LocatorService.myLatitude=location.getLatitude();

                    // LocatorService.myLongitude=location.getLongitude();

                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }

    }
private class storeLocation extends AsyncTask<ArrayList<String>, Void, Void> {
	ProgressDialog progDailog = null;
		@Override
		protected void onPreExecute() {
			progDailog = new ProgressDialog(MainDriverActivity.this);
			progDailog.setMessage("Backend Update...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(true);
            progDailog.show();
		};
		@Override
		protected Void doInBackground(ArrayList<String>... arg0) {
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			ArrayList<String> passed= arg0[0];
			params.add(new BasicNameValuePair("longitude", passed.get(0)));
			params.add(new BasicNameValuePair("latitude", passed.get(1)));
			params.add(new BasicNameValuePair("username", passed.get(2)));
			ServiceHandler sh=new ServiceHandler();
			String url="http://"+SERVER_IP+"/RestfulProject/REST/WebService/StoreLocation";
			String response=sh.makeServiceCall(url, ServiceHandler.POST, params);
			if (response.equals("ok"))
			{
				
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			progDailog.dismiss();
		}
		
	}

}
