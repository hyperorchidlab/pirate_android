package com.hop.pirate.model.bean;

public class TokenBean {
    public TokenBean(String name, double banlance, boolean isChecked) {
        this.name = name;
        this.banlance = banlance;
        this.isChecked = isChecked;
    }

    private String name;
    private double banlance;
    private boolean isChecked;

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
}
