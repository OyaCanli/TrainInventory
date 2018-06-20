package com.canli.oya.traininventory.data;

public class TrainMinimal {

    private String trainName;
    private String modelReference;
    private String brandName;
    private String categoryName;
    private String imageUri;
    private int trainId;

    public TrainMinimal(String trainName, String modelReference, String brandName, String categoryName, String imageUri, int trainId) {
        this.trainName = trainName;
        this.modelReference = modelReference;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.imageUri = imageUri;
        this.trainId = trainId;
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

    public String getImageUri() {
        return imageUri;
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

    public void setImageUrl(String imageUrl) {
        this.imageUri = imageUri;
    }
}
