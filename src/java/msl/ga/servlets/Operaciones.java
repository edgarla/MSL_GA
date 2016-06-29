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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import msl.ga.db.DbInfo;
import msl.ga.ejb.ActividadEJB;
import msl.ga.ejb.ClienteEJB;
import msl.ga.ejb.ProgramacionEJB;
import msl.ga.ejb.ProyectoEJB;
import msl.ga.ejb.TipoActividadEJB;
import msl.ga.ejb.UsuarioEJB;
import msl.ga.modelo.Actividad;
import msl.ga.modelo.Cliente;
import msl.ga.modelo.Programacion;
import msl.ga.modelo.Proyecto;
import msl.ga.modelo.TipoActividad;
import msl.ga.modelo.Usuario;
import msl.ga.util.SendMailTLS;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author edgarloraariza
 */
@WebServlet(name = "Operaciones", urlPatterns = {"/Operaciones"})
public class Operaciones extends HttpServlet {
    
    private ArrayList listaUsuarios;
    private ArrayList listaClientes;
    private ArrayList listaTipoActividades;
    private ArrayList listaProyectos;
    private Usuario usuarioEnSesion;
    private String rol;
    private DbInfo dbInfo;

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
            if(this.inicializacion(request, response)){
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
                        this.logout(request);
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
                    case "deshabilitarActividad":
                        if(this.deshabilitarActividad(request, response)){
                            this.getListaDeActividadesPorConsultor(request, response);
                            this.sendEmailNotification(request, response);
                        }
                        break;
                    case "editarActividadDeConsultor":
                        if(this.editarActividadDeConsultor(request, response)){
                            this.getListaDeActividadesPorConsultor(request, response);
                            this.sendEmailNotification(request, response);
                        }
                        break;
                    case "getListaProyectos":
                        this.getListaProyectos(response);
                        break;
                }
            }
        } catch (Exception ex) {
            response.getWriter().println("error|Error General|" + ex.getMessage());
        }
    }
    
    private boolean inicializacion(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception{
        dbInfo = new DbInfo(getClass().getResource("/").getPath());
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
        if(this.listaProyectos == null){
            try {
                this.cargarListaProyectos();
            } catch (ClassNotFoundException | SQLException ex) {
                response.getWriter().println("error|cargando lista de proyectos|" + ex.getMessage());
            }
        }
        return true;
    }
    
    private void cargarUsuarios() throws ClassNotFoundException, SQLException, IOException{
        UsuarioEJB usuarioEJB = new UsuarioEJB(this.dbInfo);
        this.listaUsuarios = usuarioEJB.getListaDeUsuarios();
    }
    
    private void cargarClientes() throws ClassNotFoundException, SQLException, IOException{
        ClienteEJB clienteEJB = new ClienteEJB(this.dbInfo);
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
    
    private void logout(HttpServletRequest request){
        request.getSession().setAttribute("usuarioEnSesion", null);
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
    
    private void cargarTiposActividades() throws ClassNotFoundException, SQLException, IOException{
        TipoActividadEJB tipoActividadEJB = new TipoActividadEJB(this.dbInfo);
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
        String porDiaParameter = request.getParameter("porDia");
        String listaActividades = "";
        
        boolean porDia = false;
        if(porDiaParameter != null && porDiaParameter.equals("true")){
            porDia = true;
        }
        
        Usuario usuario = null;
        int i = 0;
        while(i < this.listaUsuarios.size() - 1 && usuario == null){
            Usuario u = (Usuario) this.listaUsuarios.get(i);
            if(u.getUserid().equals(usuarioParameter)){
                usuario =  u;
            }
            i = i + 1;
        }
        
        ProgramacionEJB programacionEJB = new ProgramacionEJB(this.dbInfo);
        ActividadEJB actividadesEJB = new ActividadEJB(this.dbInfo);
        try {
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, false);
            if(p != null){
                ArrayList actividades = actividadesEJB.getActividadesDeProgramacion(p, porDia, fechaParameter);
                for(i = 0; i < actividades.size(); i = i + 1){
                    Actividad a = (Actividad) actividades.get(i);
                    listaActividades = listaActividades + a.toHtml(usuario, this.listaTipoActividades, this.listaProyectos);
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
        String idProyectoParameter = request.getParameter("idProyecto");
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
        
        if(idProyectoParameter == null || idProyectoParameter.compareTo("") == 0){
            idProyectoParameter = "0";
        }
        
        ProgramacionEJB programacionEJB = new ProgramacionEJB(this.dbInfo);
        ActividadEJB actividadesEJB = new ActividadEJB(this.dbInfo);
        
        try {
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            Actividad a = new Actividad(0, p.getIdProgramacion(), sdf.parse(fechaParameter), clienteParamenter, Integer.parseInt(jornadaParameter), Integer.parseInt(tipoActividadParameter), Integer.parseInt(idProyectoParameter), 0, descripcionParameter);
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
    
    public boolean deshabilitarActividad(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            String idActividadParameter = request.getParameter("idActividad");
            String usuarioParameter = request.getParameter("consultor");
            String fechaParameter = request.getParameter("fecha");
            String jornadaParameter = request.getParameter("jornada");
            String clienteParamenter = request.getParameter("cliente");
            String tipoActividadParameter = request.getParameter("tipoActividad");
            String idProyectoParameter = request.getParameter("idProyecto");
            String descripcionParameter = request.getParameter("descripcion");
            
            Usuario usuario = null;
            int i = 0;
            while(i < this.listaUsuarios.size() - 1 && usuario == null){
                Usuario u = (Usuario) this.listaUsuarios.get(i);
                if(u.getUserid().equals(usuarioParameter)){
                    usuario =  u;
                }
                i = i + 1;
            }
            
            if(idProyectoParameter == null || idProyectoParameter.compareTo("") == 0){
                idProyectoParameter = "0";
            }

            ProgramacionEJB programacionEJB = new ProgramacionEJB(this.dbInfo);
            
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            Actividad a = new Actividad(Integer.parseInt(idActividadParameter), p.getIdProgramacion(), sdf.parse(fechaParameter), clienteParamenter, Integer.parseInt(jornadaParameter), Integer.parseInt(tipoActividadParameter), Integer.parseInt(idProyectoParameter), 1, descripcionParameter);
            ActividadEJB actividadesEJB = new ActividadEJB(this.dbInfo);
            actividadesEJB.ActualizarEstadoActividad(a);
            return true;
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            response.getWriter().println("error|Error Actualizando Estado de Actividad|" + ex.getMessage());
            return false;
        }
    }
    
    private boolean editarActividadDeConsultor(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            String idActividadParameter = request.getParameter("idActividad");
            String usuarioParameter = request.getParameter("consultor");
            String fechaParameter = request.getParameter("fecha");
            String jornadaParameter = request.getParameter("jornada");
            String clienteParamenter = request.getParameter("cliente");
            String tipoActividadParameter = request.getParameter("tipoActividad");
            String idProyectoParameter = request.getParameter("idProyecto");
            String descripcionParameter = request.getParameter("descripcion");
            
            Usuario usuario = null;
            int i = 0;
            while(i < this.listaUsuarios.size() - 1 && usuario == null){
                Usuario u = (Usuario) this.listaUsuarios.get(i);
                if(u.getUserid().equals(usuarioParameter)){
                    usuario =  u;
                }
                i = i + 1;
            }
            
            if(idProyectoParameter == null || idProyectoParameter.compareTo("") == 0){
                idProyectoParameter = "0";
            }

            ProgramacionEJB programacionEJB = new ProgramacionEJB(this.dbInfo);
            ActividadEJB actividadesEJB = new ActividadEJB(this.dbInfo);
            
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            Actividad a = new Actividad(Integer.parseInt(idActividadParameter), p.getIdProgramacion(), sdf.parse(fechaParameter), clienteParamenter, Integer.parseInt(jornadaParameter), Integer.parseInt(tipoActividadParameter), Integer.parseInt(idProyectoParameter), 1, descripcionParameter);
            actividadesEJB.ActualizarEstadoActividad(a);
            
            Actividad actividadEditada = new Actividad(0, p.getIdProgramacion(), sdf.parse(fechaParameter), clienteParamenter, Integer.parseInt(jornadaParameter), Integer.parseInt(tipoActividadParameter), Integer.parseInt(idProyectoParameter), 0, descripcionParameter);
            actividadesEJB.guardarActividad(actividadEditada);
            return true;
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            response.getWriter().println("error|Error Editando Actividad|" + ex.getMessage());
            return false;
        } catch (Exception ex) {
            response.getWriter().println("error|Error Editando Actividad|" + ex.getMessage());
            return false;
        }
    }
    
    private void cargarListaProyectos() throws ClassNotFoundException, SQLException, IOException{
        ProyectoEJB proyectoEJB = new ProyectoEJB(this.dbInfo);
        this.listaProyectos =  proyectoEJB.getListaProyectos();
    }
    
    private void getListaProyectos(HttpServletResponse response) throws IOException{
        String stringListaProyectos = "";
        for(int i = 0; i < this.listaProyectos.size() - 2; i = i + 1){
            Proyecto p = (Proyecto) this.listaProyectos.get(i);
            stringListaProyectos = stringListaProyectos + p.getKey() + "|";
        }
        Proyecto p = (Proyecto) this.listaProyectos.get(this.listaProyectos.size() - 1);
        stringListaProyectos = stringListaProyectos + p.getKey();
        response.getWriter().println(stringListaProyectos);
    }
    
    private void sendEmailNotification(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
        
        ProgramacionEJB programacionEJB = new ProgramacionEJB(this.dbInfo);
        ActividadEJB actividadesEJB = new ActividadEJB(this.dbInfo);
        try {
            Programacion p = programacionEJB.getProgramacion(usuario, fechaParameter, false);
            if(p != null){
                ArrayList actividades = actividadesEJB.getActividadesDeProgramacion(p, false, fechaParameter);
                for(i = 0; i < actividades.size(); i = i + 1){
                    Actividad a = (Actividad) actividades.get(i);
                    listaActividades = listaActividades + a.toHtml(usuario, this.listaTipoActividades, this.listaProyectos);
                }
                
                String emailContent = "<html>";
                emailContent = emailContent + "<head>";
                emailContent = emailContent + "<meta charset=\"UTF-8\">";
                emailContent = emailContent + "<style>";
                emailContent = emailContent + ".titulo{text-align: center;font-family: Lucida Grande,Lucida Sans,Arial,sans-serif;font-size: 24px;font-weight: bold;color: #2779aa;}";
                emailContent = emailContent + "div.actividad{margin-left: 5px;margin-right: 5px;margin-bottom: 2px;padding: 5px;border: 1px solid #aed0ea;}";
                emailContent = emailContent + "div.actividad p.campo{font-size: 12px;font-family: Lucida Grande,Lucida Sans,Arial,sans-serif;display: inline-block;margin-right: 5px;margin-top: 0px;margin-bottom: 2px;}";
                emailContent = emailContent + "div.actividad p.campo span{font-weight: bold;font-family: Lucida Grande,Lucida Sans,Arial,sans-serif;font-size: 15px;color: #2779aa;}";
                emailContent = emailContent + "</style>";
                emailContent = emailContent + "</head>";
                emailContent = emailContent + "<body>";
                emailContent = emailContent + "<h3 class=\"titulo\">" + StringEscapeUtils.escapeHtml4("Actualización de Agenda") + "</h3>";
                emailContent = emailContent + "<p>Tu agenda ha sido actualizada</p>";
                emailContent = emailContent + "<div>";
                emailContent = emailContent + listaActividades;
                emailContent = emailContent + "</div id=\"listaActividadesConsultor\">";
                emailContent = emailContent + "</body>";
                emailContent = emailContent + "</html>";
                SendMailTLS sendMailTLS = new SendMailTLS();
                sendMailTLS.sendEmail(usuario.getEmail(), "Actualización de Agenda", emailContent);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            response.getWriter().println("error|Error Enviando Notificación|" + ex.getMessage());
        } catch (Exception ex) {
            response.getWriter().println("error|Error Enviando Notificación|" + ex.getMessage());
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
