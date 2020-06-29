package com.hop.pirate.model.bean;

import android.text.TextUtils;

import org.json.JSONObject;

import androidLib.AndroidLib;

public class UserAccountData {

    private String user;
    private String pool;
    private double inRecharge;
    private String expire;
    private int nonce;
    private double token;
    private double packets;

    private int epoch;
    private double credit;
    private int microNonce;

    public int getEpoch(){
        return epoch;
    }

    public int getMiNonce(){
        return microNonce;
    }

    public double getCredit(){
        return credit;
    }

    public double getPackets(){
        return packets;
    }
    public String getExpire() {
        return expire;
    }

    public double getToken() {
        return token;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public double getInRecharge() {
        return inRecharge;
    }

    public void setInRecharge(double inRecharge) {
        this.inRecharge = inRecharge;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public void setToken(double token) {
        this.token = token;
    }

    public void setPackets(double packets) {
        this.packets = packets;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public int getMicroNonce() {
        return microNonce;
    }

    public void setMicroNonce(int microNonce) {
        this.microNonce = microNonce;
    }
}
