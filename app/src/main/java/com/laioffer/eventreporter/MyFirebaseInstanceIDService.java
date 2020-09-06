package com.laioffer.eventreporter;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
       // Get updated InstanceID token
       String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       Log.d(TAG, "Refreshed token:" + refreshedToken);

       // If you want tot send messages to this application instance or
       // Manage this apps subscriptions on the server side, send the
       // Instance ID token to your app server.
       sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {

    }
}
