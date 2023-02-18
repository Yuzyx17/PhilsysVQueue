package com.grp_one.controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map.Entry;

import com.grp_one.SqlConnector;
import com.grp_one.controllers.Bot.ChatContextProvider;

public class UserApplicationHandler {

    private static final SqlConnector dbConn = new SqlConnector();
    private static HashMap<String, Object> userInfo = null;

    private static int sessionUID;
    private static Date dateOfTransaction;

    public static void submitInfo(HashMap<String, Object> submittedInfo) {
        userInfo = submittedInfo;
    }

    public static void setSessionUID(int UID) {
        sessionUID = UID;
    }

    public static void clearInfo() {
        userInfo = null;
    }

    public static void uploadInfo() {
        if (userInfo == null)
            System.out.println("No information");
        else {
            dateOfTransaction = new Date(System.currentTimeMillis());
            for (Entry<String, Object> entry : userInfo.entrySet()) {
                System.out.println(entry.getKey() + " " + userInfo.get(entry.getKey()));
            }
            try {
                Connection conn = dbConn.dbConn();
                String locationData = "INSERT INTO admin.user_address VALUES(?,?,?,?,?,?,?,?)";
                String personalData = "INSERT INTO admin.user_personal_info VALUES(?,?,?,?,?,?,?)";
                String applicationData = "INSERT INTO admin.application_status VALUES(?,?,?,?)";
                PreparedStatement stmt = conn.prepareStatement(applicationData);
                stmt.setInt(1, sessionUID);
                stmt.setDate(2, dateOfTransaction);
                stmt.setString(3, "Process");
                stmt.setString(4,
                        (new StringBuilder(String.valueOf(sessionUID * System.currentTimeMillis())).reverse()
                                .toString()).substring(0, 9) + String.valueOf(dateOfTransaction));
                stmt.executeUpdate();
                stmt = conn.prepareStatement(personalData);
                stmt.setInt(1, sessionUID);
                stmt.setString(2, (String) userInfo.get(ChatContextProvider.LNAME));
                Date date = Date.valueOf((LocalDate) userInfo.get(ChatContextProvider.BDAY));
                stmt.setDate(3, date);
                stmt.setString(4, (userInfo.get(ChatContextProvider.SEX)).toString());
                stmt.setString(5, (String) userInfo.get(ChatContextProvider.BTYPE));
                stmt.setString(6, (String) userInfo.get(ChatContextProvider.FILoALIEN));
                stmt.setString(7, (String) userInfo.get(ChatContextProvider.MARITAL));
                stmt.executeUpdate();
                stmt = conn.prepareStatement(locationData);
                stmt.setInt(1, sessionUID);
                stmt.setString(2, (String) userInfo.get(ChatContextProvider.ROOM));
                stmt.setString(3, (String) userInfo.get(ChatContextProvider.HOUSE));
                stmt.setString(4, (String) userInfo.get(ChatContextProvider.STRT));
                stmt.setString(5, (String) userInfo.get(ChatContextProvider.SUBDIV));
                stmt.setString(6, (String) userInfo.get(ChatContextProvider.CITY));
                stmt.setString(7, (String) userInfo.get(ChatContextProvider.PROVINCE));
                stmt.setString(8, (String) userInfo.get(ChatContextProvider.COUNTRY));
                stmt.executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}