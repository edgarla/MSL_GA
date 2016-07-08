/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.modelo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author edgarloraariza
 */
public class ActividadPorProyecto extends Actividad{

    private String idusuario;
    
    public ActividadPorProyecto(String idusuario, int idActividad, int idProgramacion, Date fechaDeEjecucion, String idCliente, int jornada, int tipoActividad, int idProyecto, int estado, String descripcion) {
        super(idActividad, idProgramacion, fechaDeEjecucion, idCliente, jornada, tipoActividad, idProyecto, estado, descripcion);
        this.idusuario = idusuario;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }
    
    public String toHtml(Proyecto p, ArrayList tipoActividades, ArrayList usuarios){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int i = 0;
        TipoActividad ta = null;
        while(i < tipoActividades.size() && ta == null){
            if(((TipoActividad)tipoActividades.get(i)).getIdTipoActividad() == this.getTipoActividad()){
                ta = (TipoActividad)tipoActividades.get(i);
            }
            i = i + 1;
        }
        
        String stringJornada;
        if(this.getJornada() == 1){
            stringJornada = "Mañana";
        }else{
            stringJornada = "Tarde";
        }
        
        i = 0;
        Usuario u = null;
        while(i < usuarios.size() && u == null){
            if(((Usuario)usuarios.get(i)).getUserid().compareTo(this.getIdusuario()) == 0){
                u = (Usuario)usuarios.get(i);
            }
        }
        
        String html = "";
        html = html + "<div class=\"actividad\">";
        html = html + "<p class=\"campo\"><span>Actividad No:</span> " + this.getIdActividad() + "</p>";
        html = html + "<p class=\"campo\"><span>Consultor:</span> " + StringEscapeUtils.escapeHtml4(u.getNombre()) + "</p>";
        html = html + "<p class=\"campo\"><span>Fecha:</span> " + sdf.format(this.getFechaDeEjecucion()) + "</p>";
        html = html + "<p class=\"campo\"><span>Cliente:</span> " + StringEscapeUtils.escapeHtml4(this.getIdCliente()) + "</p>";
        html = html + "<p class=\"campo\"><span>Jornada:</span> " + StringEscapeUtils.escapeHtml4(stringJornada) + "</p>";
        html = html + "<p class=\"campo\"><span>Tipo Actividad:</span> " + StringEscapeUtils.escapeHtml4(ta.getNombre()) + "</p>";
        html = html + "<p class=\"campo\"><span>Proyecto:</span> " + StringEscapeUtils.escapeHtml4(p.getNombre()) + "</p>";
        html = html + "<p class=\"campo\"><span>Descripcion:</span> " + StringEscapeUtils.escapeHtml4(this.getDescripcion()) + "</p>";
        html = html + "</div>";
        return html;
    }
    
}
