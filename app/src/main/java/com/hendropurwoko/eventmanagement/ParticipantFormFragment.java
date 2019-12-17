package com.hendropurwoko.eventmanagement;


import android.content.DialogInterface;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ParticipantFormFragment extends Fragment {

    String ACTION, ID;
    LinearLayout llAdd, llEdit;
    ProgressBar pb;

    public ParticipantFormFragment(String action) {
        this.ACTION = action;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_participant_form, container, false);

        final EditText etName = (EditText) view.findViewById(R.id.et_form_member_name);
        final EditText etInstitution = (EditText) view.findViewById(R.id.et_form_member_institution);
        final EditText etEmail = (EditText) view.findViewById(R.id.et_form_member_email);
        final EditText etPhone = (EditText) view.findViewById(R.id.et_form_member_phone);
        final EditText etWhatsapp = (EditText) view.findViewById(R.id.et_form_member_whatsapp);
        final Spinner spActive = (Spinner) view.findViewById(R.id.sp_form_member_active);

        llAdd = (LinearLayout) view.findViewById(R.id.ll_new);
        llEdit = (LinearLayout) view.findViewById(R.id.ll_edit);

        if(ACTION.equals("add")) {
            llEdit.setVisibility(View.GONE);
        }else{
            Bundle bundle = getArguments();
            if (bundle != null){

                ID = bundle.getString("bid").trim();
                etName.setText(bundle.getString("bname").trim());
                etEmail.setText(bundle.getString("bemail").trim());
                etPhone.setText(bundle.getString("bphone").trim());
                etWhatsapp.setText(bundle.getString("bwhatsapp").trim());
                etInstitution.setText(bundle.getString("binstitution").trim());

                //set spinner
                String[] arrActive = getResources().getStringArray(R.array.yes_no);
                int idxActive = Arrays.asList(arrActive).indexOf(bundle.getString("bactive").trim());//find the index
                spActive.setSelection(idxActive);
            }

            llAdd.setVisibility(View.GONE);
        }

        final Button btSave = (Button) view.findViewById(R.id.bt_form_user_save);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kalo mau dapetin indexnya ---> String.valueOf(spActive.getSelectedItemPosition())

                save(etName.getText().toString().trim(),
                        etInstitution.getText().toString().trim(),
                        etWhatsapp.getText().toString().trim(),
                        etPhone.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        spActive.getSelectedItem().toString().trim());
            }
        });


        final Button btUpdate = (Button) view.findViewById(R.id.bt_form_user_update);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(ID,
                        etName.getText().toString().trim(),
                        etInstitution.getText().toString().trim(),
                        etWhatsapp.getText().toString().trim(),
                        etPhone.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        spActive.getSelectedItem().toString().trim());
            }
        });

        /* === No deletion process
        final Button btDelete = (Button) view.findViewById(R.id.bt_form_user_delete);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(ID, etName.getText().toString().trim());
            }
        }); */

        pb = (ProgressBar) view.findViewById(R.id.pb);

        return view;
    }

    void save( final String nm, String in, String wa, String ph, String em, String ac ){
        pb.setVisibility(ProgressBar.VISIBLE);

        try {
            String url = Cons.BASE_URL + "peserta.php?action=1" +
                    "&name=" + URLEncoder.encode(nm, "utf-8") +
                    "&institution=" + URLEncoder.encode(in, "utf-8") +
                    "&whatsapp=" + URLEncoder.encode(wa, "utf-8") +
                    "&phone=" + URLEncoder.encode(ph, "utf-8") +
                    "&email=" + URLEncoder.encode(em, "utf-8") +
                    "&active=" + URLEncoder.encode(ac, "utf-8");

            Log.d(Cons.TAG, "save: " + url);

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
                                    nm + " saved",
                                    Toast.LENGTH_SHORT).show();

                            pb.setVisibility(ProgressBar.GONE);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
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

    void update(String id, final String nm, String in, String wa, String ph, String em, String ac ){
        try {
            String url = Cons.BASE_URL + "peserta.php?action=2" +
                    "&id=" + id +
                    "&name=" + URLEncoder.encode(nm, "utf-8") +
                    "&institution=" + URLEncoder.encode(in, "utf-8") +
                    "&whatsapp=" + URLEncoder.encode(wa, "utf-8") +
                    "&phone=" + URLEncoder.encode(ph, "utf-8") +
                    "&email=" + URLEncoder.encode(em, "utf-8") +
                    "&active=" + URLEncoder.encode(ac, "utf-8");

            Log.d(Cons.TAG, "save: " + url);

            pb.setVisibility(ProgressBar.VISIBLE);

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
                                    nm + " updated",
                                    Toast.LENGTH_SHORT).show();

                            pb.setVisibility(ProgressBar.GONE);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d("Events: ", error.toString());

                    pb.setVisibility(ProgressBar.GONE);

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

            pb.setVisibility(ProgressBar.GONE);
        }

    }

    void delete(final String id, final String nm){
        new AlertDialog.Builder(getContext())
            .setIcon(R.mipmap.ic_launcher)
            .setTitle("Warning")
            .setMessage("Delete " + nm +"?")
            .setCancelable(true)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = Cons.BASE_URL +"peserta.php?action=3&id=" + id.trim();

                    Log.d(Cons.TAG, "save: " + url);

                    pb.setVisibility(ProgressBar.VISIBLE);

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
                                            nm + " deleted",
                                            Toast.LENGTH_SHORT).show();

                                    pb.setVisibility(ProgressBar.GONE);
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
