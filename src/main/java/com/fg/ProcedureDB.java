package com.fg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcedureDB {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    Airport airport;

    @JacksonXmlProperty(isAttribute = true, localName="build")
    String build;

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void writeXML(String path) throws JsonGenerationException, JsonMappingException, IOException {
        XmlMapper xmlMapper = new XmlMapper();
       
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.writeValue(new File(path), this);
    }

    public void extractProcedure(String airportCode, String cifpRoot,  String ofolder) {
        try {
            File ifile = new File(cifpRoot+File.separator+airportCode+".dat");
            logger.info("Reading xp cifp:" + ifile.getPath());
            String ofile = ofolder+File.separator+airportCode+".procedures.xml";
            this.build="xpextract";
            AirportDB airportDb = new AirportDB();
            FixDB fixDb = new FixDB();
            NavDB navDb = new NavDB();
            AirportDB airportDB = new AirportDB();            
            this.airport=new Airport(airportCode);
            BufferedReader br = new BufferedReader(new FileReader(ifile));
            String line;
            int lineNo=0;
            AbstractProcedure procedure = null;
            String lastWaypoint = null;            
            String lastTransIdent = null;
            String lastIndent = null;
            int wpid = 0;
            ArrayList<SidTransition> sidTransitions = new ArrayList<SidTransition>();
            ArrayList<StarTransition> starTransitions = new ArrayList<StarTransition>();
            while ((line=br.readLine())!=null) {
                lineNo++;
                System.out.println("Parsing line:"+lineNo);
                String cols[] = line.split("(,|;)");
                for(int i=0;i<cols.length;i++) {
                    cols[i]=cols[i].trim();
                }
                
                String ident = cols[2];                
                String transIdent = cols[3];
                String waypoint = cols[4];

                String wayptRegionCode=cols[5];
                String secCode = cols[6];
                String subCode = cols[7];
                String descCode = cols[8];
                boolean isAddWaypoint = true;
                if (waypoint.isEmpty()) {
                    continue;
                }
                if (line.startsWith("SID:")) {
                    if (!ident.equals(lastIndent) || !transIdent.equals(lastTransIdent)) {
                        if (transIdent.startsWith("RW") || transIdent.equals("ALL")) {
                            System.out.println("SID:"+ident);
                            String rwy=transIdent;
                            String runways="All";
                            if (!rwy.equals("ALL")) {
                                runways = String.join(",",airportDb.findRunway(airportCode, rwy));
                            }                        
                            procedure=new Sid(ident, runways);
                            airport.sids.add((Sid) procedure);
                            wpid=1;
                            lastWaypoint=null;
                        } else {
                            SidTransition sidtr = new SidTransition(transIdent, ident);
                            sidTransitions.add(sidtr);
                            procedure=sidtr;                           
                            wpid=1;
                            lastWaypoint=null;
                        }
                    }
                } else if (line.startsWith("STAR:")) {
                    if (!ident.equals(lastIndent) || !transIdent.equals(lastTransIdent)) {
                        if (transIdent.startsWith("RW") || transIdent.equals("ALL")) {
                            System.out.println("Star:"+ident);
                            String rwy=transIdent;
                            String runways="All";
                            if (!rwy.equals("ALL")) {
                                runways = String.join(",",airportDb.findRunway(airportCode, rwy));
                            }                        
                            procedure=new Star(ident, runways);
                            airport.stars.add((Star) procedure);
                            wpid=1;
                            lastWaypoint=null;
                        } else {
                            String name=transIdent;
                            if (transIdent.isEmpty()) {
                                name=waypoint;
                            }
                            StarTransition starTr = new StarTransition(name, ident);
                            starTransitions.add(starTr);
                            procedure=starTr;                           
                            wpid=1;
                            lastWaypoint=null;
                        }
                    }
                } else if (line.startsWith("APPCH:")) {                    
                    if (!ident.equals(lastIndent) || !transIdent.equals(lastTransIdent)) {
                        String aprefix = "";
                        if (ident.startsWith("R")) {
                            aprefix = "RNAV";                            
                        } else if (ident.startsWith("D")) {
                            aprefix = "DME";
                        } else if (ident.startsWith("I")) {
                            aprefix = "ILS";
                        } else if (ident.startsWith("L")) {
                            aprefix = "LOC";
                        } else if (ident.startsWith("X")) {
                            aprefix = "LDA";
                        }
                        String rwy=ident.substring(1);
                        if (rwy.endsWith("X") || rwy.endsWith("Y") || rwy.endsWith("Z")) {
                            aprefix=aprefix+"-"+rwy.substring(rwy.length()-1);
                            rwy=rwy.substring(0,rwy.length()-1);
                        }
                        String suffix = "";
                        if (!transIdent.isEmpty()) {
                            suffix="-"+transIdent;
                        }
                        procedure=new Approach(aprefix+rwy+suffix, ident, transIdent, rwy);            
                        airport.getApproaches().add((Approach)procedure);
                        wpid=1;
                        lastWaypoint=null;
                    }
                } else {
                    System.out.println("Skipping line:"+lineNo);
                    isAddWaypoint=false;
                }
                if (isAddWaypoint) {
                    if (waypoint.equals(lastWaypoint)) {
                        continue;
                    }
                    float [] ll = new float[2];                    
                    lastWaypoint=waypoint;
                    lastTransIdent=transIdent;
                    lastIndent=ident;
                    boolean isRunway=false;
                    if (secCode.equals("D") || subCode.equals("N")) {
                        ll = navDb.findNav(waypoint,wayptRegionCode);
                    } else {
                        if (descCode.length()>1 && descCode.startsWith("A")) {
                            // airport
                            ll = airportDb.findAirport(waypoint);    
                        } else {
                            String icaoCode=airportCode;
                            if (secCode.equals("E")) {
                                icaoCode="ENRT";
                            }
                            ll = fixDb.findFix(waypoint, icaoCode, wayptRegionCode);
                            if (ll==null && line.startsWith("APPCH") && waypoint.startsWith("RW")) {
                                Approach ap=(Approach)procedure;
                                ll= airportDB.findRunwayLocation(airportCode, ap.getRunway());
                                isRunway=true;
                            }
                        }
                    }         
                    Waypoint wpt = new Waypoint(wpid, waypoint, ll[0], ll[1]);
                    if (isRunway) {
                        wpt.setType("Runway");
                    }
                    if (!cols[27].isEmpty()) {
                        int speed = Integer.parseInt(cols[27]);
                        wpt.setSpeed(speed);
                    }
                    if (!cols[23].isEmpty()) {
                        int sf=1;
                        String altitude=cols[23];
                        if (altitude.startsWith("FL")) {
                            altitude=altitude.substring(2);
                            sf=100;
                        }
                        wpt.setAltitude(Integer.parseInt(altitude)*sf);
                        if (cols[22].equals("+")) {
                            wpt.setAltitudeRestriction("above");
                        }
                        if (cols[22].equals("-")) {
                            wpt.setAltitudeRestriction("below");
                        }
                    }                    
                    wpid++;
                    procedure.getWaypoints().add(wpt);
                    if (!isRunway && line.startsWith("APPCH:") && descCode.length()>=4 && descCode.substring(3,4).equals("M")) {
                        Approach ap=(Approach)procedure;
                        ll=airportDB.findRunwayLocation(airportCode, ap.getRunway());
                        wpt = new Waypoint(wpid, "RW" + ap.getRunway(), ll[0], ll[1]);
                        wpt.setType("Runway");
                        procedure.getWaypoints().add(wpt);
                        wpid++;                        
                    }
                }
            }
            for(SidTransition sidTransition : sidTransitions) {
                for(Sid sid:airport.getSids()) {
                    if (sidTransition.getSidName().equals(sid.getName())) {
                        sid.getSidTransition().add(sidTransition);
                    }
                }
            }
            for(StarTransition starTransition : starTransitions) {
                for(Star star:airport.getStars()) {
                    if (starTransition.getStarName().equals(star.getName())) {
                        star.getStarTransition().add(starTransition);
                    }
                }
            }
            for(Star star:airport.getStars()) {
                if (star.getStarTransition().size()==1) {
                    int pos=0;
                    for(Waypoint wp: star.getStarTransition().get(0).getWaypoints()) {
                        star.getWaypoints().add(pos,wp);
                        pos++;
                    }
                    star.getStarTransition().remove(0);
                    pos=1;
                    for(Waypoint wp: star.getWaypoints()) {
                        wp.setId(pos);
                        pos++;
                    }
                }
            }
            int j=0;
            do {
                for(j=0;j<airport.getStars().size();j++) {
                    Star jstar = airport.getStars().get(j);
                    String jstarName=jstar.getName();
                    boolean hasDuplicate=false;
                    for(int i=0;i<airport.getStars().size();i++) {
                        if (i==j) {
                            continue;
                        }
                        Star istar = airport.getStars().get(i);
                        String istarName = istar.getName();
                        if (istarName.equals(jstarName)) {
                            hasDuplicate=true;
                            istar.setName(istarName + istar.getRunways());
                        }
                    }
                    if (hasDuplicate) {
                        jstar.setName(jstarName+jstar.getRunways());
                        break;
                    }
                }
            } while (j<airport.getStars().size());
            do {
                for(j=0;j<airport.getSids().size();j++) {
                    Sid jsid = airport.getSids().get(j);
                    String jsidName=jsid.getName();
                    boolean hasDuplicate=false;
                    for(int i=0;i<airport.getSids().size();i++) {
                        if (i==j) {
                            continue;
                        }
                        Sid isid = airport.getSids().get(i);
                        String isidName = isid.getName();
                        if (isidName.equals(jsidName)) {
                            hasDuplicate=true;
                            isid.setName(isidName + isid.getRunways());
                        }
                    }
                    if (hasDuplicate) {
                        jsid.setName(jsidName+jsid.getRunways());
                        break;
                    }
                }
            } while (j<airport.getSids().size());
            HashMap<String,Approach> aps = new HashMap<String,Approach>();
            for(Approach ap: airport.getApproaches()) {
                if (ap.getTransIdent().isEmpty()) {
                    aps.put(ap.getIdent(),ap);
                }
            }
            airport.getApproaches().removeAll(aps.values());
            for(Approach ap: airport.getApproaches()) {
                if (aps.containsKey(ap.getIdent())) {
                    Approach ap0=aps.get(ap.getIdent());
                    for(Waypoint wp: ap0.getWaypoints()) {
                        ap.getWaypoints().add(wp);
                    }
                    int id=1;
                    for(Waypoint wp: ap.getWaypoints()) {
                        wp.setId(id++);
                    }
                }
            }
            br.close();
            this.writeXML(ofile);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
