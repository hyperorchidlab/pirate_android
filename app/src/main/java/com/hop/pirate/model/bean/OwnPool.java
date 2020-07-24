package com.hop.pirate.model.bean;


/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/9 9:55 AM
 */
public class OwnPool {
    private Long id;
    private String address;
    private String name;
    private String email;
    private double mortgageNumber;
    private String websiteAddress;

    public OwnPool(Long id, String address, String name, String email,
                   double mortgageNumber, String websiteAddress) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.email = email;
        this.mortgageNumber = mortgageNumber;
        this.websiteAddress = websiteAddress;
    }

    public OwnPool() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getMortgageNumber() {
        return this.mortgageNumber;
    }

    public void setMortgageNumber(double mortgageNumber) {
        this.mortgageNumber = mortgageNumber;
    }

    public String getWebsiteAddress() {
        return this.websiteAddress;
    }

    public void setWebsiteAddress(String websiteAddress) {
        this.websiteAddress = websiteAddress;
    }
}
