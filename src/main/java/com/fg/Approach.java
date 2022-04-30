package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Approach extends AbstractProcedure {
    @JacksonXmlProperty(localName="App_Waypoint")
    @JacksonXmlElementWrapper(useWrapping = false)
    protected ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    @JsonIgnore
    private String ident;

    @JsonIgnore
    private String transIdent;

    @JsonIgnore
    private String runway;
    
    public Approach(String name, String ident, String transIdent, String runway) {
        super(name);
        this.ident=ident;
        this.transIdent=transIdent;        
        this.runway=runway;
    }

    @Override
    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override
    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;        
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getTransIdent() {
        return transIdent;
    }

    public void setTransIdent(String transIdent) {
        this.transIdent = transIdent;
    }

    public String getRunway() {
        return runway;
    }

    public void setRunway(String runway) {
        this.runway = runway;
    }
}
