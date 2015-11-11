package com.pcd.taxiapp;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GetDriverActivity extends ListActivity {

	private ProgressDialog pDialog;
	private InetAddress IP;
	// URL to get contacts JSON
	private static String url = "http://192.168.43.49/RestfulProject/REST/WebService/GetFeeds";
	
	// JSON Node names
	
	private static final String TAG_NOM = "nom";
	private static final String TAG_PRENOM = "prenom";
	private static final String TAG_VOITURE = "voiture";


	// contacts JSONArray
	JSONArray drivers = null;

	// Hashmap for ListView
	ArrayList<HashMap<String, String>> driverList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_driver);

		driverList = new ArrayList<HashMap<String, String>>();

		ListView lv = getListView();

		// Listview on item click listener
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String nom = ((TextView) view.findViewById(R.id.nom))
						.getText().toString();
				String prenom = ((TextView) view.findViewById(R.id.prenom))
						.getText().toString();
				String voiture = ((TextView) view.findViewById(R.id.voiture))
						.getText().toString();

				// Starting single contact activity
				Intent in = new Intent(getApplicationContext(),
						SingleContactActivity.class);
				in.putExtra(TAG_NOM, nom);
				in.putExtra(TAG_PRENOM, prenom);
				in.putExtra(TAG_VOITURE, voiture);
				startActivity(in);

			}
		});

		// Calling async task to get json
		new GetDrivers().execute();
	}

	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class GetDrivers extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(GetDriverActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			try {
				IP=InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
			//Toast toast = Toast.makeText(getApplicationContext(), url, 5);
			//toast.show();

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONArray jsonObj = new JSONArray(jsonStr);
					
					// Getting JSON Array node
					drivers = jsonObj;

					// looping through All Contacts
					for (int i = 0; i < drivers.length(); i++) {
						JSONObject c = drivers.getJSONObject(i);
						
						
						String nom = c.getString(TAG_NOM);
						String prenom = c.getString(TAG_PRENOM);
						String voiture = c.getString(TAG_VOITURE);
						

						

						// tmp hashmap for single contact
						HashMap<String, String> driver = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						
						driver.put(TAG_NOM, nom);
						driver.put(TAG_PRENOM, prenom);
						driver.put(TAG_VOITURE, voiture);
						

						// adding contact to contact list
						driverList.add(driver);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */
			ListAdapter adapter = new SimpleAdapter(
					GetDriverActivity.this, driverList,
					R.layout.list_item, new String[] { TAG_NOM, TAG_PRENOM,
							TAG_VOITURE }, new int[] { R.id.nom,
							R.id.prenom, R.id.voiture });

			setListAdapter(adapter);
		}

	}

}