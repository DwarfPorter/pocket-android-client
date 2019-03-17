package com.gb.pocketmessenger.models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.time.LocalTime;
import java.util.Date;

public class Message implements IMessage {
    private User user;
    private String adress;
    private String text;
    private Date date;

    public String getAdress() {
        return adress;
    }

    public void setAdress(String to) {
        this.adress = to;
    }

    @Override
    public String getId() {
        return null;
    }

    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Message(User from, String to, String text, Date date) {
        this.user = from;
        this.adress = to;
        this.date = date;
        this.text = text;
    }
}
