package com.gb.pocketmessenger.models;

import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {
    private String name;
    private String id;

    public User(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public void setName(String login) {
        this.name = login;
    }
}
