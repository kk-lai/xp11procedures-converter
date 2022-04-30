package com.fg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirportDB extends Db {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public void dat2db(String file) {
        logger.info("Reading xp fix apt:" + file);
        int lineNo=0;
        try {
            HashSet<String> importedAirports=new HashSet<String>();
            HashSet<String> importedRunways=new HashSet<String>();
            
            this.connect();
            Statement stmt = conn.createStatement();
            stmt.execute("delete from airports");
            stmt.execute("delete from runways");
            PreparedStatement prep1 = conn.prepareStatement("insert into airports values (?,?,?,?);");
            PreparedStatement prep2 = conn.prepareStatement("insert into runways values (?,?,?,?)");

            BufferedReader br = new BufferedReader(new FileReader(new File(file)));
            String line;
            conn.setAutoCommit(false);

            String aptCode = null;
            String aptName = null;            
            boolean isSkip=false;
            
            while ((line=br.readLine())!=null) {
                line=line.trim();
                lineNo++;
                
                if ((lineNo % 1000) == 1) {
                    logger.info("Processing line:"+lineNo);
                }               
                if (line.isEmpty() || line.startsWith("1100") || line.equals("I")) {
                    continue;
                }
                if (line.equals("99")) {
                    break;
                }
                String cols[]=line.split(" +");
                int lineType = Integer.parseInt(cols[0]);                
                switch (lineType) {
                    case 1:
                        aptName = String.join(" ", Arrays.copyOfRange(cols, 5, cols.length));
                        aptCode=cols[4];
                        isSkip=false;
                        break;
                    case 1302:
                        if (cols[1].equals("icao_code") && cols.length>=3) {
                            aptCode=cols[2];
                        }
                        break;
                    case 17:
                        isSkip=true;
                        break;
                    case 100:                                      
                        if (!isSkip) {
                            if (!importedAirports.contains(aptCode)) {

                                prep1.setString(1,aptCode);
                                prep1.setString(2,aptName);
                                prep1.setFloat(3,0.F);
                                prep1.setFloat(4,0.F);
                                prep1.addBatch();
                                importedAirports.add(aptCode);
                            }   
                            for(int c=8;c<18;c+=9) {
                                String runway=aptCode+"-"+cols[c];
                                if (!importedRunways.contains(runway)) {
                                    prep2.setString(1, aptCode);
                                    prep2.setString(2, cols[c]);
                                    prep2.setFloat(3, Float.parseFloat(cols[c+1]));
                                    prep2.setFloat(4, Float.parseFloat(cols[c+2]));
                                    prep2.addBatch();
                                    importedRunways.add(runway);
                                }
                            }
                        }
                        break;
                }
            }
            br.close();            
            prep1.executeBatch();
            prep2.executeBatch();                    
            conn.createStatement().executeUpdate("update airports " +
                "set latitude=(select avg(latitude) from runways where airportCode=airports.airportCode), " +
                "longitude=(select avg(longitude) from runways where airportCode=airports.airportCode);");
            conn.commit();
        } catch (Exception e) {
            logger.error("Error in line:"+lineNo);
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            logger.info("End processing apt file");
        }
    }    

    public String[] findRunway(String airportCode, String query)
    {
        if (query.startsWith("RW")) {
            query=query.substring(2);
        }
        if (query.endsWith("B")) {
            query=query.substring(0,query.length()-1)+"%";
        }
        try {
            ArrayList<String> result = new ArrayList<String>();
            this.connect();
            PreparedStatement stmt = conn.prepareStatement("select runway from runways where airportCode=? and runway like ?");
            stmt.setString(1,airportCode);
            stmt.setString(2, query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            String[] r = (String[]) result.toArray(new String[0]);
            return r;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public float[] findAirport(String airportCode)
    {       
        float[] result = null;
        try {
            this.connect();
            PreparedStatement stmt = conn.prepareStatement("select latitude,longitude from airports where airportCode=?");
            stmt.setString(1, airportCode);
            ResultSet rs = stmt.executeQuery();            
            if (rs.next()) {
                result = new float[2];
                result[0]=rs.getFloat(1);
                result[1]=rs.getFloat(2);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } 
        return result;
    }

    public float[] findRunwayLocation(String airportCode, String rwy)
    {       
        float[] result = null;
        try {
            this.connect();
            PreparedStatement stmt = conn.prepareStatement("select latitude,longitude from runways where airportCode=? and runway=?");
            stmt.setString(1, airportCode);
            stmt.setString(2, rwy);
            ResultSet rs = stmt.executeQuery();            
            if (rs.next()) {
                result = new float[2];
                result[0]=rs.getFloat(1);
                result[1]=rs.getFloat(2);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } 
        return result;
    }
}
