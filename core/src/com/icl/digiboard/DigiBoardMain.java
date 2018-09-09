package com.icl.digiboard;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.icl.digiboard.network.IResponseHandler;
import com.icl.digiboard.network.ImageDownloader;
import com.icl.digiboard.screen.DefaultSlideshowScreen;
import com.icl.digiboard.screen.LoadingScreen;

import java.awt.Image;
import java.util.ArrayList;

import javax.xml.soap.Text;

public class DigiBoardMain extends Game  {
    private static final float SCREEN_BASE = 1280f;
	SpriteBatch batch;
    private Viewport viewport;

    public BoardGlobal getGlobal() {
        return global;
    }

    private BoardGlobal global;


    private void dbg(String msg) {
        Gdx.app.debug("DigiBoardMain",msg);
    }
	public DigiBoardMain() {
	    this.global = new BoardGlobal();
    }
	public DigiBoardMain(Object appParam) {
	    this.global = new BoardGlobal();
        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            global.setHtmlLauncher(appParam);
        }
    }
	@Override
	public void create () {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        global.init();
        setViewport();
		batch = new SpriteBatch();
		LiveReloadTask reloadTask = new LiveReloadTask(this);
        Timer.schedule(reloadTask,0f,10f,-2);
    }

    public void checkForReload() {
        if(!global.isReloadScreen())
            return;
        global.setReloadScreen(false);
        LoadingScreen screen = new LoadingScreen(this);
        dbg("Calling Load screen");
        this.setScreen(screen);
    }

	@Override
	public void render () {
        super.render();
        checkForReload();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}


    public SpriteBatch getBatch() {
        return batch;
    }

    public Viewport getViewport() {
        return viewport;
    }
    private void setViewport() {
        float w,h;
        if(global.getOrientation() == BoardGlobal.Orientation.LANDSCAPE) {
            w = SCREEN_BASE;
            h = SCREEN_BASE/global.getAspectRatio();
        } else {
            h = SCREEN_BASE;
            w = SCREEN_BASE/global.getAspectRatio();
        }
        viewport = new FitViewport(w,h);
        dbg("Viewport set to "+w+" x "+h);
    }
}