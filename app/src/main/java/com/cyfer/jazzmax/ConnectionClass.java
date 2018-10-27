package com.cyfer.jazzmax;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

    String ip = "10.0.0.8:1459";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "JazMaxDBProd";
    String un = "JMapp";
    String password = "JazzMax";
    String Azurecon="jdbc:jtds:sqlserver://jazmaxfinal.database.windows.net:1433;DatabaseName=JazMaxFinal;user=nikhil@jazmaxfinal;password=Passw0rd123*;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";


    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        Boolean inDevmode=true;
       if(inDevmode==false)
        {
            ip="cyferlan.ddns.net:1459";
        }
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            Log.e("CameraLogin", ConnURL+"");
            conn = DriverManager.getConnection(Azurecon);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }





}
