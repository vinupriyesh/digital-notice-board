package com.icl.digiboard.network;

import com.badlogic.gdx.graphics.Pixmap;

public interface IResponseHandler {
    void addImage(String fileId,Pixmap pixmap);
    void downloadImages(String json);
}
