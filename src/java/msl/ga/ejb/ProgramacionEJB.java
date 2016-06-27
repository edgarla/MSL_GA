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
import msl.ga.db.DbInfo;
import msl.ga.modelo.Programacion;
import msl.ga.modelo.Usuario;

/**
 *
 * @author edgarloraariza
 */
public class ProgramacionEJB {
    private final DbInfo dbInfo;
    private final String querySiguienteIdProgramacion = "select (case when max(id_programacion) is null then 0 else max(id_programacion) end) + 1 as next_id_programacion from MSL_GA_SCHEMA.Programacion";

    public ProgramacionEJB(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
    
    private int getSiguienteIdProgramacion() throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        int result = 0;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(querySiguienteIdProgramacion)) {
                    while(r.next()){
                        result = r.getInt("next_id_programacion");
                    }
                }
            }
        }
        return result;
    }
    
    private String getQueryCrearProgramacion(Usuario u, String fecha) throws ClassNotFoundException, SQLException, IOException{
        String sql = "";
        if(dbInfo.getDriverMslGaDB().contains("postgresql")){
            sql = "insert into MSL_GA_SCHEMA.Programacion (id_programacion, id_consultor, semana) values (" + this.getSiguienteIdProgramacion() + ",'" + u.getUserid() + "', EXTRACT(WEEK FROM TIMESTAMP '" + fecha + "'))";
        }else if(dbInfo.getDriverMslGaDB().contains("sqlserver")){
            sql = "insert into MSL_GA_SCHEMA.Programacion (id_programacion, id_consultor, semana) values (" + this.getSiguienteIdProgramacion() + ",'" + u.getUserid() + "', datepart(wk, '" + fecha + "'))";
        }
        return sql;
    }
    
    private String getQueryProgramacion(Usuario u, String fecha) throws IOException{
        String sql = "";
        if(dbInfo.getDriverMslGaDB().contains("postgresql")){
            sql = "select * from MSL_GA_SCHEMA.Programacion p where p.semana = EXTRACT(WEEK FROM TIMESTAMP '" + fecha + "') and p.id_consultor = '" + u.getUserid() + "'";
        }else if(dbInfo.getDriverMslGaDB().contains("sqlserver")){
            sql = "select * from MSL_GA_SCHEMA.Programacion p where p.semana = datepart(wk, '" + fecha + "') and p.id_consultor = '" + u.getUserid() + "'";
        }
        return sql;
    }
    
    private Programacion consultarSiExisteProgramacion(Usuario u, String fecha) throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        Programacion p = null;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.getQueryProgramacion(u, fecha))) {
                    while(r.next()){
                        p = new Programacion(r.getInt("id_programacion"), r.getString("id_consultor"), r.getInt("semana"));
                    }
                }
            }
        }
        return p;
    }
    
    public Programacion crearProgramacion(Usuario u, String fecha) throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(this.getQueryCrearProgramacion(u, fecha));
            }
        }
        return this.consultarSiExisteProgramacion(u, fecha);
    }
    
    public Programacion getProgramacion(Usuario u, String fecha, boolean crearSiNo) throws ClassNotFoundException, SQLException, IOException{
        Programacion p = this.consultarSiExisteProgramacion(u, fecha);
        if(p == null && crearSiNo){
            p = this.crearProgramacion(u, fecha);
        }
        return p;
    }
}
