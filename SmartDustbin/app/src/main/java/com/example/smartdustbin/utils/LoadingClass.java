package com.example.smartdustbin.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.smartdustbin.R;

public class LoadingClass {
    //Objects declaration
    private Activity activity;
    private AlertDialog alertDialog;

    //constructor
    public LoadingClass(Activity activity) {
        this.activity = activity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading_layout, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }

    public void dismissLoading() {
        alertDialog.dismiss();
    }

}
