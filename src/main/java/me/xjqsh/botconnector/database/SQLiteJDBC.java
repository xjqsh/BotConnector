package me.xjqsh.botconnector.database;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                    " UUID  CHAR(30)    UNIQUE NOT NULL," +
                    " QNUM  CHAR(30)    UNIQUE NOT NULL)";
            stmt.executeUpdate(sql);

            stmt.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        jdbc.prepareQuery();
    }
    private void prepareQuery(){
        try {
            getInstance().getByUUID = getInstance().connection.prepareStatement(
                    "SELECT UUID,QNUM FROM UserInfo WHERE UUID=?"
            );

            getInstance().getByQnum = getInstance().connection.prepareStatement(
                    "SELECT UUID,QNUM FROM UserInfo WHERE QNUM=?"
            );

            getInstance().bind = getInstance().connection.prepareStatement(
                    "INSERT INTO UserInfo (UUID, QNUM) VALUES(?,?)"
            );

            getInstance().unbind = getInstance().connection.prepareStatement(
                    "DELETE FROM UserInfo WHERE QNUM=?"
            );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    private PreparedStatement getByUUID;
    private PreparedStatement getByQnum;
    private PreparedStatement bind;
    private PreparedStatement unbind;
    @Nullable
    public static UUID getByQnum(@NotNull String qq){
        try {
            getInstance().getByQnum.setString(1,qq);
            var result = getInstance().getByQnum.executeQuery();
            if(result.next()){
                String str = result.getString("UUID");
                return UUID.fromString(str);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static String getByUUID(@NotNull UUID uuid){
        try {
            getInstance().getByUUID.setString(1,uuid.toString());
            var result = getInstance().getByUUID.executeQuery();
            if(result.next()){
                return result.getString("QNUM");
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean bind(@NotNull UUID uuid, @NotNull String qnum){
        try {
            getInstance().bind.setString(1,uuid.toString());
            getInstance().bind.setString(2,qnum);
            return getInstance().bind.executeUpdate()>0;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean unbind(@NotNull String qnum){
        try {
            getInstance().bind.setString(1,qnum);
            return getInstance().bind.executeUpdate()>0;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }
}
