package com.hop.pirate.model.bean;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:28 PM
 */
public class WalletBean {
    /**
     * Main : 0xc5Bde4Eb52f4EaF51F5d80F0D576C6E90b5b78D7
     * Sub : HO9dvb91YZmM23XGJwv1vKSw9tiABqv8mEyHpg9dskbDcu
     * Eth : 0
     * Hop : 0
     * Approved : 0
     * IsOpen : false
     */

    private String Main;
    private String Sub;
    private double Eth;
    private double Hop;
    private double Approved;
    private boolean IsOpen;

    public String getMain() {
        return Main;
    }

    public void setMain(String Main) {
        this.Main = Main;
    }

    public String getSub() {
        return Sub;
    }

    public void setSub(String Sub) {
        this.Sub = Sub;
    }

    public double getEth() {
        return Eth;
    }

    public void setEth(double Eth) {
        this.Eth = Eth;
    }

    public double getHop() {
        return Hop;
    }

    public void setHop(double Hop) {
        this.Hop = Hop;
    }

    public double getApproved() {
        return Approved;
    }

    public void setApproved(double Approved) {
        this.Approved = Approved;
    }

    public boolean isIsOpen() {
        return IsOpen;
    }

    public void setIsOpen(boolean IsOpen) {
        this.IsOpen = IsOpen;
    }
}
