package com.vitaliir.taxcalculator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tooltip.Tooltip;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String DEFAULT_RESULT = "0.0";
    private TextView txtTransBankCommission;
    private TextView txtTransBankCommissionResult;
    private TextView txtAllBankCommissions;
    private TextView txtAllTaxes;
    private TextView txtCleanIncome;
    private TextView txtOtherCommissions;
    private TextView txtIncome;
    private TextView txtEsv;
    private TextView txtEp;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lblEsv) {
            showTooltip(view, "Єдиний соціальний внесок\n = Мін. зарплата х 22%");
        } else if (view.getId() == R.id.lblEp) {
            showTooltip(view, "Єдиний податок. Для 3 групи - 5%");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTransBankCommission = findViewById(R.id.txtTransBankCommission);
        txtTransBankCommissionResult = findViewById(R.id.txtTransBankCommissionResult);
        txtAllBankCommissions = findViewById(R.id.txtAllBankCommissions);
        txtAllTaxes = findViewById(R.id.txtAllTaxes);
        txtCleanIncome = findViewById(R.id.txtCleanIncome);
        txtOtherCommissions = findViewById(R.id.txtOtherCommission);
        txtIncome = findViewById(R.id.txtIncome);
        txtEsv = findViewById(R.id.txtEsv);
        txtEp = findViewById(R.id.txtEp);
        TextView lblEsv = findViewById(R.id.lblEsv);
        TextView lblEp = findViewById(R.id.lblEp);
        lblEsv.setOnClickListener(this);
        lblEp.setOnClickListener(this);
    }

    public void btnClear_Click(View view) {
        txtIncome.setText("");
        txtEsv.setText("");
        txtEp.setText("");
        txtTransBankCommission.setText("");
        txtOtherCommissions.setText("");
        txtTransBankCommissionResult.setText(DEFAULT_RESULT);
        txtAllBankCommissions.setText(DEFAULT_RESULT);
        txtAllTaxes.setText(DEFAULT_RESULT);
        txtCleanIncome.setText(DEFAULT_RESULT);
    }

    public void btnResult_Click(View view) {
        verifyInput();
        double taxResult = countTax();
        double incomeAfterTaxes = countIncomeAfterTaxes(taxResult);

        String otherCommission = replaceDot(txtOtherCommissions.getText().toString());
        double otherCommissionParsed = tryParseDouble(otherCommission);
        double commission = countBankTransferCommission(incomeAfterTaxes, otherCommissionParsed);

        String transBankCommissionResult = String.format("%1.9s", commission);
        String allBankCommissions = String.format("%1.9s", commission + otherCommissionParsed);
        String allTaxes = String.format("%1.9s", taxResult);
        String income = String.format("%1.9s", incomeAfterTaxes - commission - otherCommissionParsed);

        txtTransBankCommissionResult.setText(transBankCommissionResult);
        txtAllBankCommissions.setText(allBankCommissions);
        txtAllTaxes.setText(allTaxes);
        txtCleanIncome.setText(income);
    }

    private void verifyInput() {
        if (isEmpty()) {
            TaxDialog dialog = new TaxDialog();
            dialog.show(getSupportFragmentManager(), "InvalidInputTag");
        }
    }


    private boolean isEmpty() {
        TextView txtIncome = findViewById(R.id.txtIncome);
        return txtIncome.getText().toString().contains(" ")
                || txtIncome.getText().toString().length() == 0
                || txtIncome.getText().toString().isEmpty();
    }

    private double countTax() {
        double income = tryParseDouble(txtIncome.getText().toString());
        double esv = tryParseDouble(txtEsv.getText().toString());
        double ep = tryParseDouble(txtEp.getText().toString());

        return income * (ep / 100) + esv;
    }

    private double tryParseDouble(String value) {
        double result = 0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return result;
        }
    }

    private double countIncomeAfterTaxes(double tax) {
        TextView txtIncome = findViewById(R.id.txtIncome);
        double income = tryParseDouble(txtIncome.getText().toString());
        return income - tax;
    }

    private double countBankTransferCommission(double incomeAfterTaxes, double otherCommissions) {
        double readyForTransfer = incomeAfterTaxes - otherCommissions;
        String replaced = replaceDot(txtTransBankCommission.getText().toString());

        double parsedCommission = tryParseDouble(replaced);
        double percent = (parsedCommission / 100) + 1;
        double salaryWithoutCommission = readyForTransfer / percent;

        return readyForTransfer - salaryWithoutCommission;
    }

    private String replaceDot(String input) {
        return input.replace(",", ".");
    }

    private Tooltip getTooltip(View v, String text) {
        TextView textView = (TextView) v;
        return new Tooltip.Builder(textView)
                .setText(text)
                .setTextColor(Color.WHITE)
                .setBackgroundColor(Color.LTGRAY)
                .setGravity(Gravity.END)
                .setCornerRadius(8f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .build();
    }

    private void showTooltip(View view, String text) {
        Tooltip tooltip = getTooltip(view, text);
        tooltip.show();
    }
}