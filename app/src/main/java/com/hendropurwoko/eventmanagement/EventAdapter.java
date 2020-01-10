package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class EventAdapter extends RecyclerView.Adapter<EventAdapter.CardViewHolder> {
    AlertDialog.Builder dialog;
    private List<Event> list;
    private Context context;
    private String url;
    private int pos;

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