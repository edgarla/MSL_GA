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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import msl.ga.modelo.Actividad;
import msl.ga.modelo.Programacion;

/**
 *
 * @author edgarloraariza
 */
public class ActividadEJB {
    private final String querySiguienteIdActividad = "select (case when max(id_actividad) is null then 0 end) + 1 as next_id_actividad from MSL_GA_SCHEMA.Actividad";
    
    private int getSiguienteIdActividad() throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        int result = 0;
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(querySiguienteIdActividad)) {
                    while(r.next()){
                        result = r.getInt("next_id_actividad");
                    }
                }
            }
        }
        return result;
    }
    
    private String getQueryHayActividadEnJornada(String fecha, int jornada){
        return "select * from MSL_GA_SCHEMA.Actividad a where a.fecha_programacion = '" + fecha + "' and a.jornada = " + jornada + " and a.estado = 0";
    }
    
    private boolean hayActividadEnJornada(String fecha, int jornada) throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.getQueryHayActividadEnJornada(fecha, jornada))) {
                    return r.next();
                    
                }
            }
        }
    }
    
    private String getQueryCrearActividad(Actividad a) throws ClassNotFoundException, SQLException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        return "insert into MSL_GA_SCHEMA.Actividad (id_actividad, id_programacion, fecha_programacion, jornada, id_tipo_actividad, estado, descripcion) values (" + this.getSiguienteIdActividad()+ ", " + a.getIdProgramacion() + ", '" + sdf.format(a.getFechaDeEjecucion()) + "', " + a.getJornada() + ", " + a.getTipoActividad() + ", " + a.getEstado() + ", '" + a.getDescripcion() + "')";
    }
    
    public void guardarActividad(Actividad a) throws ClassNotFoundException, SQLException, Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        if(!this.hayActividadEnJornada(sdf.format(a.getFechaDeEjecucion()), a.getJornada())){
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate(this.getQueryCrearActividad(a));
                }
            }
        }else{
            throw new Exception("Ya existe una actividad asignada a esa jornada en esa fecha");
        }
    }
    
    private String getQueryListadoDeActividadPorProgramacion(Programacion p){
        return "select * from MSL_GA_SCHEMA.Actividad a where a.id_programacion = " + p.getIdProgramacion() + " and a.estado = 0";
    }
    
    public ArrayList getActividadesDeProgramacion(Programacion p) throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.getQueryListadoDeActividadPorProgramacion(p))) {
                    while(r.next()){
                        Actividad a;
                        a = new Actividad(r.getInt("id_actividad"), r.getInt("id_programacion"), r.getDate("fecha_programacion"), r.getString("id_organizacion") ,r.getInt("jornada"), r.getInt("id_tipo_actividad"), r.getInt("estado"), r.getString("descripcion"));
                        result.add(a);
                    }
                }
            }
        }
        return result;
    }
    
    private String getQueryAgregarActividadAConsultor(Actividad a){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        return "insert into MSL_GA_SCHEMA.Actividad (id_actividad, id_programacion, fecha_programacion, id_organizacion, jornada, id_tipo_actividad, estdo, descripcion) values (" + a.getIdActividad() + ", " + a.getIdProgramacion() + ", '" + sdf.format(a.getFechaDeEjecucion()) + "', '" + a.getIdCliente() + "', " + a.getJornada() + ", " + a.getTipoActividad() + ", " + a.getEstado() + ", '" + a.getDescripcion() + "')";
    }
    
    public void agregarActividadAConsultor(Actividad a) throws ClassNotFoundException, SQLException{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(msl.ga.db.DbInfo.getUrlString())) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(this.getQueryAgregarActividadAConsultor(a));
            }
        }
    }
}
