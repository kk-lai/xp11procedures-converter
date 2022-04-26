package com.fg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavDB extends Db {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public void dat2db(String file) {
        logger.info("Reading xp nav file:" + file);
        try {
            this.connect();
            Statement stmt = conn.createStatement();
            stmt.execute("delete from navs");
            String sql = "insert into navs values(?,?,?,?,?,?,?,?,?,?,?,?);";
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
                if (line.isEmpty() || line.startsWith("1150 Version") || line.equals("I")) {
                    continue;
                }
                if (line.equals("99")) {
                    break;
                }
                String cols[]=line.split(" +");
                int navType = Integer.parseInt(cols[0]);
                int numFields;
                switch (navType) {
                    case 2:
                    case 3:
                    case 12:
                    case 13:
                        numFields=11;
                        break;
                    default :
                        numFields=12;
                }
                if (cols.length>=numFields) {
                    prep.setInt(1,navType);
                    for(int i=1;i<10;i++) {
                        if (i==1 || i==2) {
                            float v = Float.parseFloat(cols[i]);
                            prep.setFloat(i+1, v);
                        } else {
                            prep.setString(i+1, cols[i]);
                        }
                    }
                    if (numFields==12) {
                        prep.setString(11, cols[10]);
                    } else {
                        prep.setString(11,"");
                    }
                    String name = String.join(" ", Arrays.copyOfRange(cols, numFields-1, cols.length));                    
                    prep.setString(12,name);
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
            logger.info("End processing nav file");
        }
    }
    
    public float[] findNav(String iden, String regionCode)
    {       
        float[] result = null;
        try {
            this.connect();
            PreparedStatement stmt = conn.prepareStatement("select latitude,longitude from navs where ident=? and regionCode=?");
            stmt.setString(1, iden);
            stmt.setString(2, regionCode);
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
