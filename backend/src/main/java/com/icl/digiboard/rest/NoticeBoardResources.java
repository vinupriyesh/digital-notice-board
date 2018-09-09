package com.icl.digiboard.rest;

import com.icl.digiboard.Global;
import com.icl.digiboard.ImageDetailsStore;
import com.icl.digiboard.exception.ImageUploadException;
import com.icl.digiboard.pojo.ImageIdData;
import com.icl.digiboard.pojo.ImageUploadData;
import com.icl.digiboard.pojo.StatusResponse;
import com.icl.digiboard.pojo.LoginData;
import com.icl.digiboard.pojo.LoginResponse;

import org.glassfish.jersey.internal.util.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/board")
public class NoticeBoardResources {
    private static final Logger logger = Logger.getLogger(NoticeBoardResources.class.getName());
    private void dbg(String msg) {
        logger.log(Level.INFO, msg);
    }
    private Global global = Global.getInstance();
    private ImageDetailsStore store = global.getStore();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/sync")
    public String doHandShake() {
        Date date =  global.getAdminTimestamp();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/details")
    public List<ImageUploadData> getImageDetails(@Context HttpHeaders httpHeaders) {
        String token =  httpHeaders.getHeaderString("token");
        return store.getFiles(token);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public StatusResponse delete(@Context HttpHeaders httpHeaders, ImageIdData id) {
        String token =  httpHeaders.getHeaderString("token");
        if(token==null || !token.equals(global.getCurrentToken())) {
            return new StatusResponse("FAILURE","Not logged in");
        }
        if(store.delete(id)) {
            global.setAdminTimestamp(new Date());
            return new StatusResponse("SUCCESS", null);
        } else {
            return new StatusResponse("FAILURE","Invalid file");
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/authorize")
    public StatusResponse authorize(@Context HttpHeaders httpHeaders, ImageIdData id) {
        String token =  httpHeaders.getHeaderString("token");
        if(token==null || !token.equals(global.getCurrentToken())) {
            return new StatusResponse("FAILURE","Not logged in");
        }
        if(store.authorize(id)) {
            global.setAdminTimestamp(new Date());
            return new StatusResponse("SUCCESS", null);
        } else {
            return new StatusResponse("FAILURE","Invalid file");
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public LoginResponse doLogin(LoginData data) {
        if(data.getUsername()==null || data.getPassword() == null) {
            return new LoginResponse("FAILURE",null);
        }
        if(!data.getUsername().equals(global.getUsername())){
            return new LoginResponse("FAILURE",null);
        }
        if(!data.getPassword().equals(global.getPassword())){
            return new LoginResponse("FAILURE",null);
        }
        String token = UUID.randomUUID().toString();
        global.setCurrentToken(token);
        return new LoginResponse("SUCCESS",token);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/upload")
    public StatusResponse upload(ImageUploadData data) {
        dbg("Received in upload - "+data.getName());
        if(data.getName() == null || "".equals(data.getName())) {
            return new StatusResponse("FAILURE","Name is empty");
        }
        if(data.getFile()==null || "".equals(data.getFile())) {
            return new StatusResponse("FAILURE","File is empty");
        }
        String fileId = null;
        try {
            fileId = saveImageToFile(data);
        } catch(ImageUploadException e) {
            logger.log(Level.SEVERE,"Image Upload Exception");
            return new StatusResponse("FAILURE",e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Failed to save the image",e);
            return new StatusResponse("FAILURE","Server Error");
        }
        data.setFile(fileId);
        data.setAuthorized(false);
        store.addFile(data);
        return new StatusResponse("SUCCESS",fileId);
    }

    private String saveImageToFile(ImageUploadData imageUploadData) throws ImageUploadException {
        String base64File = imageUploadData.getFile();
        String[] base64FileSplit = base64File.split(",");
        if(base64FileSplit.length != 2){
            throw new ImageUploadException("Image file corrupt");
        }
        String metadata = base64FileSplit[0];
        String fileData = base64FileSplit[1];
        if(!metadata.substring(5,10).equals("image")) {
            throw new ImageUploadException("Unrecognized image file - "+metadata.substring(5,10));
        }
        imageUploadData.setExtension(metadata.split(";")[0].split("/")[1]);
        String fileId = UUID.randomUUID().toString();
        byte[] data = Base64.decode(fileData.getBytes());
        imageUploadData.setSize(data.length);
        String filePath = global.getWorkPath()+"/images/";
        File directory = new File(filePath);
        if(!directory.exists()){
            directory.mkdir();
        }
        logger.log(Level.INFO,"Creating file - "+filePath+fileId+"."+imageUploadData.getExtension());
        try (OutputStream stream = new FileOutputStream(filePath+fileId+"."+imageUploadData.getExtension())) {
            stream.write(data);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,"Image File not found",e);
            throw new RuntimeException("Server file not found");

        } catch (IOException e) {
            throw new RuntimeException("Server file corrupt");
        }
        return fileId;
    }
}
