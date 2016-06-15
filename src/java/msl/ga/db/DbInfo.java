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
    
    public static String getUrlString(){
        return "jdbc:sqlserver://192.168.1.202:1433;databaseName=MDB;user=sa;password=Adm1nistrat0r";
    }
}
