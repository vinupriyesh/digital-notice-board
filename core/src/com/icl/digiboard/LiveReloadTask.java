package com.icl.digiboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.icl.digiboard.network.ImageDownloader;
import com.icl.digiboard.screen.LoadingScreen;
import com.icl.digiboard.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LiveReloadTask extends Timer.Task {
    private BoardGlobal global;
    private DigiBoardMain main;
    private ImageDownloader downloader;

    private void dbg(String msg) {
        Gdx.app.debug("LiveReloadTask",msg);
    }
    public LiveReloadTask(DigiBoardMain main) {
        this.main = main;
        this.global = main.getGlobal();
        downloader = new ImageDownloader(global);
    }

    @Override
    public void run() {
        dbg("Checking for changes");
        downloader.getServerConfig(buildUrl(),this);
    }
    private String buildUrl(){
        String server = global.getProperty("server","localhost");
        String port = global.getProperty("port","8080");
        String context = global.getProperty("context_path","backend-1.0");
        return "http://"+server+":"+port+"/"+context+"/rest/board/sync";
    }
    public void processResponse(String respJson) {
        dbg("Processing response ");

        try {
            Date serverDate = DateUtil.parse(respJson);//format.parse(respJson);
            Date clientDate = global.getServerTime();
            if(clientDate==null) {
                global.setServerTime(serverDate);
                global.setReloadScreen(true);
                return;
            }
            if(serverDate.after(clientDate)) {
                global.setServerTime(serverDate);
                global.setReloadScreen(true);
                return;
            }
        } catch (ParseException e) {
            dbg("received data is out of format - "+respJson);
        }
    }
}
