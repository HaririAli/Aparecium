package Utilities;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALI
 */
public class SQLiteDbManager {
    
    private static Connection connection;
    
    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ApareciumDb.db");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Error in connection : " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            return connection;
        }
    }
    
    public static ResultSet executeReader(Connection connection, String query) {
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(query);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Error in retrieving materials : " + e.getMessage(), JOptionPane.ERROR_MESSAGE);

        } finally {
            return resultSet;
        }
    }
    
    public static int ExecuteNonQuery(Connection connection, String query){
            int affectedRows = 0;
        try {
            affectedRows = connection.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteDbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return affectedRows;
    }
    
}
