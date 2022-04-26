package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class SidTransition extends AbstractProcedure {

    @JsonIgnore
    private String sidName;    

    public SidTransition(String name, String sidName) {
        super(name);        
        this.sidName=sidName;
    }

    @JacksonXmlProperty(localName="SidTr_Waypoint")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    @Override
    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override
    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints=waypoints;        
    }

    public String getSidName() {
        return sidName;
    }

    public void setSidName(String sidName) {
        this.sidName = sidName;
    }

}
