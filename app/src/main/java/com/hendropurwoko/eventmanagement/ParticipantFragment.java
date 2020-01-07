package com.hendropurwoko.eventmanagement;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticipantFragment extends Fragment {
    View fragment_view;
    private SwipeRefreshLayout swipeContainer;
    public List<Participant> participants;

    ArrayList<String> arr;
    HashMap<String, String> mapData;

    ProgressBar pb;
    Spinner sp;
    TextView tvJumlah;
    int EVENT_ID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_participant, container, false);

        fragment_view = view;

        pb = (ProgressBar) view.findViewById(R.id.pb);

        pb.setVisibility(ProgressBar.VISIBLE);

        loadEvent();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.participantSwipeRefreshLayout);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                pb.setVisibility(ProgressBar.VISIBLE);
                load(EVENT_ID);

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

        sp = (Spinner) view.findViewById(R.id.sp_participant);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String eventVal = adapterView.getItemAtPosition(i).toString();
                String idVal =  mapData.get(eventVal);

                pb.setVisibility(ProgressBar.VISIBLE);

                //Log.d(Cons.TAG, "onItemSelected: " + idVal);

                EVENT_ID = Integer.parseInt(idVal);

                Cons.SELECTED_EVENT_INDEX = i; //ambil index sp-nya
                Cons.SELECTED_EVENT_ID = Integer.parseInt(idVal); //tampung id event
                Cons.SELECTED_EVENT_INDEX_STATUS = true; //kasih status

                load(Cons.SELECTED_EVENT_ID );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.d(Cons.TAG, "onNothingSelected: "+ adapterView);
            }
        });

        tvJumlah = (TextView) view.findViewById(R.id.tv_jumlah);
        return view;
    }

    void load(int id) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL +"peserta.php?action=41&id="+ id;

        //Log.d(Cons.TAG, "load: " + url );

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Members: ", response.toString());
                        String id, nama, kampus, whatsapp, phone, email, active, input;

                        participants = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("result");
                            participants.clear();
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    id = data.getString("id").toString().trim();
                                    nama = data.getString("name").toString().trim();
                                    kampus = data.getString("institution").toString().trim();
                                    whatsapp = data.getString("whatsapp").toString().trim();
                                    phone = data.getString("phone").toString().trim();
                                    email = data.getString("email").toString().trim();
                                    active = data.getString("active").toString().trim();
                                    input = data.getString("input").toString().trim();

                                    participants.add(new Participant(id, nama, kampus, whatsapp, phone, email, active, input));
                                }
                                tvJumlah.setText(participants.size() + " Participants");
                            }else{
                                tvJumlah.setText("No Participant");
                            }

                            RecyclerView recyclerView = (RecyclerView) fragment_view.findViewById(R.id.rv_participants);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            ParticipantAdapter mAdapter = new ParticipantAdapter(getContext(), participants);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
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

    void loadEvent() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Cons.BASE_URL +"load_event.php";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Event: ", response.toString());
                        String id = "0", event;

                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            //set hashMap u/ spinner
                            mapData = new HashMap<String, String>();
                            arr = new ArrayList<String>();

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    if (i == 0){
                                        Cons.SELECTED_EVENT_ID = Integer.parseInt(data.getString("id"));
                                    }

                                    id = data.getString("id").toString().trim();
                                    event = data.getString("event").toString().trim();

                                    //taruh di hashMap u/ spinner
                                    mapData.put(event, id);
                                    arr.add(event);
                                }

                                //persiapan array adapter
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        getContext(),
                                        R.layout.my_spinner, //spinner layout diubah
                                        arr);

                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp.setAdapter(arrayAdapter);

                                //do not load if event index status = true
                                if(Cons.SELECTED_EVENT_INDEX_STATUS == true) {
                                    sp.setSelection(Cons.SELECTED_EVENT_INDEX, false);
                                    // baru ----- load(Cons.SELECTED_EVENT_ID);
                                    Cons.SELECTED_EVENT_INDEX_STATUS = false;
                                }else{
                                    load(Cons.SELECTED_EVENT_ID);
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
                }

            }
        });

        queue.add(jsObjRequest);
    }
}
