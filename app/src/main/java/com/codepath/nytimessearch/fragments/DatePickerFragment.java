package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by christine_nguyen on 4/1/17.
 */
public class DatePickerFragment extends DialogFragment {
    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(Calendar c) {
        DatePickerFragment frag = new DatePickerFragment();
        if (c != null) {
            Bundle args = new Bundle();
            args.putInt("month", c.get(Calendar.MONTH));
            args.putInt("day", c.get(Calendar.DAY_OF_MONTH));
            args.putInt("year", c.get(Calendar.YEAR));
            frag.setArguments(args);
        }
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (getArguments() != null) {
            year = getArguments().getInt("year");
            month = getArguments().getInt("month");
            day = getArguments().getInt("day");
        }

        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

}