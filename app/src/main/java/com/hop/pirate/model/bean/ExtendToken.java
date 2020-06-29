package com.hop.pirate.model.bean;

public class ExtendToken {
    public static String CurPaymentContract="0x4291d9Ff189D90Ba875E0fc1Da4D602406DD7D6e";
    public static String CurTokenI="0xAd44c8493dE3FE2B070f33927A315b50Da9a0e25";
    public static String CurSymbol="HOP";
    public static String TAG = "Extend token Bean";

    private String PaymentContract;
    private String TokenI;
    private String Symbol;
    private double Balance;
    private int Decimal;



    private boolean isChecked;

    public String getPaymentContract() {
        return PaymentContract;
    }

    public String getTokenI() {
        return TokenI;
    }

    public String getSymbol() {
        return Symbol;
    }

    public double getBalance() {
        return Balance;
    }

    public int getDecimal() {
        return Decimal;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


}
