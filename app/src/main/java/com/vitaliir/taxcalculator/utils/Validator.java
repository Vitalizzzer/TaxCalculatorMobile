package com.vitaliir.taxcalculator.utils;

import android.widget.TextView;

public class Validator {

    public boolean isEmpty(TextView textView) {
        //TextView txtIncome = findViewById(R.id.txtIncome);
        return textView.getText().toString().contains(" ")
                || textView.getText().toString().length() == 0
                || textView.getText().toString().isEmpty();
    }
}
