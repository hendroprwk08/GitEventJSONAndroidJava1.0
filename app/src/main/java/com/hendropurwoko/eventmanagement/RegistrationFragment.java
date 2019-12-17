package com.hendropurwoko.eventmanagement;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_registration, container, false);

        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
        pb.setVisibility(ProgressBar.VISIBLE);

        final WebView webView = (WebView) view.findViewById(R.id.web_view);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                pb.setVisibility(ProgressBar.GONE);
            }
        });

        String url = Cons.BASE_URL +"registration.html";
        webView.loadUrl(url);

        return view;
    }

}
