package com.hendropurwoko.eventmanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserFragment extends Fragment {
    @BindView(R.id.pb) ProgressBar pb;
    @BindView(R.id.userSwipeRefreshLayout) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rv_users) RecyclerView recyclerView;

    View fragment_view;
    public List<User> users;

    RequestQueue requestQueue;
    JsonObjectRequest jsObjRequest;

    private Unbinder unbinder;

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view); //bind butter knife

        fragment_view = view;

        load();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                load();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000); // Delay in millis
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    void load() {
        pb.setVisibility(ProgressBar.VISIBLE);

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL +"pengguna.php?action=4";

        jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Log.d("Users ", response.toString());

                    users = new ArrayList<>();

                    try {
                        JSONArray jsonArray = response.getJSONArray("result");
                        users.clear();
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                users.add(new User(
                                        data.getString("USERNAME").toString().trim(),
                                        data.getString("EMAIL").toString().trim(),
                                        data.getString("PHONE").toString().trim(),
                                        data.getString("ACTIVE").toString().trim(),
                                        data.getString("TYPE").toString().trim()));
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            UserAdapter mAdapter = new UserAdapter(getContext(), users);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                        }
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

        jsObjRequest.setTag("tUser");
        requestQueue.add(jsObjRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (requestQueue != null) {
            requestQueue.cancelAll("tUser");
        }
    }
}
