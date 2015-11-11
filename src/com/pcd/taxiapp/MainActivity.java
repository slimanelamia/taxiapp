package com.pcd.taxiapp;

import java.io.IOException;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
//import static com.pcd.taxiapp.HomeActivity.LOGIN_TYPE;
//import static com.pcd.taxiapp.HomeActivity.SERVER_IP;

public class MainActivity extends ActionBarActivity {
	public static String LOGIN_TYPE;
	public static String SERVER_IP="192.168.1.7";
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "regid";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "1072024966813";
    static final String TAG = "GCM Demo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		SessionManager ssmgr= new SessionManager(getApplicationContext());
		//Intent intent= new Intent(MainActivity.this,HomeActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//startActivity(intent);
		//if(!ssmgr.checkLogin()){
			//Intent intent= new Intent(MainActivity.this,HomeActivity.class);
			//startActivity(intent);
			//}
		HashMap<String, String> user=ssmgr.getUserDetails();
		String Login_type=user.get("KEY_LOGIN_TYPE");
		String username=user.get("KEY_USERNAME");
		
		/*SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		username=pref.getString("username", null);
		if (username.isEmpty()) {
			Intent intent= new Intent(MainActivity.this,HomeActivity.class);
			this.startActivity(intent);
		}*/
		mDisplay = (TextView) findViewById(R.id.mdisplay);
		//mDisplay.append("username: "+username);
        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            Log.i(TAG, regid);
            System.out.print(regid);
            if (regid.isEmpty()) {
            	System.out.print("registring on background...");
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        if (Login_type.equals("Client")){
        	
        }
        
	}
        
	 private String getRegistrationId(Context context) {
		 final SharedPreferences prefs = getGcmPreferences(context);
	        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	        if (registrationId.isEmpty()) {
	            Log.i(TAG, "Registration not found.");
	            return "";
	        }
	        // Check if app was updated; if so, it must clear the registration ID
	        // since the existing regID is not guaranteed to work with the new
	        // app version.
	        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(TAG, "App version changed.");
	            return "";
	        }
	        return registrationId;
		
	}
	private int getAppVersion(Context context) {
		 try {
	            PackageInfo packageInfo = context.getPackageManager()
	                    .getPackageInfo(context.getPackageName(), 0);
	            return packageInfo.versionCode;
	        } catch (NameNotFoundException e) {
	            // should never happen
	            throw new RuntimeException("Could not get package name: " + e);
	        }
	}
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
	}
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
	}
	private void registerInBackground() {
	        new AsyncTask<Void, Void, String>() {
	            @Override
	            protected String doInBackground(Void... params) {
	                String msg = "";
	                try {
	                    if (gcm == null) {
	                        gcm = GoogleCloudMessaging.getInstance(context);
	                    }
	                    
	                    regid = gcm.register(SENDER_ID);
	                    msg = "Device registered, registration ID=" + regid;
	                    System.out.print(regid);

	                    // You should send the registration ID to your server over HTTP, so it
	                    // can use GCM/HTTP or CCS to send messages to your app.
	                    sendRegistrationIdToBackend(regid);

	                    // For this demo: we don't need to send it because the device will send
	                    // upstream messages to a server that echo back the message using the
	                    // 'from' address in the message.

	                    // Persist the regID - no need to register again.
	                    storeRegistrationId(context, regid);
	                } catch (IOException ex) {
	                    msg = "Error :" + ex.getMessage();
	                    // If there is an error, don't just keep trying to register.
	                    // Require the user to click a button again, or perform
	                    // exponential back-off.
	                }
	                return msg;
	            }

	            

				@Override
	            protected void onPostExecute(String msg) {
	               // mDisplay.append(msg);
	            }
	        }.execute(null, null, null);
	    }

	/*public void signup(View view) {
		Intent intent= new Intent(this,SignUpActivity.class);
		startActivity(intent);}*/
	
	public void location(View view) {
		Intent intent=new Intent(this,GetDriverActivity.class);
		startActivity(intent);
	}
	public void taxi_search(View view) {
		Intent intent=new Intent(this,NotifyActivity.class);
		startActivity(intent);
	}
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
		
	}

	private void sendRegistrationIdToBackend(String regid) {
		String urladr="http://"+SERVER_IP+"/RestfulProject/REST/WebService/StoreRegID/"+username+"/"+regid+"/"+LOGIN_TYPE;
		ServiceHandler sh=new ServiceHandler();
		sh.makeServiceCall(urladr, ServiceHandler.POST);
		
	}
	

	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
