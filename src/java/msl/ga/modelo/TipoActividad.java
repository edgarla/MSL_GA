/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.modelo;

/**
 *
 * @author edgarloraariza
 */
public class TipoActividad {
    private int idTipoActividad;
    private String nombre;

    public TipoActividad(int idTipoActividad, String nombre) {
        this.idTipoActividad = idTipoActividad;
        this.nombre = nombre;
    }

    public int getIdTipoActividad() {
        return idTipoActividad;
    }

    public void setIdTipoActividad(int idTipoActividad) {
        this.idTipoActividad = idTipoActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getKey(){
        return this.idTipoActividad + "¨" + this.nombre;
    }
}
