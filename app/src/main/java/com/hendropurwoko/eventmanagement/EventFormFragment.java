package com.hendropurwoko.eventmanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.github.mikephil.charting.charts.BarChart;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventFormFragment extends Fragment {
    @BindView(R.id.pb) ProgressBar pb;
    @BindView(R.id.et_form_event_name) EditText etName;
    @BindView(R.id.et_form_event_description) EditText etDescription;
    @BindView(R.id.et_form_event_date) TextView etDate;
    @BindView(R.id.et_form_event_time) TextView etTime;
    @BindView(R.id.sp_form_event_visible) Spinner sp;
    @BindView(R.id.bt_form_event_save) Button btSave;
    @BindView(R.id.bt_form_event_update) Button btUpdate;
    @BindView(R.id.event_button_new) LinearLayout llAdd;
    @BindView(R.id.event_button_edit) LinearLayout llEdit;

    String ID, ACTION = null;
    Bundle bundle;
    final Calendar myCalendar = Calendar.getInstance();

    RequestQueue requestQueue;
    JsonObjectRequest jsObjRequest;

    private Unbinder unbinder;

    public EventFormFragment(String action){
        this.ACTION = action;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_form, container, false);
        unbinder = ButterKnife.bind(this, view); //bind butter knife

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        //put edit text
                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        etDate.setText(sdf.format(myCalendar.getTime()));
                    }
                };

                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        etTime.setText(sdf.format(myCalendar.getTime()));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(Cons.TAG, "onNothingSelected: "+ adapterView);
            }
        });

        /* //prepare spinner data
        List<String> visibles = new ArrayList<>();
        visibles.add("No");
        visibles.add("Yes");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                visibles);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp.setAdapter(dataAdapter);*/

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(etName.getText().toString(),
                        etDescription.getText().toString(),
                        etDate.getText().toString(),
                        etTime.getText().toString(),
                        String.valueOf(sp.getSelectedItemPosition()));
            }
        });

        /* --- No deletion
        final Button btDelete = (Button) view.findViewById(R.id.bt_form_event_delete);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(ID, etName.getText().toString());
            }
        }); */

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(ID,
                        etName.getText().toString(),
                        etDescription.getText().toString(),
                        etDate.getText().toString(),
                        etTime.getText().toString(),
                        String.valueOf(sp.getSelectedItemPosition()));
            }
        });

        if(ACTION.equals("add")) {
            llEdit.setVisibility(View.GONE);
        }else{
            Bundle bundle = getArguments();
            if (bundle != null){
                ID = bundle.getString("bid").trim();
                etName.setText(bundle.getString("bevent").trim());
                etDescription.setText(bundle.getString("bdeskripsi").trim());
                etDate.setText(bundle.getString("btgl").trim());
                etTime.setText(bundle.getString("bjam").trim());
                sp.setSelection(Integer.parseInt(bundle.getString("bvisible").trim()));
            }

            llAdd.setVisibility(View.GONE);
        }

        return view;
    }

    void save(final String nm, String ds, String dt, String tm, String vs ){
        pb.setVisibility(ProgressBar.VISIBLE);

        try {
            String url = Cons.BASE_URL + "event.php?action=1" +
                    "&event=" + URLEncoder.encode(nm, "utf-8") +
                    "&deskripsi=" + URLEncoder.encode(ds, "utf-8") +
                    "&tgl=" + URLEncoder.encode(dt, "utf-8") +
                    "&jam=" + URLEncoder.encode(tm, "utf-8") +
                    "&active=" + URLEncoder.encode(vs, "utf-8");


            requestQueue = Volley.newRequestQueue(getContext());
            jsObjRequest = new JsonObjectRequest(
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

            jsObjRequest.setTag("tSimpan");
            requestQueue.add(jsObjRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            String stackTrace = Log.getStackTraceString(e);
            pb.setVisibility(ProgressBar.GONE);

            Toast.makeText(getContext(),
                    stackTrace,
                    Toast.LENGTH_SHORT).show();
        }
    }

    void update(String id, final String nm, String ds, String dt, String tm, String vs ) {
        try {
            String url = Cons.BASE_URL + "event.php?action=2" +
                    "&id=" + id +
                    "&event=" + URLEncoder.encode(nm, "utf-8") +
                    "&deskripsi=" + URLEncoder.encode(ds, "utf-8") +
                    "&tgl=" + URLEncoder.encode(dt, "utf-8") +
                    "&jam=" + URLEncoder.encode(tm, "utf-8") +
                    "&active=" + URLEncoder.encode(vs, "utf-8");

            Log.d("Update: ", url);

            requestQueue = Volley.newRequestQueue(getContext());
            jsObjRequest = new JsonObjectRequest(
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

            jsObjRequest.setTag("tUpdate");
            requestQueue.add(jsObjRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            String stackTrace = Log.getStackTraceString(e);

            Toast.makeText(getContext(),
                    stackTrace,
                    Toast.LENGTH_SHORT).show();
        }
    }

    void delete(final String id, final String nm){
        new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Warning")
                .setMessage("Hide " + nm +"?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = Cons.BASE_URL +"event.php?action=3&id=" + id;

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
                                                nm + " hidden",
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

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),
                                "Deletion process canceled",
                                Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (requestQueue != null) {
            requestQueue.cancelAll("tSimpan");
            requestQueue.cancelAll("tUpdate");
        }
    }
}