package com.apps.fatima.sealocation.model;

import android.net.Uri;

import java.io.File;

public class Image {
    private Uri uri;
    private File file;
    private String image_url, id, partner_id, object_id, object_tb;

    public Image(Uri uri) {
        this.uri = uri;
    }

    public Image(String image_url, String id, String partner_id, String object_id, String object_tb) {
        this.image_url = image_url;
        this.id = id;
        this.partner_id = partner_id;
        this.object_id = object_id;
        this.object_tb = object_tb;
    }

    public Image(File file) {
        this.file = file;
    }

    public Image(String image_url) {
        this.image_url = image_url;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getObject_tb() {
        return object_tb;
    }

    public void setObject_tb(String object_tb) {
        this.object_tb = object_tb;
    }
}
