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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.pb) ProgressBar pb;
    @BindView(R.id.login_et_email) EditText etLoginEmail;
    @BindView(R.id.login_et_password) EditText etPassword;
    @BindView(R.id.regis_et_email) EditText etRegisEmail;
    @BindView(R.id.regis_et_password) EditText etRegisPassword;
    @BindView(R.id.veri_et_vericode) EditText etVerification;
    @BindView(R.id.ll_login) LinearLayout llLogin;
    @BindView(R.id.ll_registration) LinearLayout llRegistration;
    @BindView(R.id.ll_verification) LinearLayout llVerification;
    @BindView(R.id.login_bt_login) Button btLogin;
    @BindView(R.id.regis_bt_back) Button btBack;
    @BindView(R.id.veri_bt_back) Button btBackVeri;
    @BindView(R.id.veri_bt_confirm) Button btConfirmVeri;

    SharedPref sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        hide();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etLoginEmail.getText().length() == 0 || etLoginEmail.getText().length() == 0){
                    Toast.makeText(getBaseContext(), "Lengkapi data", Toast.LENGTH_SHORT).show();
                } else
                {
                    login(etLoginEmail.getText().toString().trim(),
                            etPassword.getText().toString().trim());
                }
            }
        });

        /*final Button btSignUp = (Button) findViewById(R.id.login_bt_sign_up);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Fitur ini sedang dikerjakan", Toast.LENGTH_SHORT).show();
                llRegistration.setVisibility(LinearLayout.VISIBLE);
                llLogin.setVisibility(LinearLayout.GONE);
            }
        });*/

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLogin.setVisibility(LinearLayout.VISIBLE);
                llRegistration.setVisibility(LinearLayout.GONE);
            }
        });

        btBackVeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLogin.setVisibility(LinearLayout.VISIBLE);
                llVerification.setVisibility(LinearLayout.GONE);
            }
        });

        btConfirmVeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                veri(etVerification.getText().toString().trim(),
                        etLoginEmail.getText().toString().trim());
            }
        });

         sp = new SharedPref(getBaseContext());

        //cek shared preferences
        if(sp.cekSharedPreferences()){
            //buka main app
            Toast.makeText(getBaseContext(), "Login information registered", Toast.LENGTH_SHORT).show();
            openMainApp();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    void login(final String em, String pw){
        pb.setVisibility(ProgressBar.VISIBLE);

        try {
            String url = Cons.BASE_URL + "login.php?action=1" +
                    "&em=" + URLEncoder.encode(em, "utf-8") +
                    "&pw=" + URLEncoder.encode(pw, "utf-8");

            Log.d(Cons.TAG, "login: " + url);
            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Save: ", response.toString());
                            String status = response.optString("status");
                            if(status.equals("1")) {

                                llLogin.setVisibility(LinearLayout.GONE);
                                llVerification.setVisibility(LinearLayout.VISIBLE);
                            }else{
                                Toast.makeText(getBaseContext(), "Login gagal!\nData tak ditemukan", Toast.LENGTH_SHORT).show();
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

    void veri(final String veri, String em){
        pb.setVisibility(ProgressBar.VISIBLE);

        try {
            String url = Cons.BASE_URL + "login.php?action=11" +
                    "&veri=" + URLEncoder.encode(veri, "utf-8") +
                    "&em=" + URLEncoder.encode(em, "utf-8");

            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Save: ", response.toString());
                            String email = null, type = null, username = null, foto = null, status = response.optString("status").trim();

                            if(status.equals("1")) {
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = response.getJSONArray("result");

                                    JSONObject data = jsonArray.getJSONObject(0);
                                    email = data.getString("email").toString().trim();
                                    type = data.getString("type").toString().trim();
                                    username = data.getString("username").trim();
                                    foto = data.getString("foto").trim();

                                    sp.saveSharedPreferences(email, type, username, foto);

                                    //buka main app
                                    openMainApp();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(getBaseContext(), "Login gagal!\nData tak ditemukan", Toast.LENGTH_SHORT).show();
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

    private void openMainApp() {
        Intent i = new Intent(LoginActivity.this, MainAppActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
        LoginActivity.this.finish();
    }
}
