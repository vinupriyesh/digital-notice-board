package com.icl.digiboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

public class BoardGlobal {
    public Orientation getOrientation() {
        return orientation;
    }
    private void dbg(String msg) {
        Gdx.app.debug("BoardGlobal",msg);
    }

    public boolean isReloadScreen() {
        return reloadScreen;
    }

    public void setReloadScreen(boolean reloadScreen) {
        dbg("Reload screen set to "+reloadScreen);
        this.reloadScreen = reloadScreen;
    }

    public enum Orientation{
        LANDSCAPE,
        PORTRAIT
    }
    Object htmlLauncher;
    private Orientation orientation;
    private I18NBundle properties;
    private Date serverTime;
    private boolean reloadScreen;

    public void init() {
        if(getScreenHeight()<=getScreenWidth()) {
            orientation = Orientation.LANDSCAPE;
        } else {
            orientation = Orientation.PORTRAIT;
        }
        //properties.load(Gdx.files.internal("values.properties").reader());
        FileHandle baseFileHandle = Gdx.files.internal("values");
        properties = I18NBundle.createBundle(baseFileHandle, Locale.ENGLISH);
    }
    public String getProperty(String key,String defValue) {
        try {
            return properties.get(key);
        }catch (MissingResourceException|NullPointerException e) {
            return defValue;
        }
    }
    public Object getHtmlLauncher() {
        return htmlLauncher;
    }
    public void setHtmlLauncher(Object htmlLauncher) {
        this.htmlLauncher = htmlLauncher;
    }

    public float getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    public float getScreenWidth() {
        return Gdx.graphics.getWidth();
    }
    public float getAspectRatio(){
        if(orientation == Orientation.LANDSCAPE) {
            return  (float)getScreenWidth() / (float)getScreenHeight();
        } else {
            return  (float) getScreenHeight() / (float) getScreenWidth();
        }
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }
}
