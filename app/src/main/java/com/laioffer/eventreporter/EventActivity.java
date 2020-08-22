package com.laioffer.eventreporter;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventActivity extends AppCompatActivity {
    private Fragment mEventsFragment;
    private Fragment mEventMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (mEventsFragment == null) {
            mEventsFragment = new EventsFragment();
        }
        // default show events list
        getSupportFragmentManager().beginTransaction().add(R.id.relativelayout_event, mEventsFragment).commit(); // add fragment to activity

        // lazy loading(when using the fragment map, we then load it)
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        // Set Item click listener to the menu items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_event_list:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout_event, mEventsFragment).commit();
                        break;
                    case R.id.action_event_map:
                        if (mEventMapFragment == null) {
                            mEventMapFragment = new Fragment();
                        }
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout_event, mEventMapFragment).commit();
                }
                return false;
            }
        });
    }
}