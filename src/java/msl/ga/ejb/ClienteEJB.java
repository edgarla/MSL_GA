/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.ejb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import msl.ga.db.DbInfo;
import msl.ga.modelo.Cliente;

/**
 *
 * @author edgarloraariza
 */
public class ClienteEJB {
    private final DbInfo dbInfo;
    private final String queryConsultaClientes = "Select org_name from ca_organization;";

    public ClienteEJB(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
    
    public ArrayList getListaDeClientes() throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverServiceDeskDB());
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlServiceDeskDB(), dbInfo.getUsrServiceDesk(), dbInfo.getPasServiceDesk())) {
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
