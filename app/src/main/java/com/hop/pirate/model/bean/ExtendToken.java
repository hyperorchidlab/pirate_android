package com.hop.pirate.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ExtendToken {
    public static String CurPaymentContract = "0x4291d9Ff189D90Ba875E0fc1Da4D602406DD7D6e";
    public static String CurTokenI = "0xAd44c8493dE3FE2B070f33927A315b50Da9a0e25";
    public static String CurSymbol = "HOP";
    public static String TAG = "Extend token Bean";

    private String PaymentContract;
    private String TokenI;
    private String Symbol;
    private double Balance;
    private int Decimal;


    private boolean isChecked;

    @Generated(hash = 1977001439)
    public ExtendToken(String PaymentContract, String TokenI, String Symbol, double Balance,
                       int Decimal, boolean isChecked) {
        this.PaymentContract = PaymentContract;
        this.TokenI = TokenI;
        this.Symbol = Symbol;
        this.Balance = Balance;
        this.Decimal = Decimal;
        this.isChecked = isChecked;
    }

    @Generated(hash = 1437756994)
    public ExtendToken() {
    }

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

    public void setPaymentContract(String PaymentContract) {
        this.PaymentContract = PaymentContract;
    }

    public void setTokenI(String TokenI) {
        this.TokenI = TokenI;
    }

    public void setSymbol(String Symbol) {
        this.Symbol = Symbol;
    }

    public void setBalance(double Balance) {
        this.Balance = Balance;
    }

    public void setDecimal(int Decimal) {
        this.Decimal = Decimal;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


}
