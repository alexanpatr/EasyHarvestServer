package com.www.server;

import static java.lang.Boolean.*;

/*
 * Description:
 * Paths and variables.
 * 
 * Changelog:
 * 150523 - Miscellaneous path changes.
 */
public class Globals {
    /*
     public static String logo = "&pi;pc";
     public static String h1 = "&pi;ing - pong computing";
     public static String p = "Execute code on mobile devices through the web.";
     /**/

    public static String logo = "EasyHarvest";
    public static String h1 = "EasyHarvest";
    public static String p = "Supporting the Deployment and Management of Sensing Applications on Smartphones.";/**/

    public static Boolean DBG = FALSE;
//    public static Boolean DBG = TRUE;

    /*
     * PC values.
     */
    /**/
    public static String db_dir = "C:/EasyHarvest";
    public static String db_server = "jdbc:mysql://localhost:3306/server";
    public static String db_username = "root";
    public static String db_password = "root";
    public static String server_url = "http://localhost:8084/Server";
    public static String console_cmd = "cmd";
    public static String javac_cmd = "C:/Program Files/Java/jdk1.8.0_25/bin/javac";
    public static String lib_url = "C:/Program Files (x86)/Android/android-sdk/platforms/android-23/android.jar";
    public static String dx_cmd = "C:/Program Files (x86)/Android/android-sdk/build-tools/23.0.1/dx";
    public static String zip_cmd = "C:/Program Files/7-Zip/7z";
    public static String zip_args = "a";
    /**/

    /*
     * MAC values.
     */
    /*
     public static String db_dir = "/Users/emkatsom/EasyHarvest";
     public static String db_server = "jdbc:mysql://localhost:3306/server";
     public static String db_username = "root";
     public static String db_password = "root";
     public static String server_url = "http://localhost:8084/Server";
     public static String console_cmd = "sh";
     public static String javac_cmd = "javac";
     public static String lib_url = "/Users/emkatsom/Library/Android/sdk/platforms/android-22/android.jar";
     public static String dx_cmd = "/Users/emkatsom/Library/Android/sdk/build-tools/22.0.1/dx";
     public static String zip_cmd = "zip";
     public static String zip_args = "";
     /**/

    /*
     * HOST values.
     */
    /*
     public static String db_dir = "/var/lib/tomcat6/webapps/db";
     public static String db_server = "jdbc:mysql://localhost:3306/server";
     public static String db_username = "root";
     public static String db_password = "root";
     public static String server_url = "";
     public static String console_cmd = "sh";
     public static String javac_cmd = "javac";
     public static String lib_url = "/opt/android/android.jar";
     public static String dx_cmd = "/opt/android/dx";
     public static String zip_cmd = "zip";
     public static String zip_args = "";
     /**/
}
