package game_list;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect_DB {
    private static final String DB_NAME = "topgames";
    private static final String URL = "jdbc:mysql://localhost:4306/" + DB_NAME;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnect() {
        Connection cn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection successful");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: Unable to connect to the database");
            e.printStackTrace();
        }
        return cn;
    }

    public static void main(String[] args) {
        try (Connection connection = Connect_DB.getConnect()) {
            if (connection != null) {
                System.out.println("Connected to database: " + DB_NAME);
            } else {
                System.out.println("Failed to connect to database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}