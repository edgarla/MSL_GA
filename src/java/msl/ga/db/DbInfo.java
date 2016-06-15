/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.db;

/**
 *
 * @author edgarloraariza
 */
public final class DbInfo {
    
    private static String localUrl = "jdbc:sqlserver://192.168.1.202:1433;databaseName=MDB;user=sa;password=Adm1nistrat0r";
    private static String remoteUrl = "jdbc:sqlserver://190.60.206.11:1433;databaseName=MDB;user=sa;password=Adm1nistrat0r";
    
    public static String getUrlString(){
        return remoteUrl;
    }
}
