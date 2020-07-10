package com.hop.pirate.model.bean;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/9 11:57 AM
 */
public class AppVersionBean {
    private int newVersion;
    private int minversion;
    private String updateMsgEN;
    private String updateStr;
    private String updateMsgCN;

    public AppVersionBean(int newVersion, String updateMsgChina, String updateMsgEnglish) {
        this.newVersion = newVersion;
        this.updateMsgCN = updateMsgChina;
        this.updateMsgEN = updateMsgEnglish;
    }


    public int getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(int newVersion) {
        this.newVersion = newVersion;
    }

    public AppVersionBean() {
    }


    public String getUpdateMsgCN() {
        return updateMsgCN;
    }

    public void setUpdateMsgCN(String updateMsgCN) {
        this.updateMsgCN = updateMsgCN;
    }

    public String getUpdateMsgEN() {
        return updateMsgEN;
    }

    public void setUpdateMsgEN(String updateMsgEN) {
        this.updateMsgEN = updateMsgEN;
    }


    public AppVersionBean(String updateStr) {
        this.updateStr = updateStr;
    }

    public int getMinversion() {
        return minversion;
    }

    public void setMinversion(int minversion) {
        this.minversion = minversion;
    }
}
