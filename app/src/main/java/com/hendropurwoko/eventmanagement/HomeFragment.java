package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    TextView tvJEvent, tvJMember,tvParticipant, tvMember;
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pb = (ProgressBar) view.findViewById(R.id.pb);

        tvJEvent = (TextView) view.findViewById(R.id.tv_home_j_event);
        tvJMember = (TextView) view.findViewById(R.id.tv_home_j_member);
        tvParticipant = (TextView) view.findViewById(R.id.tv_home_participant);
        tvMember = (TextView) view.findViewById(R.id.tv_home_member);

        final ImageView ivRefresh = (ImageView) view.findViewById(R.id.iv_home_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadJEvent();
                loadJMember();
                loadParticipant();
                loadMember();
            }
        });

        loadJEvent();
        loadJMember();
        loadParticipant();
        loadMember();

        return view;
    }

    private void loadMember() {
        pb.setVisibility(ProgressBar.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=3";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Events: ", response.toString());
                        String result = new String();
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);
                                    result += "<h4>"+ data.getString("event").toString().trim().toUpperCase()
                                            + "</h4> " + data.getString("date").toString().trim()
                                            + " " + data.getString("time").toString().trim().substring(0, 5)
                                            + " [ "+ data.getString("jumlah").toString().trim() + " ]<br/><br/>";
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    tvMember.setText(Html.fromHtml(result.substring(0, result.length() - 10), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    tvMember.setText(Html.fromHtml(result.substring(0, result.length() - 10)));
                                }
                            }

                            pb.setVisibility(ProgressBar.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            pb.setVisibility(ProgressBar.GONE);
                        }
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(jsObjRequest);
    }

    private void loadParticipant() {
        pb.setVisibility(ProgressBar.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=4";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Events: ", response.toString());
                        String result = new String();
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);
                                    result += "<h4>"+ data.getString("name").toString().trim().toUpperCase()
                                            +"</h4>"+ data.getString("institution").toString().trim().toUpperCase()
                                            +" [ "+ data.getString("jumlah").toString().trim() + " ]<br/><br/>";
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    tvParticipant.setText(Html.fromHtml(result.substring(0, result.length() - 10), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    tvParticipant.setText(Html.fromHtml(result.substring(0, result.length() - 10)));
                                }
                            }

                            pb.setVisibility(ProgressBar.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            pb.setVisibility(ProgressBar.GONE);
                        }
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(jsObjRequest);
    }

    private void loadJMember() {
        pb.setVisibility(ProgressBar.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=2";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Events: ", response.toString());
                        String jumlah;
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            if (jsonArray.length() != 0) {
                                JSONObject data = jsonArray.getJSONObject(0);
                                jumlah = data.getString("jumlah").toString().trim();
                                tvJMember.setText(jumlah);
                            }

                            pb.setVisibility(ProgressBar.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            pb.setVisibility(ProgressBar.GONE);
                        }
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(jsObjRequest);
    }

    private void loadJEvent() {
        pb.setVisibility(ProgressBar.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL + "dashboard.php?action=1";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Events: ", response.toString());
                        String jumlah;
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            if (jsonArray.length() != 0) {
                                JSONObject data = jsonArray.getJSONObject(0);
                                jumlah = data.getString("jumlah").toString().trim();
                                tvJEvent.setText(jumlah);
                            }

                            pb.setVisibility(ProgressBar.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            pb.setVisibility(ProgressBar.GONE);
                        }
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
                } else {
                    Toast.makeText(getContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(jsObjRequest);
    }
}
