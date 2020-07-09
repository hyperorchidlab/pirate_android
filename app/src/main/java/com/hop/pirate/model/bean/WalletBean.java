package com.hop.pirate.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 4:28 PM
 */
@Entity
public class WalletBean {
    /**
     * Main : 0xc5Bde4Eb52f4EaF51F5d80F0D576C6E90b5b78D7
     * Sub : HO9dvb91YZmM23XGJwv1vKSw9tiABqv8mEyHpg9dskbDcu
     * Eth : 0
     * Hop : 0
     * Approved : 0
     * IsOpen : false
     */

    @Id(autoincrement = true)
    private Long id;
    private String Main;
    private String Sub;
    private double Eth;
    private double Hop;
    private double Approved;
    private boolean IsOpen;

    @Generated(hash = 1687182343)
    public WalletBean(Long id, String Main, String Sub, double Eth, double Hop,
                      double Approved, boolean IsOpen) {
        this.id = id;
        this.Main = Main;
        this.Sub = Sub;
        this.Eth = Eth;
        this.Hop = Hop;
        this.Approved = Approved;
        this.IsOpen = IsOpen;
    }

    @Generated(hash = 1814219826)
    public WalletBean() {
    }

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsOpen() {
        return this.IsOpen;
    }
}
