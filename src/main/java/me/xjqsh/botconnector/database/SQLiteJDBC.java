package me.xjqsh.botconnector.database;


import java.sql.*;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class SQLiteJDBC {
    private Connection connection;
    public static SQLiteJDBC jdbc;

    private SQLiteJDBC(){}

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static SQLiteJDBC getInstance() {
        return jdbc;
    }

    public static void init() {
        jdbc=new SQLiteJDBC();
        try {
            jdbc.connection = DriverManager.getConnection("jdbc:sqlite:plugins/BotConnector/minecraft.db");
            Statement stmt = jdbc.connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS UserInfo " +
                    "(UID   INTEGER     PRIMARY KEY AUTOINCREMENT," +
                    " UUID  CHAR(30)    NOT NULL," +
                    " NAME  TEXT        NOT NULL)";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
