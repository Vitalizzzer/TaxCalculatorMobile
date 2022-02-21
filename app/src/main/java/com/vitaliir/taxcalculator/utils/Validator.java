package com.vitaliir.taxcalculator.utils;

import android.widget.TextView;

public class Validator {

    public boolean isEmpty(TextView textView) {
        return textView.getText().toString().contains(" ")
                || textView.getText().toString().length() == 0
                || textView.getText().toString().isEmpty();
    }

    public boolean isZero(TextView textView) {
        return textView.getText().toString().equals("0")
                || textView.getText().toString().equals("0.0");
    }
}
