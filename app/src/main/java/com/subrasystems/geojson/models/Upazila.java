package com.subrasystems.geojson.models;

public class Upazila {

    private String id;
    private String district_id;
    private String name;
    private String bn_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBn_name() {
        return bn_name;
    }

    public void setBn_name(String bn_name) {
        this.bn_name = bn_name;
    }
}
