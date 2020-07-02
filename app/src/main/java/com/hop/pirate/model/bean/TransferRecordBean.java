package com.hop.pirate.model.bean;

public class TransferRecordBean {

    private double hopCount;
    private String transfrom;
    private long time;

    public TransferRecordBean(double hopCount, String transfrom, long time) {
        this.hopCount = hopCount;
        this.transfrom = transfrom;
        this.time = time;
    }

    public double getHopCount() {
        return hopCount;
    }

    public void setHopCount(double hopCount) {
        this.hopCount = hopCount;
    }

    public String getTransfrom() {
        return transfrom;
    }

    public void setTransfrom(String transfrom) {
        this.transfrom = transfrom;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
