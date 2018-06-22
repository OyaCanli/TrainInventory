package com.canli.oya.traininventory.data.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "brands")
public class BrandEntry {

    @PrimaryKey(autoGenerate = true)
    private int brandId;
    private String brandName;
    private String brandLogoUri;

    @Ignore
    public BrandEntry(String brandName, String brandLogoUri) {
        this.brandName = brandName;
        this.brandLogoUri = brandLogoUri;
    }

    public BrandEntry(int brandId, String brandName, String brandLogoUri) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.brandLogoUri = brandLogoUri;
    }

    public int getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandLogoUri() {
        return brandLogoUri;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setBrandLogoUri(String brandLogoUri) {
        this.brandLogoUri = brandLogoUri;
    }
}
