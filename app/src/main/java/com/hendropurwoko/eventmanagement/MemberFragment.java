package com.hendropurwoko.eventmanagement;


import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    View fragment_view;
    private SwipeRefreshLayout swipeContainer;
    public List<Member> members;
    ProgressBar pb;
    boolean show;
    String cari = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_member, container, false);

        fragment_view = view;

        pb = (ProgressBar) view.findViewById(R.id.pb);

        final ConstraintLayout llTitle = (ConstraintLayout) view.findViewById(R.id.ll_tittle);
        final ConstraintLayout llSearch = (ConstraintLayout) view.findViewById(R.id.ll_search);

        final EditText etSearch = (EditText) view.findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER){
                    load(etSearch.getText().toString().trim());
                    return true; //still focus
                }
                return false;
            }
        });

        //detect focus
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                } else{
                    //show keyboard
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });

        final ImageView ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.requestFocus();

                //show layout
                llSearch.setVisibility(LinearLayout.VISIBLE);
                llTitle.setVisibility(LinearLayout.GONE);
            }
        });

        final ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText(null);
                load(null);

                llSearch.setVisibility(LinearLayout.GONE);
                llTitle.setVisibility(LinearLayout.VISIBLE);
            }
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.eventSwipeRefreshLayout);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                load(cari);

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
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

        load(null);

        return view;
    }

    void load(String find) {
        pb.setVisibility(ProgressBar.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url;

        if (find == null) {
            url =  Cons.BASE_URL + "peserta.php?action=8";
        }else{
            url =  Cons.BASE_URL + "peserta.php?action=9&find="+ find;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Events: ", response.toString());

                        String id, name, institution, whatsapp, phone, email, active, jumlah;
                        members = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("result");
                            members.clear();

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    id = data.getString("id").toString().trim();
                                    name = data.getString("name").toString().trim();
                                    institution = data.getString("institution").toString().trim();
                                    whatsapp = data.getString("whatsapp").toString().trim();
                                    phone = data.getString("phone").toString().trim();
                                    email = data.getString("email").toString().trim();
                                    active = data.getString("active").toString().trim();
                                    jumlah = data.getString("jumlah").toString().trim();

                                    members.add(new Member(id, name, institution, whatsapp, phone, email, active, jumlah));
                                }

                                RecyclerView recyclerView = (RecyclerView) fragment_view.findViewById(R.id.rv_members);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                MemberAdapter mAdapter = new MemberAdapter(getContext(), members);
                                recyclerView.setAdapter(mAdapter);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                pb.setVisibility(ProgressBar.GONE);
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

        queue.add(jsObjRequest);
    }
}
