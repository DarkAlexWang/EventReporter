package com.laioffer.eventreporter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
import android.util.Log;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity implements
    EventFragment.OnItemSelectListener {

    private EventFragment mListFragment;
    private CommentFragment mGridFragment;

    @Override
    public void onItemSelected(int position) {
        mGridFragment.onItemSelected(position);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Show different fragments based on screen size.
       // if (findViewById(R.id.fragment_container) != null) {
       // Fragment fragment = isTablet() ? new CommentFragment() : new EventFragment();
       // getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();

        // add list view
        mListFragment = new EventFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.event_container,
            mListFragment).commit();

        // add Grid view
        mGridFragment = new CommentFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.comment_container,
            mGridFragment).commit();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout &
            Configuration.SCREENLAYOUT_SIZE_MASK) >=
            Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
        //xml boolean value depends on screen size


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Life cycle test", "We are at onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Life cycle test", "We are at onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //important
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(isChangingConfigurations() && fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit(); }
        Log.e("Life cycle test", "We are at onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Life cycle test", "We are at onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Life cycle test", "We are at onDestroy()");
    }

}