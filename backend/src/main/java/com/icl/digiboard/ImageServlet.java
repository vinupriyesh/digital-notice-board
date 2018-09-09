package com.icl.digiboard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ImageServlet.class.getName());

    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    private String filePath = "D:\\Work\\ICL noticeboard\\workarea";

    public ImageServlet(){
        super();
        filePath = Global.getInstance().getWorkPath() +"/images";
    }
    /*public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.log(Level.INFO,"Inside do get for "+req.getPathInfo());
        // Get the absolute path of the image
        ServletContext sc = getServletContext();
        String filename = req.getPathInfo();
        if (filename == null) {
            logger.log(Level.SEVERE,"File name is null");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get the MIME type of the image
        String mimeType = sc.getMimeType(filename);
        if (mimeType == null) {
            logger.log(Level.SEVERE,"Could not get the mime type - "+filename);
            //resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            //return;
            mimeType = "image/pipeg";
        }

        // Set content type
        resp.setContentType(mimeType);

        // Set content size
        File file = new File(filename);
        resp.setContentLength((int)file.length());

        // Open the file and output streams
        FileInputStream in = new FileInputStream(file);
        OutputStream out = resp.getOutputStream();

        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        in.close();
        out.close();
    }*/
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.log(Level.INFO,"Inside image server for "+request.getPathInfo());
        String requestedFile = request.getPathInfo();
        if (requestedFile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(filePath, URLDecoder.decode(requestedFile, "UTF-8"));

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        String contentType = getServletContext().getMimeType(file.getName());

        logger.log(Level.INFO,"contentType 1 : "+contentType);
        if (contentType == null) {
            //contentType = "application/octet-stream";
            logger.log(Level.INFO,"Cannot set content type");
            contentType = "image/pipeg";
        }
        logger.log(Level.INFO,"contentType 2: "+contentType);

        // Init servlet response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        //response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            close(output);
            close(input);
        }
    }

    // Helpers (can be refactored to public utility class) ----------------------------------------

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE,"Exception while closing the resource",e);
            }
        }
    }

}
