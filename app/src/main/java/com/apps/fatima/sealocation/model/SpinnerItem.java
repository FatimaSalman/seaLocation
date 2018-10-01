package com.apps.fatima.sealocation.model;


public class SpinnerItem {
    private String text, textA;
    private String id;

    public SpinnerItem(String id, String text, String textA) {
        this.textA = textA;
        this.text = text;
        this.id = id;
    }

    public SpinnerItem(String id, String text) {
        this.text = text;
        this.id = id;
    }

    public SpinnerItem() {

    }

    public String getTextA() {
        return textA;
    }

    public void setTextA(String textA) {
        this.textA = textA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
