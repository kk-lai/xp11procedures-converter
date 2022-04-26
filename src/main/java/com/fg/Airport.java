package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class Airport {
    @JacksonXmlProperty(isAttribute = true, localName="ICAOcode")
    protected String icaoCode;

    @JacksonXmlProperty(localName="Sid")
    @JacksonXmlElementWrapper(useWrapping = false)    
    protected ArrayList<Sid> sids=new ArrayList<Sid>();

    @JacksonXmlProperty(localName="Star")
    @JacksonXmlElementWrapper(useWrapping = false)    
    protected ArrayList<Star> stars=new ArrayList<Star>();
    
    @JacksonXmlProperty(localName="Approach")
    @JacksonXmlElementWrapper(useWrapping = false)    
    protected ArrayList<Approach> approaches=new ArrayList<Approach>();

    public Airport(String icaoCode) {
        this.icaoCode=icaoCode;
    }
    
    public String getIcaoCode() {
        return icaoCode;
    }
    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public ArrayList<Sid> getSids() {
        return sids;
    }

    public void setSids(ArrayList<Sid> sids) {
        this.sids = sids;
    }

    public ArrayList<Star> getStars() {
        return stars;
    }

    public void setStars(ArrayList<Star> stars) {
        this.stars = stars;
    }

    public ArrayList<Approach> getApproaches() {
        return approaches;
    }

    public void setApproaches(ArrayList<Approach> approaches) {
        this.approaches = approaches;
    }

    
}
