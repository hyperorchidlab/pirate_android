package com.hop.pirate.model.bean;


public class ExtendToken {
    public static String CurPaymentContract;
    public static String CurTokenI ;
    public static String CurSymbol = "HOP";
    public static String TAG = "Extend token Bean";

    private String paymentContract;
    private String tokenI;
    private String symbol;
    private double balance;
    private int decimal;
    private boolean isChecked;


    public ExtendToken(String paymentContract, String tokenI, String symbol, double balance,
                       int decimal, boolean isChecked) {
        this.paymentContract = paymentContract;
        this.tokenI = tokenI;
        this.symbol = symbol;
        this.balance = balance;
        this.decimal = decimal;
        this.isChecked = isChecked;
    }

    public ExtendToken() {
    }


    public String getPaymentContract() {
        return paymentContract;
    }

    public String getTokenI() {
        return tokenI;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getBalance() {
        return balance;
    }

    public int getDecimal() {
        return decimal;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setPaymentContract(String paymentContract) {
        this.paymentContract = paymentContract;
    }

    public void setTokenI(String tokenI) {
        this.tokenI = tokenI;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


}
