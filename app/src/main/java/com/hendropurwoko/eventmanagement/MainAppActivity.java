package com.hendropurwoko.eventmanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainAppActivity extends AppCompatActivity {

    public static Context MAIN_CONTEXT;
    public static String ACTIVE_FRAGMENT;
    public static View MAIN_VIEW;

    BottomAppBar bottomAppBar;
    FloatingActionButton fab;

    Fragment fragment = null;

    /* private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_events:
                    fragment = new EventFragment();
                    ACTIVE_FRAGMENT = "event";
                    break;
                case R.id.navigation_participants:
                    fragment = new ParticipantFragment();
                    ACTIVE_FRAGMENT = "participant";
                    break;
                case R.id.navigation_users:
                    fragment = new UserFragment();
                    ACTIVE_FRAGMENT = "user";
                    break;
            }

            return loadFragment(fragment);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        MAIN_CONTEXT = getApplicationContext();
        MAIN_VIEW = getWindow().getDecorView().getRootView();

        //bottom app bar
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        //bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        bottomAppBar.replaceMenu(R.menu.menu_item);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // Handle presses on the action bar items
                switch (item.getItemId()) {
                    case R.id.menu_registration:
                        return true;
                    case R.id.menu_event:
                        fragment = new EventFragment();
                        loadFragment(fragment);
                        ACTIVE_FRAGMENT = "event";
                        return true;
                    case R.id.menu_user:
                        fragment = new UserFragment();
                        loadFragment(fragment);
                        ACTIVE_FRAGMENT = "user";
                        return true;
                    case R.id.menu_participant:
                        fragment = new ParticipantFragment();
                        loadFragment(fragment);
                        ACTIVE_FRAGMENT = "participant";
                        return true;
                }

                return false;
            }
        });

        //fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Cons.TAG, "onClick: test");
            }
        });

        hide();

        //set bottom navigation
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // kita set default nya Home Fragment
        loadFragment(new EventFragment());
        ACTIVE_FRAGMENT = "events";

    }

    // method untuk load fragment yang sesuai
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
