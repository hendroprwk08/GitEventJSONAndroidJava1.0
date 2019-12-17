package com.hendropurwoko.eventmanagement;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hide();

        pb = (ProgressBar) findViewById(R.id.pb_login);

        etUsername = (EditText) findViewById(R.id.login_et_email);
        etPassword = (EditText) findViewById(R.id.login_et_password);

        final Button btLogin = (Button) findViewById(R.id.login_bt_login);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainAppActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //close login activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();

                getApplicationContext().startActivity(i);
            }
        });

        final Button btSignUp = (Button) findViewById(R.id.login_bt_sign_up);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(etUsername.getText().toString().trim(),
                        etPassword.getText().toString().trim());


            }
        });

    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    void login(final String us, String pw){
        pb.setVisibility(ProgressBar.VISIBLE);

        try {
            String url = Cons.BASE_URL + "login.php?action=1" +
                    "&us=" + URLEncoder.encode(us, "utf-8") +
                    "&pw=" + URLEncoder.encode(pw, "utf-8");

            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Save: ", response.toString());

                            String email = null, type = null, username = null, status = response.optString("status").trim();

                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = response.getJSONArray("result");

                                    if (jsonArray.length() != 0) {
                                        email = response.optString("status").trim();
                                        type = response.optString("status").trim();
                                        username = response.optString("status").trim();
                                    }

                                    if (status.equals("0")){
                                        Toast.makeText(getBaseContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                                    }else if (status.equals("1")){
                                        saveSharedPreferences(email, type, username);

                                        //buka main
                                        Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(i);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                pb.setVisibility(ProgressBar.GONE);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d("Events: ", error.toString());

                    pb.setVisibility(ProgressBar.GONE);

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) { //time out or there is no connection
                        Toast.makeText(getBaseContext(), R.string.time_out_try_again, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) { //there was an Authentication Failure
                        Toast.makeText(getBaseContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) { //server responded with a error response
                        Toast.makeText(getBaseContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {//network error while performing the request
                        Toast.makeText(getBaseContext(), R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {//the server response could not be parsed
                        Toast.makeText(getBaseContext(), R.string.reading_data_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            queue.add(jsObjRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            String stackTrace = Log.getStackTraceString(e);

            Toast.makeText(getBaseContext(),
                    stackTrace,
                    Toast.LENGTH_SHORT).show();

            pb.setVisibility(ProgressBar.GONE);
        }
    }

    private void saveSharedPreferences(String email, String type, String username) {
        SharedPreferences mSharedPreferences = getSharedPreferences("em", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("sp_email", email);
        mEditor.putString("sp_type", type);
        mEditor.putString("sp_username", username);
        mEditor.commit();
        mEditor.apply();
    }
}
