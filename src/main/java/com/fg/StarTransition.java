package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class StarTransition extends AbstractProcedure {

    @JsonIgnore
    private String StarName;    

    public StarTransition(String name, String StarName) {
        super(name);        
        this.StarName=StarName;
    }

    @JacksonXmlProperty(localName="StarTr_Waypoint")
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

    public String getStarName() {
        return StarName;
    }

    public void setStarName(String StarName) {
        this.StarName = StarName;
    } 
    
}
