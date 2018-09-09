package com.icl.digiboard.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.icl.digiboard.DigiBoardMain;
import com.icl.digiboard.util.RectanglePacker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.soap.Text;

public class DefaultSlideshowScreen implements Screen {

    ///private Stage stage;
    private ArrayList<TextureDetails> imageTextureDetails;
    private OrthographicCamera camera;
    private DigiBoardMain main;
    private SpriteBatch batch;
    int currentSlide = 0;

    float timeToCameraZoomTarget, cameraZoomTarget, cameraZoomOrigin, cameraZoomDuration;
    float timeToCameraTarget, cameraTargetX, cameraTargetY, cameraOriginX, cameraOriginY, cameraDuration;



    private void dbg(String msg){
        Gdx.app.debug("DefaultSlideshowScreen",msg);
    }

    class TextureDetails{
       Texture texture;
       float x, y, midX, midY;
       float zoom;
       public TextureDetails(Texture texture, float x, float y) {
           this.texture = texture;
           this.x = x;
           this.y = y;
           this.midX = x + texture.getWidth()/2;
           this.midY = y + texture.getHeight()/2;
           if(texture.getWidth()>texture.getHeight()) {
               zoom = texture.getWidth()/main.getViewport().getWorldWidth();
           } else {
               zoom = texture.getHeight()/main.getViewport().getWorldHeight();
           }
       }
    }

    public DefaultSlideshowScreen(DigiBoardMain main, ArrayList<Texture> imageTextures) {
        //Image im1 = new Image(imageTextures.get(0));
        this.imageTextureDetails = new ArrayList<TextureDetails>();
        this.main = main;
        batch = new SpriteBatch();//main.getBatch();

        dbg("View port : "+main.getViewport().getWorldWidth()+"~"+main.getViewport().getWorldHeight());
        dbg("Images : "+imageTextures.size());
        camera = new OrthographicCamera(main.getViewport().getWorldWidth(),main.getViewport().getWorldHeight());
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
       // camera = new OrthographicCamera(30, 30 * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        /*for(Texture texture:imageTextures) {
            TextureDetails details = new TextureDetails(texture,0,0);

        }*/
        double size = 0;
        for(Texture texture:imageTextures){
            size+= texture.getWidth()*texture.getHeight();
        }
        double packerSize = Math.sqrt(size)*2;
        dbg("Size of the packer : "+size+"~"+packerSize);

        RectanglePacker<Texture> packer = new RectanglePacker<Texture>((int)packerSize,(int)packerSize,10);
        int idx = 0;
        for(Texture texture:imageTextures) {
            idx++;
            RectanglePacker.Rectangle rectangle = packer.insert(texture.getWidth(),texture.getHeight(),texture);
            if(rectangle!=null) {
                dbg("Texture " + idx + " : " + rectangle.x + "~" + rectangle.y + "~" + rectangle.width + "~" + rectangle.height);
                TextureDetails details = new TextureDetails(texture,rectangle.x,rectangle.y);
                this.imageTextureDetails.add(details);
            }
        }
        Collections.shuffle(imageTextureDetails);
    }

    @Override
    public void show() {
        final Timer.Task switchSlideTask = new Timer.Task() {

            @Override
            public void run() {
                currentSlide++;
                if(currentSlide==imageTextureDetails.size()) {
                    currentSlide = 0;
                }
                switchSlide();
            }
        };
        Timer.schedule(switchSlideTask,10f,10f,-2);
        switchSlide();
    }
    private void switchSlide() {
        dbg("Switching slide : "+currentSlide);
        Timer.Task loadTimer = new Timer.Task(){
            @Override
            public void run() {
                zoomCameraTo(imageTextureDetails.get(currentSlide).zoom,3f);
            }
        };
        Timer.schedule(loadTimer,3f,3f,0);
        zoomCameraTo(2f,3);
        moveCameraTo(imageTextureDetails.get(currentSlide).midX,imageTextureDetails.get(currentSlide).midY,6f);
    }


    private void updateCam(float delta,float Xtaget, float Ytarget) {

        //Creating a vector 3 which represents the target location myplayer)
        Vector3 target = new Vector3(Xtaget,Ytarget,0);
        //Change speed to your need
        final float speed=delta,ispeed=1.0f-speed;
        //The result is roughly: old_position*0.9 + target * 0.1
        Vector3 cameraPosition = camera.position;
        cameraPosition.scl(ispeed);
        target.scl(speed);
        cameraPosition.add(target);
        camera.position.set(cameraPosition);
    }
    private void zoomCameraTo (float newZoom, float duration){
        cameraZoomOrigin = camera.zoom;
        cameraZoomTarget = newZoom;
        timeToCameraZoomTarget = cameraZoomDuration = duration;
    }
    private void moveCameraTo(float x, float y, float duration) {
        cameraOriginX = camera.position.x;
        cameraOriginY = camera.position.y;
        cameraTargetX = x;
        cameraTargetY = y;
        timeToCameraTarget = cameraDuration = duration;
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //updateCam(0.01f,imageTextureDetails.get(2).midX,imageTextureDetails.get(2).midY);
        if (timeToCameraZoomTarget >= 0){
            timeToCameraZoomTarget -= delta;
            float progress = timeToCameraZoomTarget < 0 ? 1 : 1f - timeToCameraZoomTarget / cameraZoomDuration;
            camera.zoom = Interpolation.pow3Out.apply(cameraZoomOrigin, cameraZoomTarget, progress);
        }
        if (timeToCameraTarget >= 0) {
            timeToCameraTarget -= delta;
            float progress = timeToCameraTarget < 0 ? 1: 1f - timeToCameraTarget / cameraDuration;
            camera.position.x = Interpolation.pow3Out.apply(cameraOriginX,cameraTargetX,progress);
            camera.position.y = Interpolation.pow3Out.apply(cameraOriginY,cameraTargetY,progress);
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for(TextureDetails textureDetails:imageTextureDetails) {
            batch.draw(textureDetails.texture,
                    textureDetails.x,
                    textureDetails.y,
                    textureDetails.texture.getWidth(),
                    textureDetails.texture.getHeight()
                    );
        }
        batch.end();
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
}
