package com.vitaliir.taxcalculator.model;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainPage {

    private String income;
    private String esv;
    private String ep;
    private String bankCommission;
    private String otherCommission;
    private String allTaxes;
    private String transBankCommission;
    private String allBankCommission;
    private String cleanIncome;

    @Override
    @NonNull
    public String toString() {
        return String.format("{\"income\":\"%s\", \"esv\":\"%s\", \"ep\":\"%s\", " +
                        "\"bankCommission\":\"%s\", \"otherCommission\":\"%s\", " +
                        "\"allTaxes\":\"%s\", \"transBankCommission\":\"%s\", " +
                        "\"allBankCommission\":\"%s\", \"cleanIncome\":\"%s\"}",
                income, esv, ep, bankCommission, otherCommission, allTaxes, transBankCommission,
                allBankCommission, cleanIncome);
    }

}
