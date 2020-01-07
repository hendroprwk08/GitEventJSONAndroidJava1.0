package com.hendropurwoko.eventmanagement;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

public class BottomSheetNavigationFragment extends BottomSheetDialogFragment {

    SharedPref sp;
    ImageView ivPhoto;
    TextView tvUsername, tvEmail;

    public static BottomSheetNavigationFragment newInstance() {

        Bundle args = new Bundle();

        BottomSheetNavigationFragment fragment = new BottomSheetNavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            //check the slide offset and change the visibility of close button
            if (slideOffset > 0.5) {
                closeButton.setVisibility(View.VISIBLE);
            } else {
                closeButton.setVisibility(View.GONE);
            }
        }
    };

    private ImageView closeButton;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Get the content View
        View contentView = View.inflate(getContext(), R.layout.bottom_navigation_drawer, null);
        dialog.setContentView(contentView);

        NavigationView navigationView = contentView.findViewById(R.id.navigation_view);

        tvUsername = (TextView) contentView.findViewById(R.id.drawer_username);
        tvEmail = (TextView) contentView.findViewById(R.id.drawer_email);
        ivPhoto = (ImageView) contentView.findViewById(R.id.drawer_profile_image);

        //set data dari shared preferences
        sp = new SharedPref(getContext());

        //ambil SP
        if (sp.cekSharedPreferences()) {
            SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("em", Context.MODE_PRIVATE);

            tvUsername.setText(mSharedPreferences.getString("sp_username", null));
            tvEmail.setText(mSharedPreferences.getString("sp_email", null));

            String foto = mSharedPreferences.getString("sp_foto", null);
            if (foto != null) { //kalo ada gambar
                String location = Cons.PHOTO_BASE_URL + foto;
                Glide.with(getContext())
                        .load(location)
                        .centerCrop()
                        .placeholder(R.drawable.ic_person_grey_24dp)
                        .into(ivPhoto);
            }
        }

        //implement navigation menu item click event
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                dismiss(); //close bottom sheet dialog

                MainAppActivity m = new MainAppActivity();

                switch (item.getItemId()) {
                    case R.id.menu_member:
                        break;
                    case R.id.menu_user:
                        Cons.ACTIVE_FRAGMENT = "user";

                        //panggil method yang ada di activity lain
                        ((MainAppActivity)getActivity()).loadFragment(new UserFragment());

                        break;
                    case R.id.menu_profile:
                        Cons.LAST_FRAGMENT = Cons.ACTIVE_FRAGMENT;
                        Cons.ACTIVE_FRAGMENT = "profile";

                        //panggil method yang ada di activity lain
                        ((MainAppActivity)getActivity()).loadFragment(new ProfileFragment());
                        ((MainAppActivity)getActivity()).hideBar();

                        break;
                }

                return false;
            }
        });
        closeButton = contentView.findViewById(R.id.close_image_view);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss bottom sheet
                dismiss();
            }
        });

        //Set the coordinator layout behavior
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        //Set callback
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }
}