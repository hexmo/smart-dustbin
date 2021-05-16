package com.example.smartdustbin.wasteManagement;


import java.io.Serializable;
import java.util.Date;

public class WasteHistoryModel  implements Serializable {
    private String wasteId;
    private String dustbinId;
    private int wasteLevel;
    private String timeStamp;

    public WasteHistoryModel() {
    }

    public WasteHistoryModel(String wasteId, String dustbinId, int wasteLevel, String timeStamp) {
        this.wasteId = wasteId;
        this.dustbinId = dustbinId;
        this.wasteLevel = wasteLevel;
        this.timeStamp = timeStamp;
    }

    public String getWasteId() {
        return wasteId;
    }

    public void setWasteId(String wasteId) {
        this.wasteId = wasteId;
    }

    public String getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(String dustbinId) {
        this.dustbinId = dustbinId;
    }

    public int getWasteLevel() {
        return wasteLevel;
    }

    public void setWasteLevel(int wasteLevel) {
        this.wasteLevel = wasteLevel;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
