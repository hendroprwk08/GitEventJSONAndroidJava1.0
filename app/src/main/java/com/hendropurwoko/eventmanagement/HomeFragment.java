package com.hendropurwoko.eventmanagement;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {
    @BindView(R.id.pb_j_event) ProgressBar pbJEvent;
    @BindView(R.id.pb_j_member) ProgressBar pbJMember;
    @BindView(R.id.pb_chart) ProgressBar pbChart;
    @BindView(R.id.pb_member) ProgressBar pbMember;
    @BindView(R.id.pb_participant) ProgressBar pbParticipant;
    @BindView(R.id.pb_lat_event) ProgressBar pbLatEvent;
    @BindView(R.id.tv_home_j_event) TextView tvJEvent;
    @BindView(R.id.tv_home_j_member) TextView tvJMember;
    @BindView(R.id.tv_home_participant) TextView tvParticipant;
    @BindView(R.id.tv_home_member) TextView tvMember;
    @BindView(R.id.tv_home_lat_event) TextView tvLatEvent;
    @BindView(R.id.bc_chart) BarChart chart;
    @BindView(R.id.sp_thn) Spinner spThn;

    private Unbinder unbinder;

    RequestQueue requestQueue;
    JsonObjectRequest jsObjRequest;

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view); //bind butter knife

        final ImageView ivRefresh = (ImageView) view.findViewById(R.id.iv_home_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRefresh.animate().rotationBy(360f).setDuration(2000).setInterpolator(new LinearInterpolator()).start();

                loadSpinner();
                loadJEvent();
                loadJMember();
                loadActivity();
                loadEvent();
                loadParticipant();
            }
        });

        ivRefresh.animate().rotationBy(360f).setDuration(2000).setInterpolator(new LinearInterpolator()).start();

        loadSpinner();
        loadJEvent();
        loadJMember();
        loadActivity();
        loadParticipant();
        loadEvent();

        spThn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadChart(spThn.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return view;
    }

    private void loadSpinner() {
        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=5";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        ArrayList<String> strings = new ArrayList<>();

                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                strings.add(data.getString("tahun").trim());
                            }

                            //set to spinner
                            ArrayAdapter<String> thnAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_layout, strings);
                            spThn.setAdapter(thnAdapter);
                            spThn.setSelection(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tTahun");
        requestQueue.add(jsObjRequest);
    }

    private void loadChart(String thn) {
        //ambil tahun sekarang
        Calendar calendar = Calendar.getInstance();
        thn = (thn == null) ? String.valueOf(calendar.get(Calendar.YEAR)) : thn;

        // V O L L E Y
        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=6&thn="+ thn;

        pbChart.setVisibility(ProgressBar.VISIBLE);

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Events: ", response.toString());

                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        if (jsonArray.length() != 0) {
                            int peserta = 0, event = 0, bulan = 0;

                            ArrayList<BarEntry> values1 = new ArrayList<>();
                            ArrayList<BarEntry> values2 = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                bulan = Integer.parseInt(data.getString("bulan").trim());

                                /*if(data.isNull("peserta")){
                                    peserta = 0;
                                } else {
                                    peserta = Integer.parseInt(data.getString("peserta").trim());
                                }

                                if(data.isNull("event")){
                                    event = 0;
                                } else {
                                    event = Integer.parseInt(data.getString("event").trim());
                                }*/

                                peserta = Integer.parseInt((data.isNull("jpeserta")) ? "0" : data.getString("jpeserta").trim());
                                event = Integer.parseInt((data.isNull("jevent")) ? "0" : data.getString("jevent").trim());

                                values1.add(new BarEntry(bulan, peserta));
                                values2.add(new BarEntry(bulan, event));
                            }

                            /* BAR CHART */
                            float groupSpace = 0.04f;
                            float barSpace = 0.02f; // x2 dataset
                            float barWidth = 0.46f; // x2 dataset

                            //float groupSpace = 0.08f;
                            //float barSpace = 0.03f; // x4 DataSet
                            //float barWidth = 0.2f; // x4 DataSet
                            //(0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

                            int groupCount = jsonArray.length();
                            int startYear = 1;
                            int endYear = startYear + groupCount;

                            //BarChart chart = (BarChart) view.findViewById(R.id.bc_chart);
                            chart.getDescription().setEnabled(false);

                            Legend l = chart.getLegend();
                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                            l.setOrientation(Legend.LegendOrientation.VERTICAL);
                            l.setDrawInside(true);
                            l.setYOffset(0f);
                            l.setXOffset(10f);
                            l.setYEntrySpace(0f);
                            l.setTextSize(8f);

                            XAxis xAxis = chart.getXAxis();
                            xAxis.setGranularity(1f);
                            xAxis.setCenterAxisLabels(true);
                            xAxis.setValueFormatter(new ValueFormatter() {
                                @Override
                                public String getFormattedValue(float value) {
                                    return String.valueOf((int) value);
                                }
                            });

                            YAxis leftAxis = chart.getAxisLeft();
                            leftAxis.setValueFormatter(new LargeValueFormatter());
                            leftAxis.setDrawGridLines(false);
                            leftAxis.setSpaceTop(35f);
                            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                            chart.getAxisRight().setEnabled(false);

                            float start = 1f;
                            int count = 12;

                            BarDataSet set1 = new BarDataSet(values1, "Participants");
                            set1.setColor(Color.rgb(24, 116, 205));

                            BarDataSet set2 = new BarDataSet(values2, "Event");
                            set2.setColor(Color.rgb(255, 185, 15));

                            BarData data = new BarData(set1, set2);
                            data.setValueFormatter(new LargeValueFormatter());

                            chart.setData(data);

                            // specify the width each bar should have
                            chart.getBarData().setBarWidth(barWidth);

                            // restrict the x-axis range
                            chart.getXAxis().setAxisMinimum(startYear);

                            // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
                            chart.getXAxis().setAxisMaximum(startYear + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                            chart.groupBars(startYear, groupSpace, barSpace);
                            chart.invalidate();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(Cons.TAG, "onResponse: " + e.getMessage());
                    } catch (NullPointerException n){
                        Log.d(Cons.TAG, "onResponse: " + n.getMessage());
                    } finally {
                        pbChart.setVisibility(ProgressBar.GONE);
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tGrafik");
        requestQueue.add(jsObjRequest);
    }

    private void loadEvent() {
        pbMember.setVisibility(ProgressBar.VISIBLE);
        tvMember.setVisibility(TextView.GONE);

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=3";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Events: ", response.toString());
                    String result = new String();
                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                result += "<b>"+ data.getString("event").toString().trim().toUpperCase()
                                        + "</b><br/><small>" + data.getString("date").toString().trim()
                                        + " " + data.getString("time").toString().trim().substring(0, 5)
                                        + " [ "+ data.getString("jumlah").toString().trim() + " ]</small><br/><br/>";
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tvMember.setText(Html.fromHtml(result.substring(0, result.length() - 10), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tvMember.setText(Html.fromHtml(result.substring(0, result.length() - 10)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        pbMember.setVisibility(ProgressBar.GONE);
                        tvMember.setVisibility(TextView.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tEvent");
        requestQueue.add(jsObjRequest);
    }

    private void loadParticipant() {
        pbParticipant.setVisibility(ProgressBar.VISIBLE);
        tvParticipant.setVisibility(TextView.GONE);

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=4";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Events: ", response.toString());
                    String result = new String();
                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                result += "<b>"+ data.getString("name").toString().trim().toUpperCase()
                                        +"</b><br/><small>"+ data.getString("institution").toString().trim().toUpperCase()
                                        +" [ "+ data.getString("jumlah").toString().trim() + " ]</small><br/><br/>";
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tvParticipant.setText(Html.fromHtml(result.substring(0, result.length() - 10), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tvParticipant.setText(Html.fromHtml(result.substring(0, result.length() - 10)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        pbParticipant.setVisibility(ProgressBar.GONE);
                        tvParticipant.setVisibility(TextView.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tActive");
        requestQueue.add(jsObjRequest);
    }

    private void loadActivity() {
        pbLatEvent.setVisibility(ProgressBar.VISIBLE);
        tvLatEvent.setVisibility(TextView.GONE);

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=31";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Events: ", response.toString());
                    String result = new String();
                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                result += "<b>"+ data.getString("event").toString().trim().toUpperCase()
                                        + "</b><br/><small>" + data.getString("date").toString().trim()
                                        + " " + data.getString("time").toString().trim().substring(0, 5)
                                        + " [ "+ data.getString("jumlah").toString().trim() + " ]</small><br/><br/>";
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tvLatEvent.setText(Html.fromHtml(result.substring(0, result.length() - 10), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tvLatEvent.setText(Html.fromHtml(result.substring(0, result.length() - 10)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        pbLatEvent.setVisibility(ProgressBar.GONE);
                        tvLatEvent.setVisibility(TextView.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tLatest");
        requestQueue.add(jsObjRequest);
    }

    private void loadJMember() {
        pbJMember.setVisibility(ProgressBar.VISIBLE);
        tvJMember.setVisibility(TextView.GONE);

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=2";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Events: ", response.toString());
                    String jumlah;
                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        if (jsonArray.length() != 0) {
                            JSONObject data = jsonArray.getJSONObject(0);
                            jumlah = data.getString("jumlah").toString().trim();
                            tvJMember.setText(jumlah);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        pbJMember.setVisibility(ProgressBar.GONE);
                        tvJMember.setVisibility(TextView.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tJMember");
        requestQueue.add(jsObjRequest);
    }

    private void loadJEvent() {
        pbJEvent.setVisibility(ProgressBar.VISIBLE);
        tvJEvent.setVisibility(TextView.GONE);

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=1";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Events: ", response.toString());
                    String jumlah;
                    try {
                        JSONArray jsonArray = response.getJSONArray("result");

                        if (jsonArray.length() != 0) {
                            JSONObject data = jsonArray.getJSONObject(0);
                            jumlah = data.getString("jumlah").toString().trim();
                            tvJEvent.setText(jumlah);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        pbJEvent.setVisibility(ProgressBar.GONE);
                        tvJEvent.setVisibility(TextView.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsObjRequest.setTag("tJEvent");
        requestQueue.add(jsObjRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (requestQueue != null) {
            requestQueue.cancelAll("tTahun");
            requestQueue.cancelAll("tGrafik");
            requestQueue.cancelAll("tEvent");
            requestQueue.cancelAll("tActive");
            requestQueue.cancelAll("tLatest");
            requestQueue.cancelAll("tJEvent");
            requestQueue.cancelAll("tJMember");
        }
    }
}
