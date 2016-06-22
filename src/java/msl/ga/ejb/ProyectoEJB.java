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
import msl.ga.modelo.Proyecto;

/**
 *
 * @author edgarloraariza
 */
public class ProyectoEJB {
    private final String queryGetListaProyectos = "select p.id, p.nom_pro from zProyecto p where p.est_pro <> 'Cerrado';";
    
    public ArrayList getListaProyectos() throws ClassNotFoundException, SQLException{
        ArrayList proyectos = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.queryGetListaProyectos)) {
                    while(r.next()){
                        Proyecto p;
                        if(proyectos == null){
                            proyectos = new ArrayList();
                        }
                        p = new Proyecto(r.getInt("id"), r.getString("nom_pro"));
                        proyectos.add(p);
                    }
                }
            }
        }
        return proyectos;
    }
}
