package com.prashant.stockmarketadviser.model;

public class ChatListModel {
    String userName , userMsg, userUid;

    long userMsgTime;

    public ChatListModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public long getUserMsgTime() {
        return userMsgTime;
    }

    public void setUserMsgTime(long userMsgTime) {
        this.userMsgTime = userMsgTime;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
