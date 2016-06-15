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
public class Programacion {
    private int idProgramacion;
    private String idUsuario;
    private int semana;

    public Programacion(int idProgramacion, String idUsuario, int semana) {
        this.idProgramacion = idProgramacion;
        this.idUsuario = idUsuario;
        this.semana = semana;
    }

    public int getIdProgramacion() {
        return idProgramacion;
    }

    public void setIdProgramacion(int idProgramacion) {
        this.idProgramacion = idProgramacion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }
    
}
