package com.pcd.taxiapp;

import static com.pcd.taxiapp.MainActivity.SERVER_IP;
import static com.pcd.taxiapp.MainDriverActivity.d_username;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ResponseActivity extends ActionBarActivity {
	private ProgressDialog pDialog;
	String id_demande="";
	TextView alert;
	String response="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		id_demande=getIntent().getExtras().getString("DEMANDE_ID");
		alert=(TextView) findViewById(R.id.alert);
	}

	public void accept(View view) {
		new AcceptRequest().execute();
		/*String url="http://"+SERVER_IP+"/RestfulProject/REST/WebService/Accept/"+id_demande+"/"+d_username;
		ServiceHandler sh= new ServiceHandler();
		String response= sh.makeServiceCall(url, ServiceHandler.POST);
		if (!response.equals("ACCEPTED")){
			Intent intent=new Intent(this,MapTracerActivity.class);
			startActivity(intent);
		}
		else if(response.equals("ACCEPTED")){
			alert.append("Client already taken !");
		} */
	}

	private class AcceptRequest extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ResponseActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}
		@Override
		protected String doInBackground(Void... params) {
			String url="http://"+SERVER_IP+"/RestfulProject/REST/WebService/Accept/"+id_demande+"/"+d_username;
			ServiceHandler sh= new ServiceHandler();
			String response= sh.makeServiceCall(url, ServiceHandler.POST);
			if (!response.equals("ACCEPTED")){
				Intent intent=new Intent(ResponseActivity.this,GetRouteActivity.class);
				startActivity(intent);
			}
			/*if (response.equals("ACCEPTED")){
				pDialog.dismiss();
				//alert.append("Client already taken !");
				//alert.setText("Client already taken !");
				Toast.makeText(getApplicationContext(), "Client accepted by another driver !", Toast.LENGTH_LONG).show();
			}*/
			
			return response;
		}
		@Override
		protected void onPostExecute(String response) {
			// Dismiss the progress dialog
			pDialog.dismiss();
			TextView alert=(TextView) findViewById(R.id.alert);
			if (response.equals("ACCEPTED")){
				//alert.append("Client already taken !");
				alert.setText("Malheureusement un conducteur a déjà eu cette mission");
			}
		}

	}

	public void getLocation() {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude(), Toast.LENGTH_LONG).show();
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 1000, locationListener);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.response, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_response,
					container, false);
			return rootView;
		}
	}

}
