package com.icl.digiboard.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.StreamUtils;
import com.icl.digiboard.BoardGlobal;
import com.icl.digiboard.LiveReloadTask;
import com.icl.digiboard.compatibility.GwtIncompatible;
import com.icl.digiboard.pojo.ImageDetailsData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import jdk.nashorn.internal.parser.JSONParser;

@GwtIncompatible
public class ImageDownloader {
    private BoardGlobal global;
    public ImageDownloader(BoardGlobal global){
        this.global = global;
    }
    public Texture getTexture() {
        return null;
    }

    private void dbg(String msg) {
        Gdx.app.debug("ImageDownloader",msg);
    }
    private void dbg(String msg, Throwable t)
    {
        Gdx.app.debug("ImageDownloader",msg,t);
    }

    private int download (byte[] out, String url) {
        InputStream in = null;
        try {
            HttpURLConnection conn = null;
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(true);
            conn.connect();
            in = conn.getInputStream();
            int readBytes = 0;
            while (true) {
                int length = in.read(out, readBytes, out.length - readBytes);
                if (length == -1) break;
                readBytes += length;
            }
            return readBytes;
        } catch (Exception ex) {
            return 0;
        } finally {
            StreamUtils.closeQuietly(in);
        }
    }
    public void getTexture(final String url,final String fileId,final int size, final IResponseHandler response) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                downloadTexture(url,fileId,size,response);
            }
        };
        thread.start();

    }
    private void downloadTexture(final String url,final String fileId,final int size, final IResponseHandler response) {
        dbg("getting file "+url);
        byte[] bytes = new byte[size]; // assuming the content is not bigger than 200kb.
        int numBytes = download(bytes, url);
        //Gdx.app.debug("GDx",new String(bytes));
        dbg("numbytes : "+numBytes);
        Pixmap pixmap = null;
        if (numBytes != 0) {
            // load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
            pixmap = new Pixmap(bytes, 0, numBytes);
        }
        response.addImage(fileId,pixmap);
    }
    public void getImageDetails(String url, final IResponseHandler response) {
        dbg("Inside getImageDetails");
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                                        .url(url)
                                        .method(Net.HttpMethods.GET)
                                        .build();
        Gdx.net.sendHttpRequest(httpRequest,new Net.HttpResponseListener(){

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String respJson = httpResponse.getResultAsString();
                dbg("Response received "+respJson);
                response.downloadImages(respJson);
            }

            @Override
            public void failed(Throwable t) {
                dbg("Response failed",t);
            }

            @Override
            public void cancelled() {
                dbg("Response cancelled");
            }
        });
    }

    public void getServerConfig(String url, final LiveReloadTask response) {
        dbg("Inside getServerConfig");
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .url(url)
                .method(Net.HttpMethods.GET)
                .build();
        Gdx.net.sendHttpRequest(httpRequest,new Net.HttpResponseListener(){

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String respJson = httpResponse.getResultAsString();
                dbg("Response received "+respJson);
                response.processResponse(respJson);
            }

            @Override
            public void failed(Throwable t) {
                dbg("Response failed",t);
            }

            @Override
            public void cancelled() {
                dbg("Response cancelled");
            }
        });
    }
}
