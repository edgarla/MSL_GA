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
import msl.ga.modelo.TipoActividad;

/**
 *
 * @author edgarloraariza
 */
public class TipoActividadEJB {
    private final String queryConsultaActividades = "select * from MSL_GA_SCHEMA.Tipo_Actividad";
    
    public ArrayList getListaDeActividades() throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(queryConsultaActividades)) {
                    while(r.next()){
                        if(result == null){
                            result = new ArrayList();
                        }
                        TipoActividad ta = new TipoActividad(r.getInt("id_actividad"), r.getString("nombre"));
                        result.add(ta);
                    }
                }
            }
        }
        return result;
    }
}
