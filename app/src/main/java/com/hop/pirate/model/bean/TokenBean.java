package com.hop.pirate.model.bean;


public class TokenBean {

    private String name;
    private double banlance;
    private boolean isChecked;

    public TokenBean(String name, double banlance, boolean isChecked) {
        this.name = name;
        this.banlance = banlance;
        this.isChecked = isChecked;
    }

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
