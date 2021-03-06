package com.naqelexpress.naqelpointer.DB;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
       // FirebaseHandler.DeviceID=token;
       // FirebaseHandler.getInstance().Initialize(token);
        // Add custom implementation, as needed.

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //preferences.edit().putString(SyncStateContract.Constants.FIREBASE_TOKEN, token).apply();
    }
}