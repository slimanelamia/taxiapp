package com.pcd.taxiapp;

import static com.pcd.taxiapp.MainActivity.SERVER_IP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
public class MapTracerActivity extends ActionBarActivity {
	private GoogleMap map;
	private String depart;
	private String destination;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_tracer);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		depart=(String) getIntent().getExtras().getString("depart");
		destination=(String) getIntent().getExtras().getString("destination");
		new GetRoute().execute();
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mymap)).getMap();
	}

	private class GetRoute extends AsyncTask<Void, Void, ArrayList<HashMap<String,String>>> {
		private CameraUpdate center;
		private CameraUpdate zoom;
		private ArrayList<HashMap<String,List>> mark=new ArrayList<HashMap<String,List>>();
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			ServiceHandler sh=new ServiceHandler();
			ArrayList<HashMap<String, String>> route = new ArrayList<HashMap<String,String>>();
			String url="http://"+SERVER_IP+"/RestfulProject/REST/WebService/GetRoute/Place/Highway/5/1";
			String jStr=(String) sh.makeServiceCall(url, ServiceHandler.GET);
			if (jStr!=null){
				try {
					JSONArray jsonA= new JSONArray(jStr);
					
					for (int i=0; i <jsonA.length();i++) {
						JSONObject point=new JSONObject();
						point=jsonA.getJSONObject(i);
						String lat=point.getString("lat");
						String lng=point.getString("lng");
						String warning=point.getString("warning");
						HashMap<String, String> latlng=new HashMap<String, String>();
						latlng.put("lat", lat);
						latlng.put("lng", lng);
						double lati=Double.parseDouble(lat);
						double lngi=Double.parseDouble(lng);
						LatLng mposition= new LatLng(lati, lngi);
						HashMap<String, List> mitem=new HashMap<String, List>();
						if (!warning.equals("NO_WARNING")){
							List<Object> values=new ArrayList<Object>();
							values.add(warning);
							values.add(mposition);
							mitem.put("wr", values);
							mark.add(mitem);
							/*map.addMarker(new MarkerOptions()
					        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
					        .title("Warning !")
					        .snippet(warning));*/
							}
						
						
						route.add(latlng);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			return route;
		}
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			ArrayList<LatLng> points=new ArrayList<LatLng>();
			MarkerOptions markerOptions= new MarkerOptions();
			PolylineOptions lineOptions= new PolylineOptions();
			for(int i=0;i<result.size();i++){
				HashMap<String,String> step=result.get(i);
				double lat=Double.parseDouble(step.get("lat"));
				double lng=Double.parseDouble(step.get("lng"));
				LatLng position= new LatLng(lat, lng);
				points.add(position);
				if (i==0) {
					Marker source=map.addMarker(new MarkerOptions()
			        .position(position)
			        .title("Source")
			        .snippet("adresse source")
			        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
					source.showInfoWindow();
				}
				if (i==result.size()-1) {
					Marker destination=map.addMarker(new MarkerOptions()
			        .position(position)
			        .title("Destination")
			        .snippet("adresse destination")
			        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
					destination.showInfoWindow();
				}
			}
			for (int i=0;i<mark.size();i++) {
				HashMap<String,List> mstep=mark.get(i);
				String warning=(String) mstep.get("wr").get(0);
				LatLng position=(LatLng) mstep.get("wr").get(1);
				Marker wrn=map.addMarker(new MarkerOptions()
			       .position(position)
			       .title("Warning !")
			       .snippet(warning)
			       .icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)));
				wrn.showInfoWindow();
			}
			lineOptions.addAll(points);
			lineOptions.width(9);
			lineOptions.color(Color.GREEN);
			//map.moveCamera(center);
			//map.animateCamera(zoom);
			map.addPolyline(lineOptions);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_tracer, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_map_tracer,
					container, false);
			return rootView;
		}
	}

}
