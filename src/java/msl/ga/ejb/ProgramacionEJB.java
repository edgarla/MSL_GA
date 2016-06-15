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
import msl.ga.modelo.Programacion;
import msl.ga.modelo.Usuario;

/**
 *
 * @author edgarloraariza
 */
public class ProgramacionEJB {
    
    private final String querySiguienteIdProgramacion = "select (case when max(id_programacion) is null then 0 else max(id_programacion) end) + 1 as next_id_programacion from MSL_GA_SCHEMA.Programacion";
    
    private int getSiguienteIdProgramacion() throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        int result = 0;
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
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
    
    private String getQueryCrearProgramacion(Usuario u, String fecha) throws ClassNotFoundException, SQLException{
        return "insert into MSL_GA_SCHEMA.Programacion (id_programacion, id_consultor, semana) values (" + this.getSiguienteIdProgramacion() + ",'" + u.getUserid() + "', datepart(wk, '" + fecha + "'))";
    }
    
    private String getQueryProgramacion(Usuario u, String fecha){
        return "select * from MSL_GA_SCHEMA.Programacion p where p.semana = datepart(wk, '" + fecha + "') and p.id_consultor = '" + u.getUserid() + "'";
    }
    
    private Programacion consultarSiExisteProgramacion(Usuario u, String fecha) throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Programacion p = null;
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
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
    
    public Programacion crearProgramacion(Usuario u, String fecha) throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(this.getQueryCrearProgramacion(u, fecha));
            }
        }
        return this.consultarSiExisteProgramacion(u, fecha);
    }
    
    public Programacion getProgramacion(Usuario u, String fecha, boolean crearSiNo) throws ClassNotFoundException, SQLException{
        Programacion p = this.consultarSiExisteProgramacion(u, fecha);
        if(p == null && crearSiNo){
            p = this.crearProgramacion(u, fecha);
        }
        return p;
    }
}
