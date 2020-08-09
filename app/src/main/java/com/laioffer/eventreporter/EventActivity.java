package com.laioffer.eventreporter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class EventActivity extends AppCompatActivity {
    private Fragment mEventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (mEventsFragment == null) {
            mEventsFragment = new EventsFragment();
        }
        //getSupportFragmentManager().beginTransaction();
        //    add(R.id.relativelayout_event, mEventsFragment).commit();
        switchToSecondFragment();
    }
    public void switchToSecondFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.relativelayout_event, mEventsFragment).addToBackStack(null);
        transaction.commit();
    }
}