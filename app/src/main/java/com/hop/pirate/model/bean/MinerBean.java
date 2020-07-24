package com.hop.pirate.model.bean;

import org.json.JSONException;
import org.json.JSONObject;

import androidLib.AndroidLib;

public class MinerBean {
    private Long id;
    private String MID;
    private String zone;
    private double time;
    private String IP;
    private boolean isSelected;
    private String minerPoolAdd;

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

    public MinerBean(Long id, String MID, String zone, double time, String IP,
                     boolean isSelected, String minerPoolAdd) {
        this.id = id;
        this.MID = MID;
        this.zone = zone;
        this.time = time;
        this.IP = IP;
        this.isSelected = isSelected;
        this.minerPoolAdd = minerPoolAdd;
    }

    public MinerBean() {
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
                double ping = obj.optDouble("ping");
                if (ping == -1) {
                    this.setTime(-1);
                } else {
                    this.setTime(ping);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMinerPoolAdd() {
        return this.minerPoolAdd;
    }

    public void setMinerPoolAdd(String minerPoolAdd) {
        this.minerPoolAdd = minerPoolAdd;
    }
}
