package com.hop.pirate.model.bean;

public class OperationBean {
    private String title;
    private String subtitle;

    public OperationBean(String title, String subtitle, int imageResource) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageResource = imageResource;
    }

    public OperationBean() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    private int imageResource;
}
