package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;


public class UserFormFragment extends Fragment {
    String ACTION;
    LinearLayout llAdd, llEdit;
    ProgressBar pb;

    public UserFormFragment(String action) {
        this.ACTION = action;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_form, container, false);

        pb = (ProgressBar) view.findViewById(R.id.pb);

        llAdd = (LinearLayout) view.findViewById(R.id.ll_new);
        llEdit = (LinearLayout) view.findViewById(R.id.ll_edit);

        final EditText etUsername = (EditText) view.findViewById(R.id.et_form_user_username);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_form_user_password);
        final EditText etPhone = (EditText) view.findViewById(R.id.et_form_user_phone);
        final EditText etEmail = (EditText) view.findViewById(R.id.et_form_user_email);
        final Spinner spActive = (Spinner) view.findViewById(R.id.sp_form_user_active);
        final Spinner spType = (Spinner) view.findViewById(R.id.sp_form_user_type);

        if(ACTION.equals("add")) {
            llEdit.setVisibility(View.GONE);
        }else{
            Bundle bundle = getArguments();
            if (bundle != null){

                etUsername.setText(bundle.getString("busername").trim());
                etUsername.setEnabled(false);

                etEmail.setText(bundle.getString("bemail").trim());
                etPhone.setText(bundle.getString("bphone").trim());

                //panggil array
                String[] arrActive = getResources().getStringArray(R.array.yes_no);
                int idxActive = Arrays.asList(arrActive).indexOf(bundle.getString("bactive").trim());//find the index
                spActive.setSelection(idxActive);

                String[] arrType = getResources().getStringArray(R.array.admin_user);
                int idxType = Arrays.asList(arrType).indexOf(bundle.getString("btype").trim());//find the index
                spType.setSelection(idxType);

            }

            llAdd.setVisibility(View.GONE);
        }

        final Button btSave = (Button) view.findViewById(R.id.bt_form_user_save);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //kalo mau dapetin indexnya ---> String.valueOf(spActive.getSelectedItemPosition())

            save(etUsername.getText().toString().trim(),
                etPassword.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                spActive.getSelectedItem().toString().trim(),
                spType.getSelectedItem().toString().trim());
            }
        });

        final Button btUpdate = (Button) view.findViewById(R.id.bt_form_user_update);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(etUsername.getText().toString().trim(),
                        etPassword.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etPhone.getText().toString().trim(),
                        spActive.getSelectedItem().toString().trim(),
                        spType.getSelectedItem().toString().trim());
            }
        });

        final Button btDelete = (Button) view.findViewById(R.id.bt_form_user_delete);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(etUsername.getText().toString().trim());
            }
        });

        return view;
    }

    void save(final String us, String pw, String em, String ph, String ac, String ty ){
        pb.setVisibility(ProgressBar.VISIBLE);

        try {
            String url = "http://event-lcc-me.000webhostapp.com/pengguna.php?action=1" +
                    "&username=" + URLEncoder.encode(us, "utf-8") +
                    "&password=" + URLEncoder.encode(pw, "utf-8") +
                    "&email=" + URLEncoder.encode(em, "utf-8") +
                    "&phone=" + URLEncoder.encode(ph, "utf-8") +
                    "&active=" + URLEncoder.encode(ac, "utf-8") +
                    "&type=" + URLEncoder.encode(ty, "utf-8");

            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Save: ", response.toString());

                            Toast.makeText(getContext(),
                                    us + " saved",
                                    Toast.LENGTH_SHORT).show();

                            pb.setVisibility(ProgressBar.GONE);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d("Events: ", error.toString());

                    pb.setVisibility(ProgressBar.GONE);

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) { //time out or there is no connection
                        Toast.makeText(getContext(), R.string.time_out_try_again, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) { //there was an Authentication Failure
                        Toast.makeText(getContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) { //server responded with a error response
                        Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {//network error while performing the request
                        Toast.makeText(getContext(), R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {//the server response could not be parsed
                        Toast.makeText(getContext(), R.string.reading_data_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            queue.add(jsObjRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            String stackTrace = Log.getStackTraceString(e);

            Toast.makeText(getContext(),
                    stackTrace,
                    Toast.LENGTH_SHORT).show();
        }
    }

    void update(final String us, String pw,String em, String ph, String ac, String ty ){
        try {
            String url = "http://event-lcc-me.000webhostapp.com/pengguna.php?action=2" +
                    "&username=" + URLEncoder.encode(us, "utf-8") +
                    "&password=" + URLEncoder.encode(pw, "utf-8") +
                    "&email=" + URLEncoder.encode(em, "utf-8") +
                    "&phone=" + URLEncoder.encode(ph, "utf-8") +
                    "&active=" + URLEncoder.encode(ac, "utf-8") +
                    "&type=" + URLEncoder.encode(ty, "utf-8");

            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Save: ", response.toString());

                            Toast.makeText(getContext(),
                                    us + " updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d("Events: ", error.toString());

                    Toast.makeText(getContext(),
                            error.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(jsObjRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            String stackTrace = Log.getStackTraceString(e);

            Toast.makeText(getContext(),
                    stackTrace,
                    Toast.LENGTH_SHORT).show();
        }

    }

    void delete(final String us){
        new AlertDialog.Builder(getContext())
            .setIcon(R.mipmap.ic_launcher)
            .setTitle("Warning")
            .setMessage("Delete " + us +"?")
            .setCancelable(true)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String url = Cons.BASE_URL +"pengguna.php?action=3&username=" + URLEncoder.encode(us, "utf-8");

                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Delete: ", response.toString());

                                        Toast.makeText(getContext(),
                                                us + " deleted",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                pb.setVisibility(ProgressBar.GONE);

                                if (error instanceof TimeoutError || error instanceof NoConnectionError) { //time out or there is no connection
                                    Toast.makeText(getContext(), R.string.time_out_try_again, Toast.LENGTH_SHORT).show();
                                } else if (error instanceof AuthFailureError) { //there was an Authentication Failure
                                    Toast.makeText(getContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ServerError) { //server responded with a error response
                                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NetworkError) {//network error while performing the request
                                    Toast.makeText(getContext(), R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ParseError) {//the server response could not be parsed
                                    Toast.makeText(getContext(), R.string.reading_data_failed, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        queue.add(jsObjRequest);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(),
                            "Deletion canceled",
                            Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                    //showDetailDialog(view);
                }
            })
            .show();
    }
}