package com.hendropurwoko.eventmanagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onBindViewHolder(@NonNull CardViewHolder holder, int i) {
        pos = i;

        final String id = list.get(i).getId();
        final String name = list.get(i).getName();
        final String institution = list.get(i).getInstitution();
        final String whatsapp = list.get(i).getWhatsapp();
        final String phone = list.get(i).getPhone();
        final String email = list.get(i).getEmail();
        final String active = list.get(i).getActive();
        final String input = list.get(i).getInput();

        holder.tv_name.setText(name);
        holder.tv_institution.setText(institution);
        holder.tv_phone.setText(phone);
        holder.tv_input.setText(input);

        if (active.equals("No")){
            holder.tv_inactive.setVisibility(TextView.VISIBLE);
            //holder.ll.setBackgroundResource(R.drawable.red_pastel_round_corner);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            TextView d_tv_name, d_tv_institution, d_tv_phone, d_tv_whatsapp, d_tv_email;

            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("bid", id);
                b.putString("bname", name);
                b.putString("binstitution", institution);
                b.putString("bemail", email);
                b.putString("bphone", phone);
                b.putString("bwhatsapp", whatsapp);
                b.putString("bactive", active);

                ParticipantFormFragment fragment = new ParticipantFormFragment("edit");
                fragment.setArguments(b);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();

                MainAppActivity.hideBar();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wrap) LinearLayout ll;
        @BindView(R.id.tv_rv_name) TextView tv_name;
        @BindView(R.id.tv_rv_campus) TextView tv_institution;
        @BindView(R.id.tv_rv_phone) TextView tv_phone;
        @BindView(R.id.tv_rv_input) TextView tv_input;
        @BindView(R.id.tv_rv_inactive) TextView tv_inactive;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /* Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Participant> members) {
        list = members;
        notifyDataSetChanged();
    }*/
}