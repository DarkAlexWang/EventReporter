package com.laioffer.eventreporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class EventReportActivity extends AppCompatActivity {
    private static final String TAG = EventReportActivity.class.getSimpleName();
    private EditText mEditTextLocation;
    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private ImageView mImageViewLocation;
    private ImageView mImageViewSend;
    private ImageView mImageViewCamera;
    private DatabaseReference database;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private LocationTracker mLocationTracker;
    private Activity mActivity;

    // Set variables ready for picking images
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView img_event_picture;
    private Uri mImgUri;

    // Set variables ready for uploading images
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_report);


        // Firebase Authentication
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

        // Initialize cloud storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mEditTextLocation = (EditText) findViewById(R.id.edit_text_event_location);
        mEditTextTitle = (EditText) findViewById(R.id.edit_text_event_title);
        mEditTextContent = (EditText) findViewById(R.id.edit_text_event_content);
        mImageViewCamera = (ImageView) findViewById(R.id.img_event_camera);
        mImageViewSend = (ImageView) findViewById(R.id.img_event_report);
        mImageViewLocation = (ImageView) findViewById(R.id.img_event_location);
        database = FirebaseDatabase.getInstance().getReference();
        img_event_picture = (ImageView) findViewById(R.id.img_event_picture_capture);


        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = uploadEvent();
                if (mImgUri != null) {
                    uploadImage(key);
                    mImgUri = null;
                }
            }
        });

        //Add click listener for the image to pick up images from gallery through implicit intent
        mImageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        // check if GPS enabled
        mLocationTracker = new LocationTracker(this);
        mLocationTracker.getLocation();
        final double latitude = mLocationTracker.getLatitude();
        final double longitude = mLocationTracker.getLongitude();

        // location image click
        mImageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEditTextLocation.getText())) {
                    //if mEditTestLocation is not empty, then clear the content
                    // !! getTExt() return Editable
                    mEditTextLocation.setText("");
                } else { // if mEditTestLocation is empty, then get auto-location
                    new AsyncTask<Void, Void, Void>() {
                        private List<String> mAddressList = new ArrayList<String>();

                        @Override
                        protected Void doInBackground(Void... urls) {
                            mAddressList = mLocationTracker.getCurrentLocationViaJSON(latitude, longitude);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void input) {
                            if (mAddressList.size() >= 3) {
                                mEditTextLocation.setText(mAddressList.get(0) + ", " + mAddressList.get(1) +
                                    ", " + mAddressList.get(2) + ", " + mAddressList.get(3));
                            }
                        }
                    }.execute();
                }
            }
        });
    }

    /**
     * Gather information inserted by user and create event for uploading.
     * Then clear those widgets if user uploads one
     * @return the key of the event needs to be returned as link against Cloud storage
     * */
        private String uploadEvent() {
            String title = mEditTextTitle.getText().toString();
            String location = mEditTextLocation.getText().toString();
            String description = mEditTextContent.getText().toString();
            if (location.equals("") || description.equals("") ||
                title.equals("") || Utils.username == null) {
                return null;
            }
            //create event instance (without set imgUri)
            Event event = new Event();
            event.setTitle(title);
            event.setAddress(location);
            event.setDescription(description);
            event.setTime(System.currentTimeMillis());
            event.setUsername(Utils.username);
            // insert data
            String key = database.child("events").push().getKey();
            // 1. get reference to events node(collection) with child()
            // 2. create an empty new node in events node with a unique key by push() or  by specific attribute
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
                        img_event_picture.setImageDrawable(null);
                        img_event_picture.setVisibility(View.GONE);
                    }
                }
            });
            return key;
        }


        /**
         * Send Intent to launch gallery for us to pick up images, once the action finishes, images
         * will be returns as parameters in this function
         * @param requestCode code for intent to start gallery activity
         * @param resultCode result code returned when finishing picking up images from gallery
         * @param data content returned from gallery, including images we picked
         */
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    img_event_picture.setVisibility(View.VISIBLE);
                    img_event_picture.setImageURI(selectedImage);
                    mImgUri = selectedImage;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    /**
     * Upload image picked up from gallery to Firebase cloud storage
     * @param eventId eventId
     */
    private void uploadImage(final String eventId) {
        if (mImgUri == null) {
            return;
        }
        final StorageReference imgRef = storageRef.child("images/" + mImgUri.getLastPathSegment() + "_"
                + System.currentTimeMillis()); // set unique id for each image -> folder + path + time

        UploadTask uploadTask = imgRef.putFile(mImgUri); // 1: upload to cloud storage

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Task<Uri> downloadUrl = imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    // Old .getDownloadUrl is depreciated, and return a Task<uri>
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i(TAG, "Upload Successfully" + eventId);
                        database.child("events").child(eventId).child("imgUri").
                            setValue(uri.toString()); //2: upload image url to database
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       e.printStackTrace();
                    }
                });
            }
        });
    }

    // Add authtification Listener when activity starts
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
