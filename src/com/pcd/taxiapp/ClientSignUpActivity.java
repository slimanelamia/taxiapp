package com.pcd.taxiapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Build;
import static com.pcd.taxiapp.MainActivity.LOGIN_TYPE;
import static com.pcd.taxiapp.MainActivity.SERVER_IP;
public class ClientSignUpActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_sign_up);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	public void register(View view) throws IOException {
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		EditText lastname=(EditText) findViewById(R.id.lastname);
		EditText firstname=(EditText) findViewById(R.id.firstname);
		EditText usrname=(EditText) findViewById(R.id.username);
		EditText pass=(EditText) findViewById(R.id.password);
		//EditText inputvoiture=(EditText) findViewById(R.id.voiture);
		String nom= lastname.getText().toString();
		String prenom= firstname.getText().toString();
		String username= usrname.toString();
		String password= pass.toString();
		//String voiture= inputvoiture.getText().toString();
		params.add(new BasicNameValuePair("lastname",nom));
		params.add(new BasicNameValuePair("firstname",prenom));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password",password));
		params.add(new BasicNameValuePair("LOGIN_TYPE",LOGIN_TYPE));
		String urladr="http://"+SERVER_IP+"/RestfulProject/REST/WebService/Add";
		ServiceHandler sh=new ServiceHandler();
		sh.makeServiceCall(urladr, ServiceHandler.POST, params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client_sign_up, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_client_sign_up,
					container, false);
			return rootView;
		}
	}

}
