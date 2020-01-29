package com.hendropurwoko.eventmanagement;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.BitSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    @BindView(R.id.pb) ProgressBar pb;
    @BindView(R.id.tv_progress) TextView tvProgress;
    @BindView(R.id.iv_profile) CircleImageView iv;
    @BindView(R.id.bt_profile_logout) Button btLogout;
    @BindView(R.id.et_profile_username) EditText etUsername;
    @BindView(R.id.et_profile_email) EditText etEmail;
    @BindView(R.id.bt_profile_cpassword) Button btChangePassword;
    @BindView(R.id.bt_profile_upload) Button btUpload;

    //alert dialog
    @Nullable
    @BindView(R.id.et_ad_pw) EditText etPassword;

    final static int IMAGE_PICK_CODE = 1000;
    final static int PERMISSION_CODE = 1001;

    View fragmentView;
    String email, foto;
    SharedPref sp;

    private Unbinder unbinder;

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentView = view;

        unbinder = ButterKnife.bind(this, view); //bind butter knife

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                //use DEXTER
                Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            // start picker to get image for cropping and then use the image in cropping activity
                            CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getContext(), ProfileFragment.this); //<----- untuk fragment
                                //.start(getActivity()); <-- untuk activity
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setMessage("Permission is required to access your images");
                                alertDialogBuilder.setPositiveButton("Yes, sure",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            //tidak diijinkan, minta ijin
                                            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};

                                            //show popup permission
                                            requestPermissions(permission, PERMISSION_CODE);
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
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
            }
        });

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
                            sp.clearPreferences();

                            if (!sp.cekSharedPreferences()) {
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(i);
                                getActivity().finish(); //close current activity
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

        sp = new SharedPref(getContext());

        //ambil SP
        if (sp.cekSharedPreferences()) {
            SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("em", Context.MODE_PRIVATE);

            email = mSharedPreferences.getString("sp_email", null);
            foto = mSharedPreferences.getString("sp_foto", null);

            if (foto != null) { //kalo ada gambar
                String location = Cons.PHOTO_BASE_URL + foto;
                Glide.with(getContext())
                    .load(location)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person_grey_24dp)
                    .into(iv);
            }

            etUsername.setText(mSharedPreferences.getString("sp_username", ""));
            etEmail.setText(email);
        }

        //fast android networking
        AndroidNetworking.initialize(getContext());

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alertDialog = new AlertDialog.Builder(getContext());

                //inf = getActivity().getLayoutInflater();
                //view = inf.inflate(R.layout.alertdialog_change_password_form, null);

                //final View diagView = view;

                //EditText etPassword = (EditText) diagView.findViewById(R.id.et_ad_pw);

                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                view = getLayoutInflater().inflate(R.layout.alertdialog_change_password_form,null);
                unbinder = ButterKnife.bind(fragmentView, view);
                alert.setCancelable(false);
                alert.setView(view);

                final AlertDialog dialog = alert.create();

                //definisi objek
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AndroidNetworking.get(Cons.BASE_URL +"cpassword.php")
                            .addQueryParameter("action", "1")
                            .addQueryParameter("em", email)
                            .addQueryParameter("pw", etPassword.getText().toString().trim())
                            .setTag(Cons.TAG)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Save: ", response.toString());

                                    String status = response.optString("status").toString().trim();

                                    if (status.equals("1")){
                                        Toast.makeText(getContext(),
                                                "Password successfully changed",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getContext(),
                                                "Unsuccessfull",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onError(ANError error) {
                                    Toast.makeText(getContext(),
                                            "Error: " + error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        return  view;
    }

    //catch result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();

                Glide.with(getContext())
                    .load(resultUri.getPath())
                    .centerCrop()
                    .placeholder(R.drawable.ic_person_grey_24dp)
                    .into(iv);

                btUpload.setVisibility(Button.VISIBLE);
                btUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File imageFile = new File(resultUri.getPath());

                        pb.setVisibility(ProgressBar.VISIBLE);
                        tvProgress.setVisibility(TextView.VISIBLE);

                        AndroidNetworking.upload(Cons.BASE_URL +"upload_file.php")
                            .addMultipartFile("image", imageFile)
                            .addMultipartParameter("em", email)
                            .addMultipartParameter("old", foto)
                            .setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                    // show progress dialog
                                    pb.setMax((int) totalBytes);
                                    pb.setProgress((int) bytesUploaded);
                                    tvProgress.setText((int) bytesUploaded +"/"+ pb.getMax());
                                }
                            })
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(Cons.TAG, "onResponse: "+ response);
                                    try {
                                        pb.setVisibility(ProgressBar.GONE);
                                        tvProgress.setVisibility(TextView.GONE);

                                        JSONObject jsonObject = new JSONObject(response);
                                        int status = jsonObject.getInt("status");
                                        String result = jsonObject.getString("result");

                                        if(status == 0){
                                            Toast.makeText(getContext(), "Unabled to upload image: " + result, Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getContext(), result +" uploaded", Toast.LENGTH_SHORT).show();

                                            sp.updateSharedPreferences("sp_foto", result);
                                            foto = result;
                                        }

                                        btUpload.setVisibility(Button.GONE);
                                    } catch (JSONException e) {
                                        pb.setVisibility(ProgressBar.GONE);
                                        tvProgress.setVisibility(TextView.GONE);

                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    anError.printStackTrace();
                                    Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
