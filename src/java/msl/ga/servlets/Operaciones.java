/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import msl.ga.ejb.ActividadEJB;
import msl.ga.ejb.ClienteEJB;
import msl.ga.ejb.ProgramacionEJB;
import msl.ga.ejb.TipoActividadEJB;
import msl.ga.ejb.UsuarioEJB;
import msl.ga.modelo.Actividad;
import msl.ga.modelo.Cliente;
import msl.ga.modelo.Programacion;
import msl.ga.modelo.TipoActividad;
import msl.ga.modelo.Usuario;

/**
 *
 * @author edgarloraariza
 */
@WebServlet(name = "Operaciones", urlPatterns = {"/Operaciones"})
public class Operaciones extends HttpServlet {
    
    private ArrayList listaUsuarios;
    private ArrayList listaClientes;
    private ArrayList listaTipoActividades;
    private Usuario usuarioEnSesion;
    private String rol;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            if(this.inicializacion(response)){
                String accion = request.getParameter("accion");
                
                switch (accion) {
                    case "getListaUsuarios":
                        this.getListaUsuarios(response);
                        break;
                    case "login":
                        this.login(request, response);
                        break;
                    case "haySesion":
                        this.haySesion(request, response);
                        break;
                    case "logout":
                        this.logout();
                        break;
                    case "getListaClientes":
                        this.getListaClientes(response);
                        break;
                    case "getListaTipoActividades":
                        this.getListaTipoActividades(response);
                        break;
                    case "getListaActividadesDeConsultor":
                        this.getListaDeActividadesPorConsultor(request, response);
                        break;
                    case "agregarActividadAConsultor":
                            if(this.agregarActividadAConsultor(request, response)){
                                this.getListaDeActividadesPorConsultor(request, response);
                            }
                        break;
                }
            }
        }
    }
    
    private boolean inicializacion(HttpServletResponse response) throws IOException{
        if(this.listaUsuarios == null){
            try {
                this.cargarUsuarios();
            } catch (ClassNotFoundException | SQLException ex) {
                response.getWriter().println("error|cargando usuarios|" + ex.getMessage());
                return false;
            }
        }
        if(this.listaClientes ==  null){
            try {
                this.cargarClientes();
            } catch (ClassNotFoundException | SQLException ex) {
                response.getWriter().println("error|cargando clientes|" + ex.getMessage());
                return false;
            }
        }
        if(this.listaTipoActividades == null){
            try {
                this.cargarTiposActividades();
            } catch (ClassNotFoundException | SQLException ex) {
                response.getWriter().println("error|cargando tipo de actividades|" + ex.getMessage());
            }
        }
        return true;
    }
    
    private void cargarUsuarios() throws ClassNotFoundException, SQLException{
        UsuarioEJB usuarioEJB = new UsuarioEJB();
        this.listaUsuarios = usuarioEJB.getListaDeUsuarios();
    }
    
    private void cargarClientes() throws ClassNotFoundException, SQLException{
        ClienteEJB clienteEJB = new ClienteEJB();
        this.listaClientes = clienteEJB.getListaDeClientes();
    }
    
    private void getListaUsuarios(HttpServletResponse response) throws IOException{
        String stringListaUsuarios = "";
        for(int i = 0; i < this.listaUsuarios.size() - 2; i = i + 1){
            Usuario u = (Usuario) this.listaUsuarios.get(i);
            stringListaUsuarios = stringListaUsuarios + u.getKey() + "|";
        }
        Usuario u = (Usuario) this.listaUsuarios.get(this.listaUsuarios.size() - 1);
        stringListaUsuarios = stringListaUsuarios + u.getKey();
        response.getWriter().println(stringListaUsuarios);
    }
    
    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");
        boolean login = false;
        
        int i = 0;
        while(i < this.listaUsuarios.size() && login == false){
            Usuario u = (Usuario)this.listaUsuarios.get(i);
            if(u.getUserid().equals(usuario) && u.getUserid().equals(clave)){
                login = true;
                this.usuarioEnSesion = u;
                if(u.getRol() != null){
                    rol = u.getRol();
                }else{
                    rol = "consultor";
                }
            }
            i = i + 1;
        }
        
        if(login){
            request.getSession().setAttribute("usuarioEnSesion", this.usuarioEnSesion);
            response.getWriter().println("usuario|" + this.usuarioEnSesion.getNombre() + "|" + this.usuarioEnSesion.getUserid() + "|" + this.usuarioEnSesion.getEmail() + "|" + rol);
        }else{
            response.getWriter().println("error|Ingreso Errado|Los datos ingresados son incorrectos.");
        }
    }
    
    private void haySesion(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.usuarioEnSesion = (Usuario)request.getSession().getAttribute("usuarioEnSesion");
        if(this.usuarioEnSesion != null){
            response.getWriter().println("usuario|" + this.usuarioEnSesion.getNombre() + "|" + this.usuarioEnSesion.getUserid() + "|" + this.usuarioEnSesion.getEmail() + "|" + rol);
        }else{
            response.getWriter().println("usuario|none");
        }
    }
    
    private void logout(){
        this.usuarioEnSesion = null;
    }
    
    private void getListaClientes(HttpServletResponse response) throws IOException{
        String stringListaClientes = "";
        for(int i = 0; i < this.listaClientes.size() - 2; i = i + 1){
            Cliente c = (Cliente) this.listaClientes.get(i);
            stringListaClientes = stringListaClientes + c.getNombre() + "|";
        }
        Cliente c = (Cliente) this.listaClientes.get(this.listaClientes.size() - 1);
        stringListaClientes = stringListaClientes + c.getNombre();
        response.getWriter().println(stringListaClientes);
    }
    
    private void cargarTiposActividades() throws ClassNotFoundException, SQLException{
        TipoActividadEJB tipoActividadEJB = new TipoActividadEJB();
        this.listaTipoActividades = tipoActividadEJB.getListaDeActividades();
    }
    
    private void getListaTipoActividades(HttpServletResponse response) throws IOException{
        String stringListaTipoActividades = "";
        for(int i = 0; i < this.listaTipoActividades.size() - 2; i = i + 1){
            TipoActividad ta = (TipoActividad)this.listaTipoActividades.get(i);
            stringListaTipoActividades = stringListaTipoActividades + ta.getKey() + "|";
        }
        TipoActividad ta = (TipoActividad)this.listaTipoActividades.get(listaTipoActividades.size() - 1);
        stringListaTipoActividades = stringListaTipoActividades + ta.getKey();
        response.getWriter().println(stringListaTipoActividades);
    }
    
    private void getListaDeActividadesPorConsultor(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String usuarioParameter = request.getParameter("consultor");
        String fechaParameter = request.getParameter("fecha");
        String listaActividades = "";
        
        Usuario usuario = null;
        int i = 0;
        while(i < this.listaUsuarios.size() - 1 && usuario == null){
            Usuario u = (Usuario) this.listaUsuarios.get(i);
            if(u.getUserid().equals(usuarioParameter)){
                usuario =  u;
            }
            i = i + 1;
        }
        
        ProgramacionEJB programacionEJB = new ProgramacionEJB();
        ActividadEJB actividadesEJB = new ActividadEJB();
        try {
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, false);
            if(p != null){
                ArrayList actividades = actividadesEJB.getActividadesDeProgramacion(p);
                for(i = 0; i < actividades.size(); i = i + 1){
                    Actividad a = (Actividad) actividades.get(i);
                    listaActividades = listaActividades + a.toHtml(this.listaTipoActividades);
                }
                response.getWriter().println(listaActividades);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            response.getWriter().println("error|Ingreso Errado|" + ex.getMessage());
        }
    }
    
    private boolean agregarActividadAConsultor(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String usuarioParameter = request.getParameter("consultor");
        String fechaParameter = request.getParameter("fecha");
        String jornadaParameter = request.getParameter("jornada");
        String clienteParamenter = request.getParameter("cliente");
        String tipoActividadParameter = request.getParameter("tipoActividad");
        String descripcionParameter = request.getParameter("descripcion");
        String listaActividades = "";
        
        Usuario usuario = null;
        int i = 0;
        while(i < this.listaUsuarios.size() - 1 && usuario == null){
            Usuario u = (Usuario) this.listaUsuarios.get(i);
            if(u.getUserid().equals(usuarioParameter)){
                usuario =  u;
            }
            i = i + 1;
        }
        
        ProgramacionEJB programacionEJB = new ProgramacionEJB();
        ActividadEJB actividadesEJB = new ActividadEJB();
        
        try {
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            Actividad a = new Actividad(0, p.getIdProgramacion(), sdf.parse(fechaParameter), clienteParamenter, Integer.parseInt(jornadaParameter), Integer.parseInt(tipoActividadParameter), 0, descripcionParameter);
            actividadesEJB.guardarActividad(a);
            return true;
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            response.getWriter().println("error|Ingreso Errado|" + ex.getMessage());
            return false;
        } catch (Exception ex) {
            response.getWriter().println("error|Ingreso Errado|" + ex.getMessage());
            return false;
        } 
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
