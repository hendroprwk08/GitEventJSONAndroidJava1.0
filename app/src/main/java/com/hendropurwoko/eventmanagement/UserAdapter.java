package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Arrays;
import java.util.List;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.CardViewHolder> {
    private List<User> users;
    private Context context;
    private String url;
    private int pos;

    public UserAdapter(Context context,  List<User> list) {
        this.context = context;
        this.users = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item_layout, viewGroup, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        final String username = users.get(i).getUSERNAME();
        final String email = users.get(i).getEMAIL();
        final String phone = users.get(i).getPHONE();
        final String active = users.get(i).getACTIVE();
        final String type = users.get(i).getTYPE();

        cardViewHolder.tv_username.setText(username);
        cardViewHolder.tv_email.setText(email);
        cardViewHolder.tv_phone.setText(phone);
        cardViewHolder.tv_type.setText(type);

        if (active.equals("No")){
            cardViewHolder.ll.setBackgroundResource(R.drawable.redpastelrcorner);
        }

        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("busername", username);
                b.putString("bemail", email);
                b.putString("bphone", phone);
                b.putString("bactive", active);
                b.putString("btype", type);

                UserFormFragment fragment = new UserFormFragment("edit");
                fragment.setArguments(b);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();

                MainAppActivity.hideBar();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username, tv_email, tv_phone, tv_type;
        LinearLayout ll;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = (TextView) itemView.findViewById(R.id.tv_rv_username);
            tv_email = (TextView) itemView.findViewById(R.id.tv_rv_email);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_rv_phone);
            tv_type = (TextView) itemView.findViewById(R.id.tv_rv_type);
            ll = (LinearLayout) itemView.findViewById(R.id.wrap);
        }
    }
}