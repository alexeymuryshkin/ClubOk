
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aibek
 */
public class MyDB {
    Connection conn;
    
    public Connection getConn(){
        
        String name = "mona";
        String password = "Hj^W'A98";
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://46.101.171.158:80/Aibek", name, password);
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MyDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return conn;
    }
    
    
}
