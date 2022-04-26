package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Sid extends AbstractProcedure {
    @JacksonXmlProperty(isAttribute = true)
    protected String runways;

    @JacksonXmlProperty(localName="Sid_Waypoint")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    @JacksonXmlProperty(localName="Sid_Transition")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<SidTransition> sidTransition = new ArrayList<SidTransition>();

    public Sid(String name, String runways) {
        super(name);
        this.runways=runways;
    }

    public String getRunways() {
        return runways;
    }

    public void setRunways(String runways) {
        this.runways = runways;
    }

    @Override
    public ArrayList<Waypoint> getWaypoints() {
        
        return waypoints;
    }

    @Override
    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        
        this.waypoints=waypoints;
    }

    public ArrayList<SidTransition> getSidTransition() {
        return sidTransition;
    }

    public void setSidTransition(ArrayList<SidTransition> sidTransition) {
        this.sidTransition = sidTransition;
    }

    
}
