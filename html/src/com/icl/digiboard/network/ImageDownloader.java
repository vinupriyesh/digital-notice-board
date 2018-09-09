package com.icl.digiboard.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;


import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.icl.digiboard.BoardGlobal;
import com.icl.digiboard.LiveReloadTask;
import com.icl.digiboard.client.HtmlLauncher;

@SuppressWarnings("unused")
public class ImageDownloader {
    private HtmlLauncher launcher;
    private void dbg(String msg) {
        Gdx.app.debug("ImageDownloaderGwt",msg);
    }
    private void dbg(String msg, Throwable t)
    {
        Gdx.app.debug("ImageDownloaderGwt",msg,t);
    }
    public ImageDownloader(BoardGlobal global) {
        this.launcher = (HtmlLauncher) global.getHtmlLauncher();
    }

    public void getTexture(final String url,final String fileId,final int size, final IResponseHandler response) {
        dbg("Downloading: "+url);
        final RootPanel root = RootPanel.get("embed-html");
        final Image img = new Image(url);
        img.getElement().setAttribute("crossOrigin", "anonymous");
        img.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                dbg("onLoad fired");
                launcher.getPreloader().images.put(url, ImageElement.as(img.getElement()));
                response.addImage(fileId,new Pixmap(Gdx.files.internal(url)));
                root.remove(img);
            }
        });
        root.add(img);
    }

    public void getImageDetails(String url, final IResponseHandler response) {
        try{
            downloadImageDetails(url,response);
        } catch (RequestException e) {
            dbg("Failed in getImageDetails",e);
        }
    }
    private void downloadImageDetails(String url, final IResponseHandler responseHandler) throws RequestException {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
        //rb.setHeader("Content-Type", "application/xml");
        rb.sendRequest(null,
                new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        dbg("ResponseReceived : "+response.getText());
                        responseHandler.downloadImages(response.getText());
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        dbg("Response error : ",exception);

                    }
                }
        );
    }
    public void getServerConfig(String url, final LiveReloadTask response) {
        try {
            downloadServerConfig(url,response);
        } catch (RequestException e) {
            dbg("Failed in getServerConfig",e);
        }
    }
    private void downloadServerConfig(String url, final LiveReloadTask responseHandler) throws RequestException {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
        //rb.setHeader("Content-Type", "application/xml");
        rb.sendRequest(null,
                new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        dbg("ResponseReceived : "+response.getText());
                        responseHandler.processResponse(response.getText());
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        dbg("Response error : ",exception);

                    }
                }
        );
    }
}
