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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onBindViewHolder(@NonNull CardViewHolder holder, int i) {
        final String username = users.get(i).getUSERNAME();
        final String email = users.get(i).getEMAIL();
        final String phone = users.get(i).getPHONE();
        final String active = users.get(i).getACTIVE();
        final String type = users.get(i).getTYPE();

        holder.tv_username.setText(username);
        holder.tv_email.setText(email);
        holder.tv_phone.setText(phone);
        holder.tv_type.setText(type);

        if (active.equals("No")){
            holder.tv_inactive.setVisibility(TextView.VISIBLE);
            //cardViewHolder.ll.setBackgroundResource(R.drawable.red_pastel_round_corner);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

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
        @BindView(R.id.wrap) LinearLayout ll;
        @BindView(R.id.tv_rv_username) TextView tv_username;
        @BindView(R.id.tv_rv_email) TextView tv_email;
        @BindView(R.id.tv_rv_phone) TextView tv_phone;
        @BindView(R.id.tv_rv_type) TextView tv_type;
        @BindView(R.id.tv_rv_inactive) TextView tv_inactive;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}