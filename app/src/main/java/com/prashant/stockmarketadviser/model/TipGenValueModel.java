package com.prashant.stockmarketadviser.model;

public class TipGenValueModel {
    String firstTarget, secondTarget, thirdTarget, stopLoss, lotSize, quantity;

    String firstProfit, secondProfit, thirdProfit, rate, scripName, tip;

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getFirstProfit() {
        return firstProfit;
    }

    public void setFirstProfit(String firstProfit) {
        this.firstProfit = firstProfit;
    }

    public String getSecondProfit() {
        return secondProfit;
    }

    public void setSecondProfit(String secondProfit) {
        this.secondProfit = secondProfit;
    }

    public String getThirdProfit() {
        return thirdProfit;
    }

    public void setThirdProfit(String thirdProfit) {
        this.thirdProfit = thirdProfit;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getScripName() {
        return scripName;
    }

    public void setScripName(String scripName) {
        this.scripName = scripName;
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

    public String getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getLotSize() {
        return lotSize;
    }

    public void setLotSize(String lotSize) {
        this.lotSize = lotSize;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
