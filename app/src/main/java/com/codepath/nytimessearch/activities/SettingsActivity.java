package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.codepath.nytimessearch.R;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    public void saveSettingsFilters(View view) {
        Intent settings = new Intent();
        settings.putExtra("beginDate", "stub");
        settings.putExtra("sortOrder", "stub");
        settings.putStringArrayListExtra("categories", new ArrayList<String>());

        setResult(RESULT_OK, settings);
        finish();
    }
}
