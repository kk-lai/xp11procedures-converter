package com.fg;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Waypoint {
    @JacksonXmlProperty(isAttribute = true, localName="ID")
    protected int id;
    protected String name="";
    protected String type="Normal";
    protected float latitude=0.F;
    protected float longitude=0.F;
    protected int speed=0;
    protected int altitude=0;
    protected int altitudeCons=0;
    protected String altitudeRestriction="at";
    
    public Waypoint(int id, String name, float latitude, float longitude) {
        this.id=id;
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getAltitude() {
        return altitude;
    }
    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }
    public int getAltitudeCons() {
        return altitudeCons;
    }
    public void setAltitudeCons(int altitudeCons) {
        this.altitudeCons = altitudeCons;
    }
    public String getAltitudeRestriction() {
        return altitudeRestriction;
    }
    public void setAltitudeRestriction(String altitudeRestriction) {
        this.altitudeRestriction = altitudeRestriction;
    }


    

}
