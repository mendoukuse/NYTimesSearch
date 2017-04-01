package com.codepath.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.fragments.DatePickerFragment;
import com.codepath.nytimessearch.models.Filters;
import com.codepath.nytimessearch.models.SortOrder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {
    TextView tvBeginDate;
    Spinner spSortOrder;
    Filters filters;
    SimpleDateFormat sdf;

    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch(buttonView.getId()) {
                case R.id.cbArts:
                    if (isChecked) {
                        filters.addCategory("Arts");
                    } else {
                        filters.removeCategory("Arts");
                    }
                    break;
                case R.id.cbFashion:
                    if (isChecked) {
                        filters.addCategory("Fashion & Style");
                    } else {
                        filters.removeCategory("Fashion & Style");
                    }
                    break;
                case R.id.cbSports:
                    if (isChecked) {
                        filters.addCategory("Sports");
                    } else {
                        filters.removeCategory("Sports");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        filters = (Filters) getIntent().getSerializableExtra("filters");

        if (filters == null) {
            filters = new Filters();
        }

        setUpDateSetting();
        setUpSpinner();
        setUpCheckboxes();
    }

    private void setUpSpinner() {
        spSortOrder = (Spinner) findViewById(R.id.spSortOrder);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                SortOrder.getNames());

        spSortOrder.setAdapter(adapter);
        spSortOrder.setSelection(adapter.getPosition(filters.getSortOrder()));
    }

    private void setUpCheckboxes() {
        CheckBox cbArts = (CheckBox) findViewById(R.id.cbArts);
        CheckBox cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        CheckBox cbSports = (CheckBox) findViewById(R.id.cbSports);

        cbArts.setOnCheckedChangeListener(checkListener);
        cbFashion.setOnCheckedChangeListener(checkListener);
        cbSports.setOnCheckedChangeListener(checkListener);
    }

    private void setUpDateSetting() {
        sdf = new SimpleDateFormat("MM/dd/yyyy");
        tvBeginDate = (TextView) findViewById(R.id.tvBeginDate);
        tvBeginDate.setText(sdf.format(filters.getBeginDate().getTime()));
    }

    public void showCalendarDialogFragment(View v) {
        DialogFragment newFragment = DatePickerFragment.newInstance(filters.getBeginDate());
        newFragment.setCancelable(true);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = filters.getBeginDate();
        c.set(year, month, day);
        filters.setBeginDate(c);
        tvBeginDate.setText(sdf.format(c.getTime()));
    }

    public void saveSettingsFilters(View view) {
        Intent settings = new Intent();

        filters.setSortOrder(spSortOrder.getSelectedItem().toString());
        settings.putExtra("filters", filters);
        setResult(RESULT_OK, settings);
        finish();
    }
}
