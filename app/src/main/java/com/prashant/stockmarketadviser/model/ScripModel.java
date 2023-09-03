package com.prashant.stockmarketadviser.model;

public class ScripModel {

    String stopLoss, firstTarget, secondTarget, thirdTarget;
    String stopLossStatusImage, firstTargetStatusImage, secondTargetStatusImage, thirdTargetStatusImage;
    String scripName, scripType, scripTargetStatus, time, uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getFirstTarget() {
        return firstTarget;
    }

    public void setFirstTarget(String firstTarget) {
        this.firstTarget = firstTarget;
    }

    public String getSecondTarget() {
        return secondTarget;
    }

    public void setSecondTarget(String secondTarget) {
        this.secondTarget = secondTarget;
    }

    public String getThirdTarget() {
        return thirdTarget;
    }

    public void setThirdTarget(String thirdTarget) {
        this.thirdTarget = thirdTarget;
    }

    public String getStopLossStatusImage() {
        return stopLossStatusImage;
    }

    public void setStopLossStatusImage(String stopLossStatusImage) {
        this.stopLossStatusImage = stopLossStatusImage;
    }

    public String getFirstTargetStatusImage() {
        return firstTargetStatusImage;
    }

    public void setFirstTargetStatusImage(String firstTargetStatusImage) {
        this.firstTargetStatusImage = firstTargetStatusImage;
    }

    public String getSecondTargetStatusImage() {
        return secondTargetStatusImage;
    }

    public void setSecondTargetStatusImage(String secondTargetStatusImage) {
        this.secondTargetStatusImage = secondTargetStatusImage;
    }

    public String getThirdTargetStatusImage() {
        return thirdTargetStatusImage;
    }

    public void setThirdTargetStatusImage(String thirdTargetStatusImage) {
        this.thirdTargetStatusImage = thirdTargetStatusImage;
    }

    public String getScripName() {
        return scripName;
    }

    public void setScripName(String scripName) {
        this.scripName = scripName;
    }

    public String getScripType() {
        return scripType;
    }

    public void setScripType(String scripType) {
        this.scripType = scripType;
    }

    public String getScripTargetStatus() {
        return scripTargetStatus;
    }

    public void setScripTargetStatus(String scripTargetStatus) {
        this.scripTargetStatus = scripTargetStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
