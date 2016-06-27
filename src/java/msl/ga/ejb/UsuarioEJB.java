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
import java.util.ArrayList;
import msl.ga.db.DbInfo;
import msl.ga.modelo.Usuario;

/**
 *
 * @author edgarloraariza
 */
public class UsuarioEJB {
    private final DbInfo dbInfo;
    private final String queryConsultaUsuarios = "select cc.userid, cc.first_name + ' ' + cc.last_name as nombre, cc.email_address, r.nombre_rol from ca_contact cc  left join MSL_GA_SCHEMA.Rol_Usuario ru on cc.userid = ru.id_usuario left join MSL_GA_SCHEMA.Rol r on ru.id_rol = r.id_rol where (cc.contact_type = 2301 or cc.contact_type = 2307) and email_address is not null and cc.first_name is not null order by nombre";

    public UsuarioEJB(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
    
    public ArrayList getListaDeUsuarios() throws ClassNotFoundException, SQLException, IOException{
        Class.forName(dbInfo.getDriverServiceDeskDB());
        ArrayList result = null;
        try (Connection conn = DriverManager.getConnection(dbInfo.getUrlServiceDeskDB(), dbInfo.getUsrServiceDesk(), dbInfo.getPasServiceDesk())) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet r = st.executeQuery(queryConsultaUsuarios)) {
                    while(r.next()){
                        if(result == null){
                            result = new ArrayList();
                        }
                        Usuario u = new Usuario(r.getString("userid"), r.getString("nombre"), r.getString("email_address"), r.getString("nombre_rol"));
                        result.add(u);
                    }
                }
            }
        }
        return result;
    }
}
