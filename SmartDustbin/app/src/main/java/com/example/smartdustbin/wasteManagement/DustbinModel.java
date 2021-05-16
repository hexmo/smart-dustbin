package com.example.smartdustbin.wasteManagement;

import java.io.Serializable;

public class DustbinModel implements Serializable {
    private String dustbinId;
    private boolean hasOwner;
    private String secret;
    private int wasteLevel;
    private String dustbinRoom;
    private String ownerId;

    public DustbinModel() {
    }

    public DustbinModel(String dustbinId, boolean hasOwner, String secret, int wasteLevel, String dustbinRoom, String ownerId) {
        this.dustbinId = dustbinId;
        this.hasOwner = hasOwner;
        this.secret = secret;
        this.wasteLevel = wasteLevel;
        this.dustbinRoom = dustbinRoom;
        this.ownerId = ownerId;
    }


    public String getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(String dustbinId) {
        this.dustbinId = dustbinId;
    }

    public boolean isHasOwner() {
        return hasOwner;
    }

    public void setHasOwner(boolean hasOwner) {
        this.hasOwner = hasOwner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getWasteLevel() {
        return wasteLevel;
    }

    public void setWasteLevel(int wasteLevel) {
        this.wasteLevel = wasteLevel;
    }

    public String getDustbinRoom() {
        return dustbinRoom;
    }

    public void setDustbinRoom(String dustbinRoom) {
        this.dustbinRoom = dustbinRoom;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
