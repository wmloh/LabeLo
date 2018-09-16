package com.voxelpushing.test_networking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageSet {
    @SerializedName("img")
    private List<String> images;

    @SerializedName("labels")
    private List<String> labels;

    public ImageSet(List<String> imgs, List<String> lbls){
        this.images = imgs;
        this.labels = lbls;
    }

    public List<String> getImages(){
        return images;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setImages(List<String> imgs){
        this.images = imgs;
    }

    public void setLabels(List<String> lbls){
        this.labels = lbls;
    }
}
