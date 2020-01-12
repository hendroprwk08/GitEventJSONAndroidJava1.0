package com.hendropurwoko.eventmanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainAppActivity extends AppCompatActivity {

    public static Context MAIN_CONTEXT;
    public static String ACTION;
    public static Fragment LAST_FRAGMENT;
    public static View MAIN_VIEW;

    public static BottomAppBar bottomAppBar;
    public static FloatingActionButton fab;

    boolean doubleBackToExitPressedOnce = false;

    public static Fragment fragment = null;

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
                    case R.id.menu_home:
                        Cons.LAST_FRAGMENT = Cons.ACTIVE_FRAGMENT;
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        Cons.ACTIVE_FRAGMENT = "home";
                        return true;
                }

                return false;
            }
        });
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show BottomSheetDialogFragment
                BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetNavigationFragment.newInstance();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });

        //fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Cons.TAG, "onClick: " + Cons.ACTIVE_FRAGMENT);

                if (bottomAppBar.getFabAlignmentMode() == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER){
                    if (Cons.ACTIVE_FRAGMENT.equals("event")) {
                        fragment = new EventFormFragment("add");
                    }else if (Cons.ACTIVE_FRAGMENT.equals("user")) {
                        fragment = new UserFormFragment("add");
                    }else if (Cons.ACTIVE_FRAGMENT.equals("home") || Cons.ACTIVE_FRAGMENT.equals("participant")) {
                        Cons.LAST_FRAGMENT = Cons.ACTIVE_FRAGMENT;
                        Toast.makeText(getBaseContext(), R.string.opening_web_page, Toast.LENGTH_SHORT).show();
                        fragment = new RegistrationFragment();
                    }

                    hideBar();

                    loadFragment(fragment);
                }else{
                    if (Cons.ACTIVE_FRAGMENT.equals("event")) {
                        fragment = new EventFragment();
                    }else if (Cons.ACTIVE_FRAGMENT.equals("user")) {
                        fragment = new UserFragment();
                    }else if (Cons.ACTIVE_FRAGMENT.equals("registration") ||
                            Cons.ACTIVE_FRAGMENT.equals("profile") ||
                            Cons.ACTIVE_FRAGMENT.equals("member") ||
                            Cons.ACTIVE_FRAGMENT.equals("participant") ||
                            Cons.ACTIVE_FRAGMENT.equals("home")){
                        if (Cons.LAST_FRAGMENT == "user") {
                            fragment = new UserFragment();
                        }else if (Cons.LAST_FRAGMENT ==  "participant") {
                            fragment = new ParticipantFragment();
                        }else if (Cons.LAST_FRAGMENT ==  "event") {
                            fragment = new EventFragment();
                        }else {
                            fragment = new HomeFragment();
                        }
                    }

                    showBar();

                    loadFragment(fragment);
               }

                ACTION = null;
            }
        });

        hide();

        // kita set default nya Home Fragment
        loadFragment(new HomeFragment());
        Cons.ACTIVE_FRAGMENT = "home";
    }

    // method untuk load fragment yang sesuai
    public void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
        };
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        showBar();
        loadFragment(new HomeFragment());

        Toast.makeText(this, R.string.press_back_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
