package com.hendropurwoko.eventmanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.app.ActivityCompat.requestPermissions;

class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.CardViewHolder> {
    private List<Member> list;
    private Context context;
    private String url;
    private int pos;

    int selectedPos = 0;
    final static int PERMISSION_CODE = 1001;

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
        pos = i;

        final String id = list.get(i).getId();
        final String name = list.get(i).getName();
        final String institution = list.get(i).getInstitution();
        final String whatsapp = list.get(i).getWhatsapp();
        final String phone = list.get(i).getPhone();
        final String email = list.get(i).getEmail();
        final String active = list.get(i).getActive();
        final String times = list.get(i).getTimes();
        final boolean exp = list.get(i).isExpanded();

        holder.tvName.setText(name);
        holder.tvInstitution.setText(institution);
        holder.tvWhatsapp.setText(whatsapp);
        holder.tvPhone.setText(phone);
        holder.tvTimes.setText(times);
        holder.tvEmail.setText(email.trim());

        holder.tvInactive.setVisibility(active.equals("No") ? TextView.VISIBLE : TextView.GONE);
        holder.llEmail.setVisibility(email.length() == 0 ? LinearLayout.GONE : LinearLayout.VISIBLE);
        holder.llPhone.setVisibility(phone.length() == 0 ? LinearLayout.GONE : LinearLayout.VISIBLE);
        holder.llWa.setVisibility(whatsapp.length() == 0 ? LinearLayout.GONE : LinearLayout.VISIBLE);
        holder.llActivity.setVisibility(exp ? LinearLayout.VISIBLE : LinearLayout.GONE);

        //send mail
        holder.ivSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                //i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    context.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //call wa number
        holder.ivCallWa.setOnClickListener(new View.OnClickListener() {
            private static final int REQUEST_PHONE_CALL = 1;

            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + whatsapp));

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((MainAppActivity) context, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    context.startActivity(callIntent);
                }
            }

            private int checkSelfPermission(String callPhone) {
                return 0;
            }
        });

        //call phone number
        holder.ivCallPhone.setOnClickListener(new View.OnClickListener() {
            private static final int REQUEST_PHONE_CALL = 1;

            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((MainAppActivity) context, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    context.startActivity(callIntent);
                }
            }

            private int checkSelfPermission(String callPhone) {
                return 0;
            }
        });

        //sms wa number
        holder.ivMsgWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = "+62"+ whatsapp; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = context.getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(context, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        //sms phone number
        holder.ivMsgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
                intent.putExtra("sms_body", "");
                context.startActivity(intent);
            }
        });

        //expandalbel recyclerbiew
        final Member member = list.get(pos);

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Cons.TAG, "onClick: " + pos);
                boolean expanded = member.isExpanded();

                if (!expanded){
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

                    holder.llActivity.setVisibility(LinearLayout.VISIBLE);
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }else{
                    holder.llActivity.setVisibility(LinearLayout.GONE);
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }

                member.setExpanded(!expanded);
                notifyItemChanged(pos);

            }
        });

    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

                /*holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                holder.llActivity.setVisibility(LinearLayout.VISIBLE);

                }else{
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    holder.llActivity.setVisibility(LinearLayout.GONE);
                }
            }
        });
    }*/

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInstitution, tvWhatsapp, tvPhone, tvTimes, tvEmail, tvActivity, tvInactive;
        LinearLayout llEmail, llActivity, llPhone, llWa;
        CircleImageView iv, ivEmail, ivSendMail, ivCallWa, ivMsgWa, ivCallPhone, ivMsgPhone;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_rv_name);
            tvInstitution = (TextView) itemView.findViewById(R.id.tv_rv_institution);
            tvWhatsapp = (TextView) itemView.findViewById(R.id.tv_rv_whatsapp);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_rv_phone);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_rv_email);
            tvTimes = (TextView) itemView.findViewById(R.id.tv_rv_times);
            tvActivity = (TextView) itemView.findViewById(R.id.tv_rv_activity);
            tvInactive = (TextView) itemView.findViewById(R.id.tv_rv_inactive);
            iv = (CircleImageView) itemView.findViewById(R.id.iv);
            ivSendMail = (CircleImageView) itemView.findViewById(R.id.iv_send_email);
            ivCallPhone = (CircleImageView) itemView.findViewById(R.id.iv_call_phone);
            ivCallWa = (CircleImageView) itemView.findViewById(R.id.iv_call_wa);
            ivMsgPhone = (CircleImageView) itemView.findViewById(R.id.iv_msg_phone);
            ivMsgWa = (CircleImageView) itemView.findViewById(R.id.iv_msg_wa);
            ivEmail = (CircleImageView) itemView.findViewById(R.id.iv_send_email);
            llPhone = (LinearLayout) itemView.findViewById(R.id.ll_phone);
            llEmail = (LinearLayout) itemView.findViewById(R.id.ll_email);
            llWa = (LinearLayout) itemView.findViewById(R.id.ll_wa);
            llActivity = (LinearLayout) itemView.findViewById(R.id.ll_rv_activity);
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