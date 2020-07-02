package com.hop.pirate.model.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import androidLib.AndroidLib;

public class MinerBean implements Serializable {
    private String MID;
    private String zone;
    private double time;
    private String IP;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public MinerBean(String mid, String zone) {
        this.MID = mid;
        this.zone = zone;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getZone() {
        return this.zone;
    }

    public void setZone(String z) {
        this.zone = z;
    }

    public String getIP() {
        return this.IP;
    }

    public void setIP(String ip) {
        this.IP = ip;
    }

    @Override
    public String toString() {
        return "[MinerBean]ZONE:" + zone + " time:" + time + " ip:" + IP + " mid:" + MID;
    }

    public void TestPing() {
        String jsonStr = AndroidLib.testPing(this.MID);
        if (jsonStr.equals("")) {
            this.setIP("None");
            this.setTime(-1);
        } else {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                this.setIP(obj.optString("ip"));
                this.setTime(obj.optDouble("ping"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
