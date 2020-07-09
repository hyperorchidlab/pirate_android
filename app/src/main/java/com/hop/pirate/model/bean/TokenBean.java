package com.hop.pirate.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TokenBean {

    private String name;
    private double banlance;
    private boolean isChecked;

    @Generated(hash = 1005833175)
    public TokenBean(String name, double banlance, boolean isChecked) {
        this.name = name;
        this.banlance = banlance;
        this.isChecked = isChecked;
    }

    @Generated(hash = 1886787915)
    public TokenBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBanlance() {
        return banlance;
    }

    public void setBanlance(double banlance) {
        this.banlance = banlance;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
