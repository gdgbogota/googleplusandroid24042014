package com.example.googleplussample;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;
import com.google.android.gms.plus.model.people.PersonBuffer;

public class MainActivity extends ActionBarActivity implements OnClickListener, com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult>  {
	
	/* Request code used to invoke sign in user interactions. */
	  private static final int RC_SIGN_IN = 0;

	  /* Client used to interact with Google APIs. */
	  private GoogleApiClient mGoogleApiClient;

	  /* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	  private boolean mIntentInProgress;
	  
	  /* Track whether the sign-in button has been clicked so that we know to resolve
	   * all issues preventing sign-in without waiting.
	   */
	  private boolean mSignInClicked;

	  /* Store the connection result from onConnectionFailed callbacks so that we can
	   * resolve them when the user clicks sign-in.
	   */
	  private com.google.android.gms.common.ConnectionResult mConnectionResult;
	  private com.google.android.gms.common.SignInButton button;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API, null)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.build();

		button= (SignInButton) findViewById(R.id.sign_in_button);
		button.setOnClickListener(this);
		
	}
	
	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
	  if (mConnectionResult.hasResolution()) {
	    try {
	      mIntentInProgress = true;
	      //startIntentSenderForResult(mConnectionResult.getIntentSender(),
	      //    RC_SIGN_IN, null, 0, 0, 0);
	      mConnectionResult.startResolutionForResult(this, // your activity
                  RC_SIGN_IN);
	    } catch (SendIntentException e) {
	      // The intent was canceled before it was sent.  Return to the default
	      // state and attempt to connect to get an updated ConnectionResult.
	      mIntentInProgress = false;
	      mGoogleApiClient.connect();
	    }
	  }
	}
	
	protected void onStart() {
	    super.onStart();
	    mGoogleApiClient.connect();
	  }

	  protected void onStop() {
	    super.onStop();

	    if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	  }



	@Override
	public void onConnectionFailed(com.google.android.gms.common.ConnectionResult result) {
		// TODO Auto-generated method stub
		
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent data) {
		// TODO Auto-generated method stub
		
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress =false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
		

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.sign_in_button
				&& !mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}


		
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
		 Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
	      .setResultCallback(this);
		 
		 if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			 Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			 String personName = currentPerson.getDisplayName();
			 Image personPhoto = currentPerson.getImage();
			 String personGooglePlusProfile = currentPerson.getUrl();
			 
			 Toast.makeText(this, "Resulttado :"+personName+"\n"+" Perfil :"+personGooglePlusProfile , Toast.LENGTH_LONG).show();
			 
			 
		 }
		 
		mSignInClicked = false;
		  Toast.makeText(this, "Usuario conectado!", Toast.LENGTH_LONG).show();
		
	}



	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
		
	}

	@Override
	public void onResult(People.LoadPeopleResult peopleData) {
		// TODO Auto-generated method stub
		if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
			PersonBuffer personBuffer = peopleData.getPersonBuffer();
			try {
				int count = personBuffer.getCount();
				for (int i = 0; i < count; i++) {
					Log.d("Resultado INFORMACION", "Display name: " + personBuffer.get(i).getDisplayName());
				}
			} finally {
				personBuffer.close();
			}
		} else {
			Log.e("ERROR", "Error requesting visible circles: " + peopleData.getStatus());
		}
		
	}


}
