package com.fg;

import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public abstract class AbstractProcedure {
    @JacksonXmlProperty(isAttribute = true)
    protected String name;

    public AbstractProcedure(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract ArrayList<Waypoint> getWaypoints();
    public abstract void setWaypoints(ArrayList<Waypoint> waypoints);
}
