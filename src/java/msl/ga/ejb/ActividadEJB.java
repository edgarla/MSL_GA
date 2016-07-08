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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import msl.ga.db.DbInfo;
import msl.ga.modelo.Actividad;
import msl.ga.modelo.ActividadPorProyecto;
import msl.ga.modelo.Programacion;
import msl.ga.modelo.Proyecto;

/**
 *
 * @author edgarloraariza
 */
public class ActividadEJB {
    private final DbInfo dbInfo;
    private final String querySiguienteIdActividad = "select (case when max(id_actividad) is null then 0 else max(id_actividad) end) + 1 as next_id_actividad from MSL_GA_SCHEMA.Actividad";

    public ActividadEJB(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
    
    private int getSiguienteIdActividad() throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        int result = 0;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
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
    
    private String getQueryHayActividadEnJornada(int idProgramacion, String fecha, int jornada){
        return "select * from MSL_GA_SCHEMA.Actividad a where a.id_programacion = " + idProgramacion + " and a.fecha_programacion = '" + fecha + "' and a.jornada = " + jornada + " and a.estado = 0";
    }
    
    private boolean hayActividadEnJornada(int idProgramacion, String fecha, int jornada) throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.getQueryHayActividadEnJornada(idProgramacion, fecha, jornada))) {
                    int nActividades = 0;
                    while(r.next()){
                        nActividades = nActividades + 1;
                    }
                    return (nActividades < 2);
                }
            }
        }
    }
    
    private String getQueryCrearActividad(Actividad a) throws ClassNotFoundException, SQLException, IOException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        return "insert into MSL_GA_SCHEMA.Actividad (id_actividad, id_programacion, fecha_programacion, id_organizacion, jornada, id_tipo_actividad, estado, id_proyecto, descripcion) values (" + this.getSiguienteIdActividad()+ ", " + a.getIdProgramacion() + ", '" + sdf.format(a.getFechaDeEjecucion()) + "', '" + a.getIdCliente() + "', " + a.getJornada() + ", " + a.getTipoActividad() + ", " + a.getEstado() + ", " + a.getIdProyecto() + " , '" + a.getDescripcion() + "')";
    }
    
    public void guardarActividad(Actividad a) throws ClassNotFoundException, SQLException, Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        if(this.hayActividadEnJornada(a.getIdProgramacion(), sdf.format(a.getFechaDeEjecucion()), a.getJornada())){
            Class.forName(dbInfo.getDriverMslGaDB());
            try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate(this.getQueryCrearActividad(a));
                }
            }
        }else{
            throw new Exception("Ya existe una actividad asignada a esa jornada en esa fecha");
        }
    }
    
    private String getQueryListadoDeActividadPorProgramacion(Programacion p, boolean porDia, String fecha){
        if(porDia){
            return "select * from MSL_GA_SCHEMA.Actividad a where a.id_programacion = " + p.getIdProgramacion() + " and a.fecha_programacion = '" + fecha + "'  and a.estado = 0 order by 3, 5, 1";
        }else{
            return "select * from MSL_GA_SCHEMA.Actividad a where a.id_programacion = " + p.getIdProgramacion() + " and a.estado = 0 order by 3, 5, 1";
        }
    }
    
    public ArrayList getActividadesDeProgramacion(Programacion p, boolean porDia, String fecha) throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.getQueryListadoDeActividadPorProgramacion(p, porDia, fecha))) {
                    while(r.next()){
                        Actividad a;
                        if(result == null){
                            result = new ArrayList();
                        }
                        a = new Actividad(r.getInt("id_actividad"), r.getInt("id_programacion"), r.getDate("fecha_programacion"), r.getString("id_organizacion") ,r.getInt("jornada"), r.getInt("id_tipo_actividad"), r.getInt("id_proyecto"), r.getInt("estado"), r.getString("descripcion"));
                        result.add(a);
                    }
                }
            }
        }
        return result;
    }
    
    private String getQueryActualizarEstadoActividad(Actividad a){
        return "update MSL_GA_SCHEMA.Actividad set estado = " + a.getEstado() + " where id_actividad = " + a.getIdActividad();
    }
    
    public void ActualizarEstadoActividad(Actividad a) throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverMslGaDB());
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(this.getQueryActualizarEstadoActividad(a));
            }
        }
    }
    
    private String getQueryActividadesPorProyecto(Proyecto p, String fecha){
        String sql = "";
        if(dbInfo.getDriverMslGaDB().contains("postgresql")){
            sql = "select p.id_consultor, a.* from MSL_GA_SCHEMA.Actividad a inner join MSL_GA_SCHEMA.Programacion p on p.id_programacion = a.id_programacion  where a.estado = 0 and p.semana = EXTRACT(WEEK FROM TIMESTAMP '" + fecha + "') and a.id_proyecto = " + p.getIdProyecto();
        }else if(dbInfo.getDriverMslGaDB().contains("sqlserver")){
            sql = "select p.id_consultor, a.* from MSL_GA_SCHEMA.Actividad a inner join MSL_GA_SCHEMA.Programacion p on p.id_programacion = a.id_programacion  where a.estado = 0 and p.semana = datepart(wk, '" + fecha + "') and a.id_proyecto = " + p.getIdProyecto();
        }
        return sql;
    }
    
    public ArrayList ActividadesPorProyecto(Proyecto p, String fecha) throws ClassNotFoundException, SQLException{
        Class.forName(dbInfo.getDriverMslGaDB());
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlMslGaDB(), dbInfo.getUsrMslGaDB(), dbInfo.getPasMslGaDB())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(this.getQueryActividadesPorProyecto(p, fecha))){
                    while(r.next()){
                        ActividadPorProyecto a;
                        if(result == null){
                            result = new ArrayList();
                        }
                        a = new ActividadPorProyecto(r.getString("id_consultor"), r.getInt("id_actividad"), r.getInt("id_programacion"), r.getDate("fecha_programacion"), r.getString("id_organizacion") ,r.getInt("jornada"), r.getInt("id_tipo_actividad"), r.getInt("id_proyecto"), r.getInt("estado"), r.getString("descripcion"));
                        result.add(a);
                    }
                }
            }
        }
        return result;
    }
}
