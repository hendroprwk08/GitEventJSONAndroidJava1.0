package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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

        holder.tv_event.setText(event);
        holder.tv_tanggal.setText(tgl);
        holder.tv_jam.setText(jam);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        TextView tv_event, tv_tanggal, tv_jam;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_event = (TextView) itemView.findViewById(R.id.tv_event_event);
            tv_tanggal = (TextView) itemView.findViewById(R.id.tv_event_date);
            tv_jam = (TextView) itemView.findViewById(R.id.tv_event_time);
        }
    }
}