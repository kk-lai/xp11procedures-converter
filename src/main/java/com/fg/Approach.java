package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Approach extends AbstractProcedure {
    @JacksonXmlProperty(localName="Approach_Waypoint")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
    
    public Approach(String name) {
        super(name);
    }

    @Override
    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override
    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;        
    }   
}
