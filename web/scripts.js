/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var usuarioEnSesion = null;

function onLoad(){
    haySession();
}

function mostrarMensaje(response){
    var errorMessage = response.split("|");
    $( "#AlertPanel" ).dialog({
        title: errorMessage[1],
        resizable: false,
        modal: true,
        autoOpen: false,
        show: {
          effect: "fade",
          duration: 1000
        },
        hide: {
          effect: "fade",
          duration: 1000
        },
        open:function(){
            $(this).html("<span class=\"ui-icon ui-icon-alert\" style=\"float:left; margin:0 7px 20px 0;\"></span>" + errorMessage[2]);
        },
        buttons: {
          "Aceptar": function() {
            $( this ).dialog( "close" );
          }
        }
    });
    $("#AlertPanel").dialog("open");
}

function cargarUsuarios(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "getListaUsuarios"},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                var usuarios = response.split("|");
                $("#usuario").html("<option></option>");
                $("#listaConsultores").html("<option></option>");
                for(var i = 0; i < usuarios.length; i = i + 1){
                    var usuario = usuarios[i].split("¨");
                    $("#usuario").html($("#usuario").html() + "<option value=\"" + usuario[0] + "\">" + usuario[1] + "</option>");
                    $("#listaConsultores").html($("#listaConsultores").html() + "<option value=\"" + usuario[0] + "\">" + usuario[1] + "</option>");
                }
            }
        }
    });
}

function login(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "login", usuario: $("#usuario").val(), clave: $("#clave").val()},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                usuarioEnSesion = response.split("|");
                $("#LoginPanel").hide();
                $("#idUsuario").val(usuarioEnSesion[2]);
                if(usuarioEnSesion[4].match("administrador")){
                    initAdministrador();
                    $("#administrador").fadeIn();
                }else if(usuarioEnSesion[4].match("pm")){
                    initPM();
                    $("#pm").fadeIn();
                }else if(usuarioEnSesion[4].match("consultor")){
                    initConsultor();
                    actividadesDelConsultor2();
                    $("#consultor").fadeIn();
                }
            }
        }
    });
}

function loginOnEnter(e){
    if(e.which === 13) {
        login();
    }
}

function haySession(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "haySesion"},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                if(response.match("^usuario")){
                    usuarioEnSesion = response.split("|");
                    if(usuarioEnSesion[1].match("none")){
                        cargarUsuarios();
                        $("#LoginPanel").fadeIn();
                        $("#usuario").selectmenu();
                    }else{
                        if(usuarioEnSesion[4].match("administrador")){
                            initAdministrador();
                            $("#administrador").fadeIn();
                        }else if(usuarioEnSesion[4].match("pm")){
                            initPM();
                            $("#pm").fadeIn();
                        }else if(usuarioEnSesion[4].match("consultor")){
                            initConsultor();
                            actividadesDelConsultor2();
                            $("#consultor").fadeIn();
                        }
                    }
                }
            }
        }
    });
}

function logout(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "logout"},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                if(usuarioEnSesion[4].match("administrador")){
                    $("#administrador").hide();
                }else if(usuarioEnSesion[4].match("pm")){
                    $("#pm").hide();
                }else if(usuarioEnSesion[4].match("consultor")){
                    $("#consultor").hide();
                }
                usuarioEnSesion = null;
                location.reload();
            }
        }
    });
}

function initAdministrador(){
    cargarUsuarios();
    cargarClientes();
    cargarTipoActividades();
    cargarProyectos("#proyecto");
    $("#botonAgregarActividad").button({
        icons: {
            primary: "ui-icon-disk"
        }
    });
    $("#botonEditarActividad").button({
        icons: {
            primary: "ui-icon-wrench"
        }
    });
    $("#botonEliminarActividad").button({
        icons: {
            primary: "ui-icon-trash"
        }
    });
    $("#botonCancelarActividad").button({
        icons: {
            primary: "ui-icon-cancel"
        }
    });
    $("#botonEditarActividad").hide();
    $("#botonEliminarActividad").hide();
    $("#botonCancelarActividad").hide();
    $("#acordeonAdmin").accordion();
    $("#listaConsultores").selectmenu({
        change: function(event, data){
            actividadesDelConsultor();
            cancelarEditarActividad();
        }
    });
    $("#jornada").selectmenu();
    $("#listaClientes").selectmenu();
    $("#tipoActividad").selectmenu();
    $("#proyecto").selectmenu();
    $("#fechaActividad").datepicker({
        dateFormat: 'dd-mm-yy',
        onSelect: function(){
            actividadesDelConsultor();
            resetFormularioActividades();
        }
    });
}

function initConsultor(){
    $("#acordeonConsultor").accordion();
    $("#fechaActividadConsultor").datepicker({
        dateFormat: 'dd-mm-yy',
        onSelect: function(){
            actividadesDelConsultor2();
        }
    });
}

function initPM(){
    cargarProyectos("#listaProyectos");
    $("#acordeonPM").accordion();
    $("#listaProyectos").selectmenu({
        change: function(event, data){
            consultarActividadesPorProyectos();
        }
    });
    $("#fechaActividadPM").datepicker({
        dateFormat: 'dd-mm-yy',
        onSelect: function(){
            consultarActividadesPorProyectos();
        }
    });
}

function cargarClientes(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "getListaClientes"},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                var clientes = response.split("|");
                for(var i = 0; i < clientes.length; i = i + 1){
                    var cliente = clientes[i];
                    $("#listaClientes").html($("#listaClientes").html() + "<option value=\"" + cliente + "\">" + cliente + "</option>");
                }
            }
        }
    });
}

function cargarTipoActividades(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "getListaTipoActividades"},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                var TipoActividades = response.split("|");
                for(var i = 0; i < TipoActividades.length; i = i + 1){
                    var tipoActividad = TipoActividades[i].split("¨");
                    $("#tipoActividad").html($("#tipoActividad").html() + "<option value=\"" + tipoActividad[0] + "\">" + tipoActividad[1] + "</option>");
                }
            }
        }
    });
}

function actividadesDelConsultor(){
    if($("#listaConsultores").val() !== ""){
        $.ajax({
            url: "/MSL_GestionDeActividades/Operaciones",
            type: "POST",
            data: {accion: "getListaActividadesDeConsultor", consultor: $("#listaConsultores").val(), porDia: $("#porDia").prop('checked'), fecha: $("#fechaActividad").val()},
            success:function(response){
                if(response.match("^error")){
                    mostrarMensaje(response);
                }else{
                    $("#listaActividades").html(response);
                }
            }
        });
    }
}

function actividadesDelConsultor2(){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "getListaActividadesDeConsultor", consultor: $("#idUsuario").val(), fecha: $("#fechaActividadConsultor").val()},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                $("#listaActividadesConsultor").html(response);
            }
        }
    });
}

function agregarActividadAConsultor(){
    if($("#listaConsultores").val() !== "" && $("#jornada").val() !== "" && $("#listaClientes").val() !== "" && $("#panelTipoActividad").val() !== ""){
        $.ajax({
            url: "/MSL_GestionDeActividades/Operaciones",
            type: "POST",
            data: {accion: "agregarActividadAConsultor", 
                consultor: $("#listaConsultores").val(), 
                fecha: $("#fechaActividad").val(),
                jornada: $("#jornada").val(),
                cliente: $("#listaClientes").val(),
                tipoActividad: $("#tipoActividad").val(),
                idProyecto: $("#proyecto").val(),
                descripcion: $("#descripcion").val()},
            success:function(response){
                if(response.match("^error")){
                    mostrarMensaje(response);
                }else{
                    $("#listaActividades").html(response);
                }
            }
        });
    }else{
        mostrarMensaje("error|Seleccione Todos los Datos|Es necesario que el consultor, la jornada, el ciente y el tipo de actividad sean seleccionadas");
    }
}

function selecionarActividad(idActividad, consultor, fecha, jornada, cliente, tipoActividad, idProyecto, descripcion){
    $("#idActividad").val(idActividad);
    
    $("#listaConsultores").val(consultor);
    $("#listaConsultores").selectmenu("refresh");
    
    var valFecha = fecha.split("-");
    $('#fechaActividad').datepicker("setDate", new Date(valFecha[2], valFecha[1] - 1, valFecha[0]));
    
    $("#jornada").val(jornada);
    $("#jornada").selectmenu("refresh");
    
    $("#listaClientes").val(cliente);
    $("#listaClientes").selectmenu("refresh");
    
    $("#tipoActividad").val(tipoActividad);
    $("#tipoActividad").selectmenu("refresh");
    
    if(idProyecto === '0'){
        $("#proyecto").val("");
        $("#proyecto").selectmenu("refresh");
    }else{
        $("#proyecto").val(idProyecto);
        $("#proyecto").selectmenu("refresh");
    }
    
    $("#descripcion").val(descripcion);
    
    $("#botonAgregarActividad").hide();
    $("#botonEditarActividad").show();
    $("#botonEliminarActividad").show();
    $("#botonCancelarActividad").show();
}

function resetFormularioActividades(){
    $("#idActividad").val("");
    
    $("#jornada").val("");
    $("#jornada").selectmenu("refresh");
    
    $("#listaClientes").val("");
    $("#listaClientes").selectmenu("refresh");
    
    $("#tipoActividad").val("");
    $("#tipoActividad").selectmenu("refresh");
    
    $("#proyecto").val("");
    $("#proyecto").selectmenu("refresh");
    
    $("#descripcion").val("");
    
    $("#botonEditarActividad").hide();
    $("#botonEliminarActividad").hide();
    $("#botonCancelarActividad").hide();
    $("#botonAgregarActividad").show();
}

function cancelarEditarActividad(){
    $("#idActividad").val("");
    
    $('#fechaActividad').datepicker("setDate", new Date());
    
    $("#jornada").val("");
    $("#jornada").selectmenu("refresh");
    
    $("#listaClientes").val("");
    $("#listaClientes").selectmenu("refresh");
    
    $("#tipoActividad").val("");
    $("#tipoActividad").selectmenu("refresh");
    
    $("#proyecto").val("");
    $("#proyecto").selectmenu("refresh");
    
    $("#descripcion").val("");
    
    $("#botonEditarActividad").hide();
    $("#botonEliminarActividad").hide();
    $("#botonCancelarActividad").hide();
    $("#botonAgregarActividad").show();
}

function deshabilitarActividadAConsultor(){
    if($("#listaConsultores").val() !== "" && $("#jornada").val() !== "" && $("#listaClientes").val() !== "" && $("#panelTipoActividad").val() !== ""){
        $.ajax({
            url: "/MSL_GestionDeActividades/Operaciones",
            type: "POST",
            data: {accion: "deshabilitarActividad", 
                idActividad: $("#idActividad").val(),
                consultor: $("#listaConsultores").val(), 
                fecha: $("#fechaActividad").val(),
                jornada: $("#jornada").val(),
                cliente: $("#listaClientes").val(),
                tipoActividad: $("#tipoActividad").val(),
                idProyecto: $("#proyecto").val(),
                descripcion: $("#descripcion").val()},
            success:function(response){
                if(response.match("^error")){
                    mostrarMensaje(response);
                }else{
                    $("#listaActividades").html(response);
                    resetFormularioActividades();
                }
            }
        });
    }else{
        mostrarMensaje("error|Seleccione Todos los Datos|Es necesario que el consultor, la jornada, el ciente y el tipo de actividad sean seleccionadas");
    }
}

function editarActividadAConsultor(){
    if($("#listaConsultores").val() !== "" && $("#jornada").val() !== "" && $("#listaClientes").val() !== "" && $("#panelTipoActividad").val() !== ""){
        $.ajax({
            url: "/MSL_GestionDeActividades/Operaciones",
            type: "POST",
            data: {accion: "editarActividadDeConsultor", 
                idActividad: $("#idActividad").val(),
                consultor: $("#listaConsultores").val(), 
                fecha: $("#fechaActividad").val(),
                jornada: $("#jornada").val(),
                cliente: $("#listaClientes").val(),
                tipoActividad: $("#tipoActividad").val(),
                idProyecto: $("#proyecto").val(),
                descripcion: $("#descripcion").val()},
            success:function(response){
                if(response.match("^error")){
                    mostrarMensaje(response);
                }else{
                    $("#listaActividades").html(response);
                    resetFormularioActividades();
                }
            }
        });
    }else{
        mostrarMensaje("error|Seleccione Todos los Datos|Es necesario que el consultor, la jornada, el ciente y el tipo de actividad sean seleccionadas");
    }
}

function cargarProyectos(elemento){
    $.ajax({
        url: "/MSL_GestionDeActividades/Operaciones",
        type: "POST",
        data: {accion: "getListaProyectos"},
        success:function(response){
            if(response.match("^error")){
                mostrarMensaje(response);
            }else{
                var proyectos = response.split("|");
                for(var i = 0; i < proyectos.length; i = i + 1){
                    var proyecto = proyectos[i].split("¨");
                    $(elemento).html($(elemento).html() + "<option value=\"" + proyecto[0] + "\">" + proyecto[1] + "</option>");
                }
            }
        }
    });
}

function consultarActividadesPorProyectos(){
    if($("#listaProyectos").val() !== ""){
        $.ajax({
            url: "/MSL_GestionDeActividades/Operaciones",
            type: "POST",
            data: {accion: "getActividadesPorProyecto",
                    idproyecto: $("#listaProyectos").val(),
                    fecha: $("#fechaActividadPM").val()},
            success:function(response){
                if(response.match("^error")){
                    mostrarMensaje(response);
                }else{
                    $("#listaActividadesPM").html(response);
                }
            }
        });
    }
}