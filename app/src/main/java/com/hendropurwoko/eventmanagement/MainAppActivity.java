package com.hendropurwoko.eventmanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainAppActivity extends AppCompatActivity {

    public static Context MAIN_CONTEXT;
    public static String ACTIVE_FRAGMENT, ACTION;
    public static Fragment LAST_FRAGMENT;
    public static View MAIN_VIEW;

    public static BottomAppBar bottomAppBar;
    public static FloatingActionButton fab;
    TextView tvTitle;

    Fragment fragment = null;

    public MainAppActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        MAIN_CONTEXT = getApplicationContext();
        MAIN_VIEW = getWindow().getDecorView().getRootView();

        //bottom app bar
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        //bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        bottomAppBar.replaceMenu(R.menu.menu_item);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // Handle presses on the action bar items
                switch (item.getItemId()) {
                    case R.id.menu_registration:
                        Cons.LAST_FRAGMENT = ACTIVE_FRAGMENT;
                        fragment = new RegistrationFragment();
                        loadFragment(fragment);
                        ACTIVE_FRAGMENT = "registration";
                        hideBar();
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
                        LAST_FRAGMENT = fragment;
                        fragment = new ParticipantFragment();
                        loadFragment(fragment);
                        ACTIVE_FRAGMENT = "participant";
                        return true;
                    case R.id.menu_profile:
                        LAST_FRAGMENT = fragment;
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        ACTIVE_FRAGMENT = "profile";
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
                Log.d(Cons.TAG, "onClick: " + ACTIVE_FRAGMENT);

                if (bottomAppBar.getFabAlignmentMode() == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER){
                    if (ACTIVE_FRAGMENT.equals("event")) {
                        fragment = new EventFormFragment("add");
                    }else if (ACTIVE_FRAGMENT.equals("user")) {
                        fragment = new UserFormFragment("add");
                    }else if (ACTIVE_FRAGMENT.equals("participant")) {
                        fragment = new ParticipantFormFragment("add");
                    }else if (ACTIVE_FRAGMENT.equals("registration")) {
                        fragment = new RegistrationFragment();
                    }

                    loadFragment(fragment);

                    hideBar();

                }else{
                    if (ACTIVE_FRAGMENT.equals("event")) {
                        fragment = new EventFragment();
                    }else if (ACTIVE_FRAGMENT.equals("user")) {
                        fragment = new UserFragment();
                    }else if (ACTIVE_FRAGMENT.equals("participant")) {
                        fragment = new ParticipantFragment();
                    }else if (ACTIVE_FRAGMENT.equals("registration")) {
                        if (Cons.LAST_FRAGMENT == "user") {
                            fragment = new UserFragment();
                        }else if (Cons.LAST_FRAGMENT ==  "participant") {
                            fragment = new ParticipantFragment();
                        }else {
                            fragment = new EventFragment();
                        }
                    }

                    loadFragment(fragment);

                    showBar();
               }

                ACTION = null;
            }
        });

        hide();

        // kita set default nya Home Fragment
        loadFragment(new ParticipantFragment());
        ACTIVE_FRAGMENT = "participant";
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

    public static void hideBar() {
        //slide down bottomappbar
        Animation moveDown = AnimationUtils.loadAnimation(MAIN_CONTEXT, R.anim.move_down);
        bottomAppBar.startAnimation(moveDown);

        //replace icon fab to arrow and end position fab
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        fab.setImageResource(R.drawable.ic_keyboard_arrow_left_white_24dp);
    }

    public static void showBar(){
        //slide up bottomappbar
        Animation moveUp = AnimationUtils.loadAnimation(MAIN_CONTEXT, R.anim.move_up);
        bottomAppBar.startAnimation(moveUp);

        //replace icon fab to arrow and center fab
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        fab.setImageResource(R.drawable.ic_add_white_24dp);
    }
}
