/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.modelo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author edgarloraariza
 */
public class Actividad {
    private int idActividad;
    private int idProgramacion;
    private Date fechaDeEjecucion;
    private String idCliente;
    private int jornada;
    private int tipoActividad;
    private int estado;
    private String descripcion;

    public Actividad(int idActividad, int idProgramacion, Date fechaDeEjecucion, String idCliente, int jornada, int tipoActividad, int estado, String descripcion) {
        this.idActividad = idActividad;
        this.idProgramacion = idProgramacion;
        this.fechaDeEjecucion = fechaDeEjecucion;
        this.idCliente = idCliente;
        this.jornada = jornada;
        this.tipoActividad = tipoActividad;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdProgramacion() {
        return idProgramacion;
    }

    public void setIdProgramacion(int idProgramacion) {
        this.idProgramacion = idProgramacion;
    }

    public Date getFechaDeEjecucion() {
        return fechaDeEjecucion;
    }

    public void setFechaDeEjecucion(Date fechaDeEjecucion) {
        this.fechaDeEjecucion = fechaDeEjecucion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public int getJornada() {
        return jornada;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }

    public int getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(int tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    public String toString(ArrayList tipoActividades){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        int i = 0;
        TipoActividad ta = null;
        while(i < tipoActividades.size() && ta == null){
            if(((TipoActividad)tipoActividades.get(i)).getIdTipoActividad() == this.tipoActividad){
                ta = (TipoActividad)tipoActividades.get(i);
            }
            i = i + 1;
        }
        return this.idActividad + "¨" + sdf.format(fechaDeEjecucion) + "" + this.idCliente + "¨" + ta.getNombre() + "¨" + this.descripcion;
    }
    
    public String toHtml(Usuario u, ArrayList tipoActividades){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int i = 0;
        TipoActividad ta = null;
        while(i < tipoActividades.size() && ta == null){
            if(((TipoActividad)tipoActividades.get(i)).getIdTipoActividad() == this.tipoActividad){
                ta = (TipoActividad)tipoActividades.get(i);
            }
            i = i + 1;
        }
        String stringJornada = "";
        if(this.jornada == 1){
            stringJornada = "Mañana";
        }else{
            stringJornada = "Tarde";
        }
        String html = "";
        html = html + "<div class=\"actividad\" onclick=\"selecionarActividad('" + this.idActividad + "','" + u.getUserid() + "','" + sdf.format(fechaDeEjecucion) + "','" + this.jornada + "','" + this.idCliente + "','" + ta.getIdTipoActividad() + "','" + this.descripcion + "')\">";
        html = html + "<p class=\"campo\"><span>Actividad No:</span> " + this.idActividad + "</p>";
        html = html + "<p class=\"campo\"><span>Fecha:</span> " + sdf.format(fechaDeEjecucion) + "</p>";
        html = html + "<p class=\"campo\"><span>Cliente:</span> " + this.idCliente + "</p>";
        html = html + "<p class=\"campo\"><span>Jornada:</span> " + stringJornada + "</p>";
        html = html + "<p class=\"campo\"><span>Tipo Actividad:</span> " + ta.getNombre() + "</p>";
        html = html + "<p class=\"campo\"><span>Descripcion:</span> " + this.descripcion + "</p>";
        html = html + "</div>";
        return html;
    }
}
