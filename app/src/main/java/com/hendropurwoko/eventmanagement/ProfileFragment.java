package com.hendropurwoko.eventmanagement;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.BitSet;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    final static int IMAGE_PICK_CODE = 1000;
    final static int PERMISSION_CODE = 1001;

    ImageView iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        iv = (ImageView) view.findViewById(R.id.iv_profile);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) ==
                            PackageManager.PERMISSION_DENIED){
                        //tidak diijinkan, minta ijin
                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};

                        //show popup permission
                        requestPermissions(permission, PERMISSION_CODE);
                    }else{
                       //sudah granted
                        pickImageFromGallery();
                    }
                }else{
                    //os < Marshmallow
                }
            }
        });

        final Button btLogout = (Button) view.findViewById(R.id.bt_profile_logout);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure want to logout");
                alertDialogBuilder.setPositiveButton("Yes, sure",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //remove share preferences
                                clearPreferences();

                                if (!cekSharedPreferences()) {
                                    Intent i = new Intent(getActivity(), LoginActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getActivity().startActivity(i);
                                }else{
                                    Toast.makeText(getActivity(), "Gagal", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        final EditText etUsername = (EditText) view.findViewById(R.id.et_profile_username);
        final EditText etEmail = (EditText) view.findViewById(R.id.et_profile_email);

        //ambil SP
        if (cekSharedPreferences()) {
            SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("em", Context.MODE_PRIVATE);

            etUsername.setText(mSharedPreferences.getString("sp_username", ""));
            etEmail.setText(mSharedPreferences.getString("sp_email", "0"));
        }

        return  view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }else{
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //catch result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri uri = data.getData();
            iv.setImageURI(uri);
        }
    }

    void clearPreferences(){
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("em", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.clear().commit();
    }

    private boolean cekSharedPreferences() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences("em",0);
        String str = mPrefs.getString("sp_email", "");

        if (str.length() != 0) return true;
        return false;
    }
}
