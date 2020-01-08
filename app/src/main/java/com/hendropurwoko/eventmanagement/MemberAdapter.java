package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.CardViewHolder> {
    private List<Member> list;
    private Context context;
    private String url;
    private int pos;

    public MemberAdapter(Context context, List<Member> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_item_layout, viewGroup, false);
        CardViewHolder viewHolder = new CardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHolder holder, int i) {
        final boolean[] showActivity = {false};
        pos = i;

        final String id = list.get(i).getId();
        final String name = list.get(i).getName();
        final String institution = list.get(i).getInstitution();
        final String whatsapp = list.get(i).getWhatsapp();
        final String phone = list.get(i).getPhone();
        final String email = list.get(i).getEmail();
        final String active = list.get(i).getActive();
        final String times = list.get(i).getTimes();

        holder.tvName.setText(name);
        holder.tvInstitution.setText(institution);
        holder.tvWhatsapp.setText(whatsapp);
        holder.tvPhone.setText(phone);
        holder.tvEmail.setText(email);
        holder.tvTimes.setText(times);

        if (active.equals("No")){
            holder.llWrap.setBackgroundResource(R.drawable.redpastelrcorner);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!showActivity[0]){
                    showActivity[0] = true;
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    holder.llActivity.setVisibility(LinearLayout.VISIBLE);

                    try {
                        String url = Cons.BASE_URL + "peserta.php?action=7" +
                                "&id=" + URLEncoder.encode(id, "utf-8");

                        RequestQueue queue = Volley.newRequestQueue(context);
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Save: ", response.toString());

                                        JSONArray jsonArray = null;

                                        try {
                                            jsonArray = response.getJSONArray("result");

                                            if (jsonArray.length() != 0) {

                                                String myText = "";
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject data = jsonArray.getJSONObject(i);

                                                    myText = myText + (i + 1) + ". <b>" + data.getString("event").toString().trim() +
                                                            "</b> <small>" + data.getString("date").toString().trim() +
                                                            " " + data.getString("time").toString().trim().substring(0, 5) + "</small><br/>";
                                                }

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    holder.tvActivity.setText(Html.fromHtml(myText.substring(0, myText.length() - 5), Html.FROM_HTML_MODE_COMPACT));
                                                } else {
                                                    holder.tvActivity.setText(Html.fromHtml(myText.substring(0, myText.length() - 5)));
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

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
                                }
                            }
                        });
                        queue.add(jsObjRequest);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();

                        String stackTrace = Log.getStackTraceString(e);

                        Toast.makeText(context,
                                stackTrace,
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showActivity[0] = false;
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    holder.llActivity.setVisibility(LinearLayout.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInstitution, tvWhatsapp, tvPhone, tvEmail, tvTimes, tvActivity;
        LinearLayout llWrap, llActivity;
        CircleImageView iv;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_rv_name);
            tvInstitution = (TextView) itemView.findViewById(R.id.tv_rv_institution);
            tvWhatsapp = (TextView) itemView.findViewById(R.id.tv_rv_whatsapp);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_rv_phone);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_rv_email);
            tvTimes = (TextView) itemView.findViewById(R.id.tv_rv_times);
            tvActivity = (TextView) itemView.findViewById(R.id.tv_rv_activity);
            llWrap = (LinearLayout) itemView.findViewById(R.id.wrap);
            llActivity = (LinearLayout) itemView.findViewById(R.id.ll_rv_activity);
            iv = (CircleImageView) itemView.findViewById(R.id.iv);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Member> members) {
        list = members;
        notifyDataSetChanged();
    }

    private void loadEventPeserta(String id) {


    }
}