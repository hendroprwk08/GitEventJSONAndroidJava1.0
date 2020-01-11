package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class EventAdapter extends RecyclerView.Adapter<EventAdapter.CardViewHolder> {
    private List<Event> list;
    private Context context;

    String mail_event, mail_deskripsi;

    public EventAdapter(Context context, List<Event> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_layout, parent, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        final String id = list.get(position).getId();
        final String event = list.get(position).getEvent();
        final String deskripsi = list.get(position).getDescription();
        final String tgl = list.get(position).getDate();
        final String jam = list.get(position).getTime();
        final String visible = list.get(position).getVisible();

        mail_event = list.get(position).getEvent();
        mail_deskripsi = list.get(position).getDescription();

        holder.tv_event.setText(event);
        holder.tv_tanggal.setText(tgl);
        holder.tv_jam.setText(jam);

        if (visible.equals("0")){
            //holder.ll.setBackgroundResource(R.drawable.red_pastel_round_corner);
            holder.tv_inactive.setVisibility(TextView.VISIBLE);
        }

        holder.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("bid", id);
                b.putString("bevent", event);
                b.putString("bdeskripsi", deskripsi);
                b.putString("btgl", tgl);
                b.putString("bjam", jam);
                b.putString("bvisible", visible);

                EventFormFragment fragment = new EventFormFragment("edit");
                fragment.setArguments(b);

                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();

                MainAppActivity.hideBar();
            }
        });

        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //collect email address to an array
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = Cons.BASE_URL +"peserta.php?action=4";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d(Cons.TAG, "onResponse: " + response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("result");

                                    String[] emails = new String[jsonArray.length()];

                                    if (jsonArray.length() != 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject data = jsonArray.getJSONObject(i);

                                            emails[i] =  data.getString("EMAIL").trim();
                                        }
                                    }
                                    Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
                                    selectorIntent.setData(Uri.parse("mailto:"));

                                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, event);
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, deskripsi);
                                    emailIntent.setSelector( selectorIntent );

                                    context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) { //time out or there is no connection
                            Toast.makeText(context, R.string.time_out_try_again, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) { //there was an Authentication Failure
                            Toast.makeText(context, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) { //server responded with a error response
                            Toast.makeText(context, R.string.server_problem, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {//network error while performing the request
                            Toast.makeText(context, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {//the server response could not be parsed
                            Toast.makeText(context, R.string.reading_data_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.server_problem, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                queue.add(jsObjRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll;
        TextView tv_event, tv_tanggal, tv_jam, tv_inactive;
        CircleImageView ivInfo, ivShare;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.wrap);
            tv_event = (TextView) itemView.findViewById(R.id.tv_event_event);
            tv_tanggal = (TextView) itemView.findViewById(R.id.tv_event_date);
            tv_jam = (TextView) itemView.findViewById(R.id.tv_event_time);
            tv_inactive = (TextView) itemView.findViewById(R.id.tv_inactive);
            ivInfo = (CircleImageView) itemView.findViewById(R.id.iv_detail);
            ivShare = (CircleImageView) itemView.findViewById(R.id.iv_share);
        }
    }
}