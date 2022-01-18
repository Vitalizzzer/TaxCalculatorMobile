package com.vitaliir.taxcalculator.utils;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class Tooltip {

    public void showTextViewTooltip(TextView textView, String text) {
        com.tooltip.Tooltip tooltip = getTooltip(textView, text);
        tooltip.show();
    }

    public com.tooltip.Tooltip getTooltip(View v, String text) {
        return new com.tooltip.Tooltip.Builder(v)
                .setText(text)
                .setTextColor(Color.WHITE)
                .setBackgroundColor(Color.LTGRAY)
                .setGravity(Gravity.END)
                .setCornerRadius(8f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .build();
    }
}
