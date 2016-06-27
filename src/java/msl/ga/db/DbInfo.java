/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author edgarloraariza
 */
public class DbInfo {
    
    private String driverServiceDeskDB;
    private String localUrlServiceDesk;
    private String remoteUrlerviceDesk = "jdbc:sqlserver://190.60.206.11:1433;databaseName=MDB";
    private String usrServiceDesk;
    private String pasServiceDesk;
    
    private String driverMslGaDB;
    private String urlMslGaDB;
    private String usrMslGaDB;
    private String pasMslGaDB;

    public DbInfo(String path) throws IOException, Exception {
        Properties properties = new Properties();
        String dbConfigFile = path + "dbConfig.properties";
        InputStream inputStream = new FileInputStream(dbConfigFile);
        if(inputStream == null){
            throw new Exception("No se encuentra archivo de configurcion de base de datos: " + dbConfigFile);
        }
        properties.load(inputStream);
        
        this.driverServiceDeskDB = properties.getProperty("service_desk_db_driver");
        this.localUrlServiceDesk = properties.getProperty("service_desk_db_url");
        this.usrServiceDesk = properties.getProperty("service_desk_db_user");
        this.pasServiceDesk = properties.getProperty("service_desk_db_password");
        
        this.driverMslGaDB = properties.getProperty("msl_ga_db_driver");
        this.urlMslGaDB = properties.getProperty("msl_ga_db_url");
        this.usrMslGaDB = properties.getProperty("msl_ga_db_user");
        this.pasMslGaDB = properties.getProperty("msl_ga_db_password");
    }
    
    public String getDriverServiceDeskDB(){
        return driverServiceDeskDB;
    }
    public String getUrlServiceDeskDB(){
        return localUrlServiceDesk;
    }

    public String getUsrServiceDesk() {
        return usrServiceDesk;
    }
    
    public String getPasServiceDesk() {
        return pasServiceDesk;
    }

    public String getDriverMslGaDB() {
        return driverMslGaDB;
    }
    
    public String getUrlMslGaDB(){
        return urlMslGaDB;
    }

    public String getUsrMslGaDB() {
        return usrMslGaDB;
    }

    public String getPasMslGaDB() {
        return pasMslGaDB;
    }

    private InputStream FileInputStream(String dbConfigFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
