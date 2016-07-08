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
import msl.ga.modelo.Proyecto;

/**
 *
 * @author edgarloraariza
 */
public class ProyectoEJB {
    private final DbInfo dbInfo;
    private final String queryGetListaProyectos = "select p.id, p.nom_pro from zProyecto p where p.est_pro <> 'Cerrado';";

    public ProyectoEJB(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
    
    public ArrayList<Proyecto> getListaProyectos() throws ClassNotFoundException, SQLException, IOException{
        ArrayList proyectos = null;
        Class.forName(dbInfo.getDriverServiceDeskDB());
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlServiceDeskDB(), dbInfo.getUsrServiceDesk(), dbInfo.getPasServiceDesk())) {
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
