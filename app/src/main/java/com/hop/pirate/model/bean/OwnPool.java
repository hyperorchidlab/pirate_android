package com.hop.pirate.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/9 9:55 AM
 */
@Entity
public class OwnPool {
    @Id(autoincrement = true)
    private Long id;
    private String address;
    private String name;
    private String email;
    private double mortgageNumber;
    private String websiteAddress;

    @Generated(hash = 1283472363)
    public OwnPool(Long id, String address, String name, String email,
                   double mortgageNumber, String websiteAddress) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.email = email;
        this.mortgageNumber = mortgageNumber;
        this.websiteAddress = websiteAddress;
    }

    @Generated(hash = 414715948)
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
