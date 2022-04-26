package com.fg;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixDB extends Db {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public void dat2db(String file) {
        logger.info("Reading xp fix file:" + file);
        try {
            this.connect();
            Statement stmt = conn.createStatement();
            stmt.execute("delete from fixes");
            String sql = "insert into fixes values(?,?,?,?,?,?);";
            PreparedStatement prep = conn.prepareStatement(sql);

            BufferedReader br = new BufferedReader(new FileReader(new File(file)));
            String line;
            conn.setAutoCommit(false);
            int lineNo=0;
            
            while ((line=br.readLine())!=null) {
                line=line.trim();
                lineNo++;
                if ((lineNo % 1000) == 1) {
                    logger.info("Processing line:"+lineNo);
                }
                if (line.isEmpty() || line.startsWith("1101 Version") || line.equals("I")) {
                    continue;
                }
                if (line.equals("99")) {
                    break;
                }
                String cols[]=line.split(" +");
                if (cols.length>=5) {
                    float lat=Float.parseFloat(cols[0]);
                    float lon=Float.parseFloat(cols[1]);
                    String waypointType=null;
                    if (cols.length>=6) {
                        waypointType=cols[5];
                    }                    
                    prep.setFloat(1,lat);
                    prep.setFloat(2,lon);
                    prep.setString(3,cols[2]);
                    prep.setString(4,cols[3]);
                    prep.setString(5,cols[4]);
                    prep.setString(6,waypointType);
                    prep.addBatch();
                }
            }
            br.close();            
            prep.executeBatch();
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            logger.info("End processing fix file");
        }
    }

    public float[] findFix(String iden, String airportCode, String regionCode)
    {       
        float[] result = null;
        try {
            this.connect();
            PreparedStatement stmt = conn.prepareStatement("select latitude,longitude from fixes where ident=? and airportCode=? and regionCode=?");
            stmt.setString(1, iden);
            stmt.setString(2, airportCode);
            stmt.setString(3, regionCode);
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
