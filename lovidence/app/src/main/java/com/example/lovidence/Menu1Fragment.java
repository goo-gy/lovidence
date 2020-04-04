package com.example.lovidence;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.URI;

public class Menu1Fragment extends Fragment {
    ViewGroup viewGroup;
    WebView wView;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       /* wView = (WebView)getView().findViewById(R.id.webView);
        Intent intent = getActivity().getIntent();
        Uri data = intent.getData();
        wView.setWebViewClient(new WebViewClient());

        if(data!=null) wView.loadUrl(data.toString());
        else wView.loadUrl("http://google.com/");*/
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup =(ViewGroup) inflater.inflate(R.layout.fragment_menu1, container, false);
        wView = (WebView) viewGroup.findViewById(R.id.webView);
        Intent intent = getActivity().getIntent();
        Uri data = intent.getData();
        wView.getSettings().setJavaScriptEnabled(true);
        wView.setWebViewClient(new WebViewClient());

        if(data!=null) wView.loadUrl(data.toString());
        else wView.loadUrl("http://google.com/");

        return viewGroup;
    }

    private class Callback extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            return (false);
        }
    }
}