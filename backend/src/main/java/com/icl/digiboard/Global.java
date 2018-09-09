package com.icl.digiboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Global {
    private static final Logger logger = Logger.getLogger(Global.class.getName());
    private static Global global = new Global();
    private String workPath;
    private ImageDetailsStore store;
    private String username, password;
    private String currentToken;
    private Date adminTimestamp;

    private Global() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/digiboard.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Failed to load property file",e);
        }
        workPath = properties.getProperty("work_area","/digiboard");
        password = properties.getProperty("password","admin");
        username = properties.getProperty("username","admin");

        store = new ImageDetailsStore(this);
        logger.log(Level.INFO,"Global initiated - "+workPath);
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public void setCurrentToken(String token) {
        this.currentToken = token;
    }
    public String getCurrentToken() {
        return currentToken;
    }

    public static Global getInstance() {
        return global;
    }

    public String getWorkPath() {
        return workPath;
    }

    public ImageDetailsStore getStore() {
        return store;
    }

    public Date getAdminTimestamp() {
        return adminTimestamp;
    }

    public void setAdminTimestamp(Date adminTimestamp) {
        this.adminTimestamp = adminTimestamp;
    }
}
