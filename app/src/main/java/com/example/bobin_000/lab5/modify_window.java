package com.example.bobin_000.lab5;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * Created by bobin_000 on 6/25/2016.
 */
public class modify_window extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Display Metrics
        DisplayMetrics dm = new DisplayMetrics();

        //Get details of display
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Set width and height of total screen
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //Setting popup window to 80% width and 60% height of total screen
        getWindow().setLayout((int)(width*.8), (int)(height*.6));
    }
}
