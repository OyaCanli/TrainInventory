package com.canli.oya.traininventory.data.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.NO_ACTION;

@Entity(tableName = "trains")
public class TrainEntry {

    @PrimaryKey(autoGenerate = true)
    private int trainId;
    private String trainName;
    private String modelReference;
    private String brandName;
    private String categoryName;
    private int quantity;
    private String imageUri;
    private String description;
    private String location;
    private String scale;

    @Ignore
    public TrainEntry(String trainName, String modelReference, String brandName, String categoryName, int quantity, String imageUri, String description, String location, String scale) {
        this.trainName = trainName;
        this.modelReference = modelReference;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.imageUri = imageUri;
        this.description = description;
        this.location = location;
        this.scale = scale;
    }

    public TrainEntry(int trainId, String trainName, String modelReference, String brandName, String categoryName, int quantity, String imageUri, String description, String location, String scale) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.modelReference = modelReference;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.imageUri = imageUri;
        this.description = description;
        this.location = location;
        this.scale = scale;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getModelReference() {
        return modelReference;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getScale() {
        return scale;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public void setModelReference(String modelReference) {
        this.modelReference = modelReference;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "TrainEntry{" +
                "trainId=" + trainId +
                ", trainName='" + trainName + '\'' +
                ", modelReference='" + modelReference + '\'' +
                ", brandName='" + brandName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", quantity=" + quantity +
                ", imageUri='" + imageUri + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", scale='" + scale + '\'' +
                '}';
    }
}
