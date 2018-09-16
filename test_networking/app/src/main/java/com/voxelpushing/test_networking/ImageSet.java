package com.voxelpushing.test_networking;

import com.google.gson.annotations.SerializedName;

public class ImageSet {
    @SerializedName("img")
    private String[] images;

    @SerializedName("labels")
    private String[] labels;

    public ImageSet(String[] imgs, String[] lbls){
        this.images = imgs;
        this.labels = lbls;
    }

    public String[] getImgages(){
        return images;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setImages(String[] imgs){
        this.images = imgs;
    }

    public void setLabels(String[] lbls){
        this.labels = lbls;
    }
}
