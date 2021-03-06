package com.vitaliir.taxcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vitaliir.taxcalculator.model.MainPage;
import com.vitaliir.taxcalculator.utils.Parser;
import com.vitaliir.taxcalculator.utils.Tooltip;
import com.vitaliir.taxcalculator.utils.Validator;
import org.json.JSONObject;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String DEFAULT_RESULT = "0.0";
    private final static String DIGITS_NUM_FORMAT = "%1.9s";
    private static final String EMPTY_STRING = "";
    private final static String ESV_TOOLTIP = "Єдиний соціальний внесок\n = Мін. зарплата х 22%";
    private final static String EP_TOOLTIP = "Єдиний податок. \nНаприклад, для 3 групи - 5%";
    private final static String TITLE = "Некоректні дані!";
    private final static String EMPTY_INCOME = "Заповніть, будь ласка, поле 'Дохід'";
    private final static String EMPTY_CLEAN_INCOME = "Зробіть, будь ласка, розрахунок!";

    private Validator validator;
    private Parser parser;
    private Tooltip tooltip;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validator = new Validator();
        parser = new Parser();
        tooltip = new Tooltip();

        txtTransBankCommission = findViewById(R.id.txtTransBankCommission);
        txtTransBankCommissionResult = findViewById(R.id.txtTransBankCommissionResult);
        txtAllBankCommissions = findViewById(R.id.txtAllBankCommissions);
        txtAllTaxes = findViewById(R.id.txtAllTaxes);
        txtCleanIncome = findViewById(R.id.txtCleanIncome);
        txtOtherCommissions = findViewById(R.id.txtOtherCommission);
        txtIncome = findViewById(R.id.txtIncome);
        txtEsv = findViewById(R.id.txtEsv);
        txtEp = findViewById(R.id.txtEp);

        fillInInputData();

        TextView lblEsv = findViewById(R.id.lblEsv);
        TextView lblEp = findViewById(R.id.lblEp);
        lblEsv.setOnClickListener(this);
        lblEp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lblEsv) {
            tooltip.showTextViewTooltip((TextView) view, ESV_TOOLTIP);
        } else if (view.getId() == R.id.lblEp) {
            tooltip.showTextViewTooltip((TextView) view, EP_TOOLTIP);
        }
    }

    public void btnClear_Click(View view) {
        txtIncome.setText(EMPTY_STRING);
        txtEsv.setText(EMPTY_STRING);
        txtEp.setText(EMPTY_STRING);
        txtTransBankCommission.setText(EMPTY_STRING);
        txtOtherCommissions.setText(EMPTY_STRING);
        txtTransBankCommissionResult.setText(DEFAULT_RESULT);
        txtAllBankCommissions.setText(DEFAULT_RESULT);
        txtAllTaxes.setText(DEFAULT_RESULT);
        txtCleanIncome.setText(DEFAULT_RESULT);
    }

    public void btnResult_Click(View view) {
        isDataCorrect(txtIncome, TITLE, EMPTY_INCOME);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        double taxes = countTax();
        double cleanIncomeAfterTaxes = countIncomeAfterTaxes(taxes);
        double otherCommissions = getOtherCommissions();
        double bankTransferCommission = countBankTransferCommission(cleanIncomeAfterTaxes, otherCommissions);

        String allTaxes = String.format(DIGITS_NUM_FORMAT, decimalFormat.format(taxes));
        String cleanIncome = String.format(DIGITS_NUM_FORMAT,
                decimalFormat.format(cleanIncomeAfterTaxes - bankTransferCommission - otherCommissions));
        String allBankCommissions = String.format(DIGITS_NUM_FORMAT,
                decimalFormat.format(bankTransferCommission + otherCommissions));
        String bankTransferCommissionResult = String.format(DIGITS_NUM_FORMAT,
                decimalFormat.format(bankTransferCommission));

        txtAllTaxes.setText(allTaxes);
        txtCleanIncome.setText(cleanIncome);
        txtAllBankCommissions.setText(allBankCommissions);
        txtTransBankCommissionResult.setText(bankTransferCommissionResult);
    }

    public void btnSave_Click(View view) {
        if (isDataCorrect(txtCleanIncome, TITLE, EMPTY_CLEAN_INCOME)) {
            saveData();
        }
    }

    public void btnSaved_Click(View view) {
        Intent intent = new Intent(this, SavedPageActivity.class);
        startActivity(intent);
    }

    private void fillInInputData() {
        Intent intent = getIntent();
        txtTransBankCommission.setText(intent.getStringExtra("txtTransBankCommission"));
        txtTransBankCommissionResult.setText(intent.getStringExtra("txtTransBankCommissionResult"));
        txtAllBankCommissions.setText(intent.getStringExtra("txtAllBankCommissions"));
        txtAllTaxes.setText(intent.getStringExtra("txtAllTaxes"));
        txtCleanIncome.setText(intent.getStringExtra("txtCleanIncome"));
        txtOtherCommissions.setText(intent.getStringExtra("txtOtherCommissions"));
        txtIncome.setText(intent.getStringExtra("txtIncome"));
        txtEsv.setText(intent.getStringExtra("txtEsv"));
        txtEp.setText(intent.getStringExtra("txtEp"));
    }

    private boolean isDataCorrect(TextView field, @SuppressWarnings("SameParameterValue") String title, String message) {
        if (validator.isEmpty(field) || validator.isZero(field)) {
            TaxDialog dialog = new TaxDialog(title, message);
            dialog.show(getSupportFragmentManager(), "InvalidInputTag");
            return false;
        }
        return true;
    }

    private double countTax() {
        double income = parser.tryParseDouble(txtIncome.getText().toString());
        double esv = parser.tryParseDouble(txtEsv.getText().toString());
        double ep = parser.tryParseDouble(txtEp.getText().toString());

        return income * (ep / 100) + esv;
    }

    private double countIncomeAfterTaxes(double tax) {
        double income = parser.tryParseDouble(txtIncome.getText().toString());
        return income - tax;
    }

    private double countBankTransferCommission(double incomeAfterTaxes, double otherCommissions) {
        double readyForTransfer = incomeAfterTaxes - otherCommissions;
        double percent = (getTransBankCommission() / 100) + 1;
        double salaryWithoutCommission = readyForTransfer / percent;
        return readyForTransfer - salaryWithoutCommission;
    }

    private double getTransBankCommission() {
        String replaced = txtTransBankCommission.getText().toString();
        return parser.tryParseDouble(replaced);
    }

    private double getOtherCommissions() {
        String otherCommission = txtOtherCommissions.getText().toString();
        return parser.tryParseDouble(otherCommission);
    }

    @SneakyThrows
    private void saveData() {
        String formattedTime = getDateTimeNow();
        String json = getMainPageData().toString();
        JSONObject jsonObject = new JSONObject(json);

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(formattedTime, jsonObject.toString());
        editor.apply();

        Toast.makeText(getApplicationContext(), "Збережено!", Toast.LENGTH_SHORT).show();
    }

    private String getDateTimeNow(){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dateTimeNow = LocalDateTime.now();
        return dateFormatter.format(dateTimeNow);
    }

    private MainPage getMainPageData(){
        MainPage mainPage = new MainPage();
        mainPage.setIncome(txtIncome.getText().toString());
        mainPage.setEsv(txtEsv.getText().toString());
        mainPage.setEp(txtEp.getText().toString());
        mainPage.setBankCommission(txtTransBankCommission.getText().toString());
        mainPage.setOtherCommission(txtOtherCommissions.getText().toString());
        mainPage.setAllTaxes(txtAllTaxes.getText().toString());
        mainPage.setTransBankCommission(txtTransBankCommissionResult.getText().toString());
        mainPage.setAllBankCommission(txtAllBankCommissions.getText().toString());
        mainPage.setCleanIncome(txtCleanIncome.getText().toString());
        return mainPage;
    }
}