package com.icl.digiboard;

import com.icl.digiboard.pojo.ImageIdData;
import com.icl.digiboard.pojo.ImageUploadData;
import com.icl.digiboard.pojo.StatusResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageDetailsStore {
    private static final Logger logger = Logger.getLogger(ImageDetailsStore.class.getName());

    private List<ImageUploadData> files;
    private Global global;

    public ImageDetailsStore(Global global) {
        this.global = global;
        try {
            deSerializeFiles();
        } catch (IOException e) {
            logger.log(Level.SEVERE,"File not found",e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE,"Class not found for the file",e);
        }
        if(files == null) {
            files = new ArrayList<>();
        }
        global.setAdminTimestamp(new Date());
        logger.log(Level.INFO,"Initiated ImageDetailStore");
    }
    public List<ImageUploadData> getFiles(String token) {
        if(token!=null && token.equals(global.getCurrentToken())) {
            return files;
        }
        return getAuthorizedList();
    }
    private List<ImageUploadData> getAuthorizedList() {
        List<ImageUploadData> authFiles = new ArrayList<>();
        for(ImageUploadData data:files) {
            if(data.isAuthorized()) {
                authFiles.add(data);
            }
        }
        return authFiles;
    }
    public void addFile(ImageUploadData data) {
        files.add(data);
        try {
            serializeFiles();
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Could not serialize",e);
        }
    }
    public synchronized void serializeFiles() throws IOException {
        logger.log(Level.INFO,"Serializing files: "+files.size());
        String filePath = global.getWorkPath();
        File directory = new File(filePath);
        if(!directory.exists()){
            directory.mkdir();
        }
        FileOutputStream fileOut = new FileOutputStream(filePath+"/files.dat");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(files);
        out.close();
        fileOut.close();
    }
    private synchronized void deSerializeFiles() throws IOException, ClassNotFoundException {
        logger.log(Level.INFO,"De-Serializing files");
        String filePath = global.getWorkPath();
        File directory = new File(filePath);
        if(!directory.exists()){
            directory.mkdir();
        }
        FileInputStream fileIn = new FileInputStream(filePath+"/files.dat");
        if(fileIn==null) {
            logger.log(Level.INFO,"File not available to deserialize");
            return;
        }
        ObjectInputStream in = new ObjectInputStream(fileIn);
        this.files =  (List<ImageUploadData>) in.readObject();
        in.close();
        fileIn.close();
    }

    public boolean delete(ImageIdData id) {
        for(ImageUploadData imageUploadData:files) {
            if (imageUploadData.getFile().equals(id.getFileId())) {
                files.remove(imageUploadData);
                try {
                    serializeFiles();
                } catch (IOException e) {
                    logger.log(Level.SEVERE,"Could not serialize",e);
                }
                return true;
            }
        }
        return false;
    }
    public boolean authorize(ImageIdData id) {
        for(ImageUploadData imageUploadData:files) {
            if(imageUploadData.getFile().equals(id.getFileId())) {
                imageUploadData.setAuthorized(true);
                try {
                    serializeFiles();
                } catch (IOException e) {
                    logger.log(Level.SEVERE,"Could not serialize",e);
                }
                return true;
            }
        }
        return false;
    }
}
