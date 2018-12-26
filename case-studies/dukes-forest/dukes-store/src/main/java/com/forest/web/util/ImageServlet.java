/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.forest.web.util;

import com.forest.ejb.ProductBean;
import com.forest.entity.Product;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Image servlet based on BalusC (@link below) implementation
 * @author balusc
 * @author markito
 * @link http://balusc.blogspot.com/2007/04/imageservlet.html
 */
@WebServlet(urlPatterns = "/image/*")
public class ImageServlet extends HttpServlet {
    
    private static final long serialVersionUID = 6439315738094263474L;

    @EJB
    ProductBean productBean;
    private static final Logger logger = Logger.getLogger(ImageServlet.class.getCanonicalName());
    // Constants 
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    // Properties 
    private final String UPLOAD_DIR = "/upload/img/";
    //private final String UPLOAD_DIR = ResourceBundle.getBundle("bundles.Bundle").getString("UPLOAD_LOCATION");

    // Actions 
    @Override
    public void init() throws ServletException {
        // In a Windows environment with the Applicationserver running on the
        // c: volume, the above path is exactly the same as "c:\images".
        // In UNIX, it is just straightforward "/images".
        // If you have stored files in the WebContent of a WAR, for example in the
        // "/WEB-INF/images" folder, then you can retrieve the absolute path by:
        // this.imagePath = getServletContext().getRealPath("/WEB-INF/images");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get requested image by path info.
        String requestedImage = request.getParameter("id"); //request.getPathInfo();
        

        // Check if file name is actually supplied to the request URI.
        if (requestedImage == null) {
            // Do your thing if the image is not supplied to the request URI.
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.

            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        Product p = productBean.find(Integer.parseInt(requestedImage));

        if ((p == null) || (p.getImgSrc() == null)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
        } else {
            /*
            // Decode the file name (might contain spaces and on) and prepare file object.
            File image = new File(getServletContext().getRealPath(UPLOAD_DIR), URLDecoder.decode(requestedImage, "UTF-8"));
            
            // Check if file actually exists in filesystem.
            if (!image.exists()) {
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            logger.info("Uploaded image already exists");
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
            }
            
            // Get content type by filename.
            String contentType = getServletContext().getMimeType(image.getName());
            
            // Check if file is actually an image (avoid download of other files by hackers!).
            // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
            if (contentType == null || !contentType.startsWith("image")) {
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            logger.info("Uploaded image doesn't look like a real image");
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
            }
             */
            // Init servlet response.
            response.reset();
            response.setBufferSize(DEFAULT_BUFFER_SIZE);
            //response.setContentType("image/jpg");
            response.setHeader("Content-Length", String.valueOf(p.getImgSrc().length));
            response.setHeader("Content-Disposition", "inline; filename=\"" + p.getName() + "\"");

            // Prepare streams.
            ByteArrayInputStream byteInputStream = null;
            BufferedOutputStream output = null;

            try {
                // Open streams.
                byteInputStream = new ByteArrayInputStream(p.getImgSrc());
                output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

              // Write file contents to response.
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int length;
                while ((length = byteInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            } finally {
                // close streams.
                close(output);
                close(byteInputStream);
            }
        }
    }

    // Helpers (can be refactored to public utility class) 
    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, 
                        "Problems during image resource manipulation. {0}", 
                        e.getMessage());
            }
        }
    }
}
