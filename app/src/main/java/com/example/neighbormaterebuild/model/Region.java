package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Region {

    public static Region region;

    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Util> regions;

    public Region() {
    }

    public Region(int code, String status, List<Util> regions) {
        this.code = code;
        this.status = status;
        this.regions = regions;
    }

    @Override
    public String toString() {
        return "RegionResponse{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", regions=" + regions +
                '}';
    }

    public static Region getRegion() {
        if (region == null) {
            synchronized (Region.class) {
                if (null == region) {
                    region = new Region();
                }
            }
        }
        return region;
    }

    public static void setRegion(Region region) {
        Region.region = region;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Util> getRegions() {
        return regions;
    }

    public void setRegions(List<Util> regions) {
        this.regions = regions;
    }
}
