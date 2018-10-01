package com.apps.fatima.sealocation.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {

    private String id;
    private String name;
    private String url;
    private int urlImage;
    private boolean isReported = false;

    public Picture(String url) {
        this.url = url;
    }

    public Picture(int urlImage) {
        this.urlImage = urlImage;
    }

    public Picture(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(int urlImage) {
        this.urlImage = urlImage;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeByte(isReported ? (byte) 1 : (byte) 0);
    }

    public Picture() {
    }

    protected Picture(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.url = in.readString();
        this.isReported = in.readByte() != 0;
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
}
