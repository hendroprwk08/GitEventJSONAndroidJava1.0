package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.CardViewHolder> {
    private List<Participant> list;
    private Context context;
    private String url;
    private int pos;

    public ParticipantAdapter(Context context, List<Participant> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participant_item_layout, viewGroup, false);
        CardViewHolder viewHolder = new CardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        pos = i;

        final String id = list.get(i).getId();
        final String name = list.get(i).getName();
        final String institution = list.get(i).getInstitution();
        final String whatsapp = list.get(i).getWhatsapp();
        final String phone = list.get(i).getPhone();
        final String email = list.get(i).getEmail();
        final String input = list.get(i).getInput();

        cardViewHolder.tv_name.setText(name);
        cardViewHolder.tv_institution.setText(institution);
        cardViewHolder.tv_phone.setText(phone);
        cardViewHolder.tv_input.setText(input);

        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            TextView d_tv_name, d_tv_institution, d_tv_phone, d_tv_whatsapp, d_tv_email;

            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_institution, tv_phone, tv_input;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_rv_name);
            tv_institution = (TextView) itemView.findViewById(R.id.tv_rv_campus);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_rv_phone);
            tv_input = (TextView) itemView.findViewById(R.id.tv_rv_input);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Participant> members) {
        list = members;
        notifyDataSetChanged();
    }
}