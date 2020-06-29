package com.hop.pirate.model.bean;

public class FlowBean {

    public FlowBean(double flow, double hop, int type) {
        this.flow = flow;
        this.hop = hop;
        this.type = type;
    }

    private double flow;
    private double hop;
    private int type;//1 custom

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected;

    public double getFlow() {
        return flow;
    }

    public double getHop() {
        return hop;
    }

    public int getType() {
        return type;
    }
}
