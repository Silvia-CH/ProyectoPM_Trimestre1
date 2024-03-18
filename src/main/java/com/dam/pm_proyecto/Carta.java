package com.dam.pm_proyecto;

public class Carta {
    private int imgBtnId;

    private int img_card;

    public Carta(int imgBtnId, int img_card) {
        this.imgBtnId = imgBtnId;
        this.img_card = img_card;
    }

    public int getImgBtnId() {
        return imgBtnId;
    }

    public void setImgBtnId(int imgBtnId) {
        this.imgBtnId = imgBtnId;
    }

    public int getImg_card() {
        return img_card;
    }

    public void setImg_card(int img_card) {
        this.img_card = img_card;
    }
}
