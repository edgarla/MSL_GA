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
public class Usuario {
    private String userid;
    private String nombre;
    private String email;
    private String rol;

    public Usuario(String userid, String nombre, String email, String rol) {
        this.userid = userid;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String getKey(){
        return this.userid + "¨" + this.nombre;
    }
}
