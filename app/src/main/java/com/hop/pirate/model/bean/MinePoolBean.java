package com.hop.pirate.model.bean;


import androidLib.AndroidLib;

public class MinePoolBean {
    public static String TAG = "Miner Pool Bean";

    public static void syncPoolsAndUserData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidLib.syncPoolsAndUserData();
            }
        }).start();
    }

    public MinePoolBean() {

    }

    public MinePoolBean(String name, String websiteAddress, String email, int miningMachineNumber, int mortgage, int user) {
        this.name = name;
        this.websiteAddress = websiteAddress;
        mineMachineNumber = miningMachineNumber;
        mortgageNumber = mortgage;
        this.email = email;
        this.userNumber = user;
    }

    public MinePoolBean(String address, double mortgageNumber, String name, String websiteAddress, String email,
                        int userNumber, int mineMachineNumber, boolean isSelected) {
        this.address = address;
        this.mortgageNumber = mortgageNumber;
        this.name = name;
        this.websiteAddress = websiteAddress;
        this.email = email;
        this.userNumber = userNumber;
        this.mineMachineNumber = mineMachineNumber;
        this.isSelected = isSelected;
    }

    private String address;
    private double mortgageNumber;
    private String name;
    private String websiteAddress;
    private String email;


    private int userNumber;
    private int mineMachineNumber;
    private boolean isSelected;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteAddress() {
        return websiteAddress;
    }

    public void setWebsiteAddress(String websiteAddress) {
        this.websiteAddress = websiteAddress;
    }

    public int getMineMachineNumber() {
        return mineMachineNumber;
    }

    public void setMineMachineNumber(int mineMachineNumber) {
        this.mineMachineNumber = mineMachineNumber;
    }

    public double getMortgageNumber() {
        return mortgageNumber;
    }

    public void setMortgageNumber(double mortgageNumber) {
        this.mortgageNumber = mortgageNumber;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
