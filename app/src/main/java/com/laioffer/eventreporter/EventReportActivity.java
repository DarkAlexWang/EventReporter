package com.laioffer.eventreporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventReportActivity extends AppCompatActivity {
    private static final String TAG = EventReportActivity.class.getSimpleName();
    private EditText mEditTextLocation;
    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private ImageView mImageViewSend;
    private ImageView mImageViewCamera;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_report);

        // Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:singed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:singed_out");
                }
            }
        };

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInAnoymously", task.getException());
                }
            }
        });

        mEditTextLocation = (EditText) findViewById(R.id.edit_text_event_location);
        mEditTextTitle = (EditText) findViewById(R.id.edit_text_event_title);
        mEditTextContent = (EditText) findViewById(R.id.edit_text_event_content);
        mImageViewCamera = (ImageView) findViewById(R.id.img_event_camera);
        mImageViewSend = (ImageView) findViewById(R.id.img_event_report);
        database = FirebaseDatabase.getInstance().getReference();

        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = uploadEvent();
            }
        });
    }

        /**
         * Gather information inserted by user and create event for uploading. Then clear those widgets if user uploads one
         * @return the key of the event needs to be returned as link against Cloud storage */
        private String uploadEvent() {
            String title = mEditTextTitle.getText().toString();
            String location = mEditTextLocation.getText().toString();
            String description = mEditTextContent.getText().toString();
            if (location.equals("") || description.equals("") ||
                title.equals("") || Utils.username == null) {
                return null;
            }
            //create event instance
            Event event = new Event();
            event.setTitle(title);
            event.setAddress(location);
            event.setDescription(description);
            event.setTime(System.currentTimeMillis());
            event.setUsername(Utils.username);
            String key = database.child("events").push().getKey();
            event.setId(key);
            database.child("events").child(key).setValue(event, new
                DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast toast = Toast.makeText(getBaseContext(),
                            "The event is failed, please check your network status.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getBaseContext(), "The event is reported", Toast.LENGTH_SHORT);
                        toast.show();
                        mEditTextTitle.setText("");
                        mEditTextLocation.setText("");
                        mEditTextContent.setText("");
                    }
                }
            });
            return key;
        }
        @Override
        public void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }
        @Override
        public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }
}
