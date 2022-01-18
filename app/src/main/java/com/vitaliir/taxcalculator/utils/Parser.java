package com.vitaliir.taxcalculator.utils;

public class Parser {

    public double tryParseDouble(String value) {
        double result = 0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return result;
        }
    }
}
