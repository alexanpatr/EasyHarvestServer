package com.www.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceUnregister extends Thread {

    String DB_SERVER = Globals.db_server;
    String DB_USERNAME = Globals.db_username;
    String DB_PASSWORD = Globals.db_password;

    ResultSet rs = null;
    Statement s = null;
    Connection c = null;

    DeviceUnregister() {
        super("DeviceUnregister thread");
        System.out.println("DeviceUnregister: " + this);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection(DB_SERVER, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DeviceUnregister.class.getName()).log(Level.SEVERE, null, ex);
        }
        start();
    }

    public void run() {
        try {
            while (true) {
                s = c.createStatement();
                s.executeQuery("SELECT * FROM devices");
                rs = s.getResultSet();
                if (rs.next()) {
                    do {
                        String id = rs.getString("id");
                        String date = rs.getString("last_date");
                        String time = rs.getString("last_time");
                        if (!isTimeRecent(time)) {
                            System.out.println("DeviceUnregister: Unregistering device with id " + id + "...");
                            s = c.createStatement();
                            s.executeUpdate("DELETE FROM devices WHERE id='" + id + "'");
                        }/**/
                        /*if (!isDateRecent(date)) {
                         System.out.println("DeviceUnregister: Unregistering device with id " + id + "...");
                         s = c.createStatement();
                         s.executeUpdate("DELETE FROM devices WHERE id='" + id + "'");
                         }/**/

                    } while (rs.next());
                    Thread.sleep(6 * 10000);
                    //Thread.sleep(86400000);
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("DeviceUnregister: Interrupted.");
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DeviceUnregister.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("DeviceUnregister: Exiting thread...");
        try {
            s.close();
            rs.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DeviceUnregister.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Boolean isDateRecent(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calPrev = Calendar.getInstance();
        Date datePrev = sdf.parse(date);
        calPrev.setTime(datePrev);
        Calendar calCurr = Calendar.getInstance();
        Date dateCurr = sdf.parse(sdf.format(calCurr.getTime()));
        calCurr.setTime(dateCurr);
        calCurr.add(Calendar.MONTH, -1);
        /*System.out.println("calCurr: " + sdf.format(calCurr.getTime()));
         System.out.println("calPrev: " + sdf.format(calPrev.getTime()));
         System.out.println("calPrev after calCurr: " + calPrev.after(calCurr));/**/
        return calPrev.after(calCurr);
    }

    private Boolean isTimeRecent(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar calPrev = Calendar.getInstance();
        Date datePrev = sdf.parse(time);
        calPrev.setTime(datePrev);
        Calendar calCurr = Calendar.getInstance();
        Date dateCurr = sdf.parse(sdf.format(calCurr.getTime()));
        calCurr.setTime(dateCurr);
        calCurr.add(Calendar.MINUTE, -2);
        /*System.out.println("calCurr: " + sdf.format(calCurr.getTime()));
         System.out.println("calPrev: " + sdf.format(calPrev.getTime()));
         System.out.println("calPrev after calCurr: " + calPrev.after(calCurr));/**/
        return calPrev.after(calCurr);
    }

}
