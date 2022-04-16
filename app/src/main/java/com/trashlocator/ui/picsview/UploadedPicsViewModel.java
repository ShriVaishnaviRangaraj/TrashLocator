package com.trashlocator.ui.picsview;

public class UploadedPicsViewModel {
    String imagelink;
    public UploadedPicsViewModel(){}

    public UploadedPicsViewModel(String imageLink) {

        this.imagelink = imageLink;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }
}
