package com.canli.oya.traininventory.data.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "brands", indices = {@Index(value = {"brandName"},
        unique = true)})
public class BrandEntry {

    @PrimaryKey(autoGenerate = true)
    private int brandId;
    private String brandName;
    private String brandLogoUri;
    private String webUrl;

    @Ignore
    public BrandEntry(String brandName, String brandLogoUri, String webUrl) {
        this.brandName = brandName;
        this.brandLogoUri = brandLogoUri;
        this.webUrl = webUrl;
    }

    public BrandEntry(@NonNull int brandId, @NonNull String brandName, String brandLogoUri, String webUrl) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.brandLogoUri = brandLogoUri;
        this.webUrl = webUrl;
    }

    @NonNull
    public int getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandLogoUri() {
        return brandLogoUri;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setBrandLogoUri(String brandLogoUri) {
        this.brandLogoUri = brandLogoUri;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setBrandId(@NonNull int brandId) {
        this.brandId = brandId;
    }
}
