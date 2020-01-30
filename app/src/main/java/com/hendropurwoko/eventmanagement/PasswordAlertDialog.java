package com.hendropurwoko.eventmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.androidnetworking.common.Priority;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PasswordAlertDialog extends DialogFragment {
    @BindView(R.id.et_ad_pw) EditText etPassword;

    private Unbinder unbinder;

    String email;
    View alertView;

    public PasswordAlertDialog() {}

    public static PasswordAlertDialog newInstance(String email) {
        PasswordAlertDialog frag = new PasswordAlertDialog();
        Bundle args = new Bundle();
        args.putString("b_title", email);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        alertView = View.inflate(getContext(), R.layout.alertdialog_change_password_form, null);
        unbinder = ButterKnife.bind(this, alertView);
        return alertView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = getArguments().getString("b_title");
        //getDialog().setTitle(title);
        getDialog().setCancelable(true);

        // Show soft keyboard automatically and request focus to field
        etPassword.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.bt_ad_replace_password)
    public void replace(){
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
}
