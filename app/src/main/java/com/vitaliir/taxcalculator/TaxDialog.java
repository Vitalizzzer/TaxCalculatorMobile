package com.vitaliir.taxcalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class TaxDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Некоректні дані!");
        builder.setMessage("Заповніть, будь ласка, поле 'Дохід'");
        builder.setPositiveButton("OK", (dialog, id) -> {
        });
        return builder.create();
    }
}
