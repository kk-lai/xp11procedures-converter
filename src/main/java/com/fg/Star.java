package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Star extends AbstractProcedure {
    @JacksonXmlProperty(isAttribute = true)
    protected String runways;

    @JacksonXmlProperty(localName="Star_Waypoint")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    @JacksonXmlProperty(localName="Star_Transition")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<StarTransition> starTransition = new ArrayList<StarTransition>();

    public Star(String name, String runways) {
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
           
    public ArrayList<StarTransition> getStarTransition() {
        return starTransition;
    }

    public void setStarTransition(ArrayList<StarTransition> starTransition) {
        this.starTransition = starTransition;
    }
}
