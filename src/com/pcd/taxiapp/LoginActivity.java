package com.pcd.taxiapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.EditText;
import android.widget.TextView;
import static com.pcd.taxiapp.MainActivity.LOGIN_TYPE;
import static com.pcd.taxiapp.MainActivity.SERVER_IP;
public class LoginActivity extends ActionBarActivity {

	private String url="http://"+SERVER_IP+"/RestfulProject/REST/WebService/Login";
	private List<NameValuePair> params=new ArrayList<NameValuePair>();
	private ProgressDialog pDialog;
	private AsyncTask<String, String, String> execute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}



	}
	public void signin(View view) {
		new AttemptLogin().execute();
		/* ServiceHandler sh = new ServiceHandler();
		String response=sh.makeServiceCall(url, ServiceHandler.POST, params);
		if (response=="success") {

			Intent intent= new Intent(this,MainActivity.class);
			startActivity(intent);
		}
		else {
			TextView wrong= (TextView) findViewById(R.id.wrong);
			wrong.append("wrong password");
		} */

	}
	private class AttemptLogin extends AsyncTask<Void, Void, Void> {
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Attempting login..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			int success;
			try {
				EditText user= (EditText) findViewById(R.id.username);
				EditText pass= (EditText) findViewById(R.id.password);
				String username = user.getText().toString();
				String password = pass.getText().toString();
				params.add(new BasicNameValuePair("username",username.toString()));
				params.add(new BasicNameValuePair("password",password.toString()));
				params.add(new BasicNameValuePair("LOGIN_TYPE",LOGIN_TYPE));
				Log.d("request", "starting...");
				ServiceHandler sh = new ServiceHandler();
				String response=sh.makeServiceCall(url, ServiceHandler.POST, params);
				if (response!="fail") {
					pDialog.dismiss();
					SessionManager ssmgr= new SessionManager(getApplicationContext());
					ssmgr.createLoginSession(username, LOGIN_TYPE);
					/*SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
					Editor editor = pref.edit();
					editor.putString("username", username);
					editor.commit();
					if (response!="NOT_REGISTRED") {
						editor.putString("regid", response);
					}*/
					if(LOGIN_TYPE.equals("client")){
						Intent intent= new Intent(LoginActivity.this,MainActivity.class);
						//intent.putExtra(com.pcd.taxiapp.MainActivity, response);
						startActivity(intent);
					}
					else if(LOGIN_TYPE.equals("conducteur")) {
						Intent intent= new Intent(LoginActivity.this,MainDriverActivity.class);
						//intent.putExtra(com.pcd.taxiapp.MainActivity, response);
						intent.putExtra("D_USERNAME", username);
						startActivity(intent);
					}
				}
				else {
					pDialog.dismiss();
					Log.d("Login Failure !", response);
					TextView wrong= (TextView) findViewById(R.id.wrong);
					wrong.append("wrong password");

				}

			} catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			//dismiss the dialog once product deleted
			//pDialog.dismiss();

		}	
	}

	public void signup(View view) {
		if (LOGIN_TYPE=="conducteur") {
			Intent intent= new Intent(this,SignUpActivity.class);
			startActivity(intent);
		}
		else if (LOGIN_TYPE=="client"){
			Intent intent= new Intent(this,ClientSignUpActivity.class);
			startActivity(intent);
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
		}
	}

}
