package com.pcd.taxiapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import static com.pcd.taxiapp.MainActivity.LOGIN_TYPE;
import static com.pcd.taxiapp.MainActivity.SERVER_IP;
public class SignUpActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	public void register(View view) throws IOException {
		
		EditText inputnom=(EditText) findViewById(R.id.nom);
		EditText inputprenom=(EditText) findViewById(R.id.prenom);
		EditText inputvoiture=(EditText) findViewById(R.id.voiture);
		EditText usrname=(EditText) findViewById(R.id.username);
		EditText pass=(EditText) findViewById(R.id.password);
		String nom= inputnom.getText().toString();
		String prenom= inputprenom.getText().toString();
		String voiture= inputvoiture.getText().toString();
		String username= usrname.toString();
		String password= pass.toString();
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("lastname",nom));
		params.add(new BasicNameValuePair("firstname",prenom));
		params.add(new BasicNameValuePair("voiture",voiture));
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
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_sign_up,
					container, false);
			return rootView;
		}
	}

}
