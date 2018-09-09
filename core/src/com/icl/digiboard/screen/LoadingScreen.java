package com.icl.digiboard.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.icl.digiboard.DigiBoardMain;
import com.icl.digiboard.network.IResponseHandler;
import com.icl.digiboard.network.ImageDownloader;
import com.icl.digiboard.pojo.ImageDetailsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class LoadingScreen implements Screen, IResponseHandler {
   // private ArrayList<Texture> imageTextures;
    private HashMap<String,Pixmap> imageTexturesMap;
    private DigiBoardMain main;
    private Stage stage;
    private SpriteBatch batch;
    private Label nowLoading;
    private int pendingDownloads;
    private int completedDownloads;


    private void dbg(String msg) {
        Gdx.app.debug("LoadingScreen",msg);
    }
    public LoadingScreen(DigiBoardMain main){
        this.main = main;
        //this.imageTextures = new ArrayList<Texture>();
        this.imageTexturesMap = new HashMap<String, Pixmap>();
        Viewport view = main.getViewport();
        stage = new Stage(view);
        view.apply();
        batch = main.getBatch();

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = new BitmapFont(Gdx.files.internal("fnt/luci.fnt"),Gdx.files.internal("fnt/luci.png"),false,true);
        //font.getData().setScale(5);
        font.setColor(Color.BLACK);
        style.font = font;
        nowLoading = new Label("Loading...",style);
        nowLoading.setX(stage.getViewport().getWorldWidth()/2-nowLoading.getWidth()/2);
        nowLoading.setY(stage.getViewport().getWorldHeight()/2-nowLoading.getHeight()/2);
    }


    @Override
    public void show() {
        stage.addActor(nowLoading);
        nowLoading.addAction(sequence(fadeOut(0f),fadeIn(1)));
        final LoadingScreen screen = this;
        Timer.Task loadTimer = new Timer.Task() {
            @Override
            public void run() {
                dbg("Executing the timer");
                String server = main.getGlobal().getProperty("server","localhost");
                String port = main.getGlobal().getProperty("port","8080");
                String context = main.getGlobal().getProperty("context_path","backend-1.0");
                ImageDownloader downloader = new ImageDownloader(main.getGlobal());
                downloader.getImageDetails("http://"+server+":"+port+"/"+context+"/rest/board/details",screen);
            }
        };
        Timer.schedule(loadTimer, 1f, 1f,0);
    }

    @Override
    public void downloadImages(String jsonString) {
        JsonReader json = new JsonReader();
        JsonValue base = json.parse(jsonString);
        dbg("Size of the json "+base.size);
        for(int i = 0;i<base.size;i++) {
            Json jsonParser = new Json();
            ImageDetailsData data = jsonParser.fromJson(ImageDetailsData.class,base.get(i).toString());
            dbg("Received Image : "+data.getName()+"~"+data.getFile());
            if(imageTexturesMap.get(data.getFile())==null) {
                ImageDownloader downloader = new ImageDownloader(main.getGlobal());
                String server = main.getGlobal().getProperty("server","localhost");
                String port = main.getGlobal().getProperty("port","8080");
                String context = main.getGlobal().getProperty("context_path","backend-1.0");
                pendingDownloads+=1;
                downloader.getTexture("http://"+server+":"+port+"/"+context+"/images/"+data.getFile()+"."+data.getExtension(),data.getFile(),data.getSize(),this);
            }
        }
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if(pendingDownloads!=0 && pendingDownloads == completedDownloads ) {
            ArrayList<Texture> images = new ArrayList<Texture>();
            for(String s:imageTexturesMap.keySet()) {
                images.add(new Texture(imageTexturesMap.get(s)));
            }
            DefaultSlideshowScreen screen = new DefaultSlideshowScreen(main,images);
            main.setScreen(screen);
        }
        main.checkForReload();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
    private synchronized void incrementDownloads() {
        completedDownloads++;
    }
    public void addImage(String fileId, Pixmap pixmap) {
        incrementDownloads();
        dbg("Completed download - "+completedDownloads+" of "+pendingDownloads);
        if(fileId!=null && pixmap != null) {
            imageTexturesMap.put(fileId, pixmap);
        }
    }
}
