/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.ejb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import msl.ga.modelo.Cliente;

/**
 *
 * @author edgarloraariza
 */
public class ClienteEJB {
    private final String queryConsultaClientes = "Select org_name from ca_organization;";
    
    public ArrayList getListaDeClientes() throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(queryConsultaClientes)) {
                    while(r.next()){
                        if(result == null){
                            result = new ArrayList();
                        }
                        Cliente c = new Cliente(r.getString("org_name"));
                        result.add(c);
                    }
                }
            }
        }
        return result;
    }
}
