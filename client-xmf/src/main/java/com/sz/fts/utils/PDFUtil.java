package com.sz.fts.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author 耿怀志
 * @version [版本号, 2017/8/21]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PDFUtil {

    public static Integer pdfToImg(String pdfName,String imgName){
        File file = new File("/home/szfts/tomcat/apache-tomcat-8.0.50/webapps/szfts/resources/file/wxmbImg/" + pdfName+".pdf");
        int out = 0;
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for(int i=0;i<pageCount;i++){
//                BufferedImage image = renderer.renderImageWithDPI(i, 296);
                           BufferedImage image = renderer.renderImage(i, 2.5f);
                ImageIO.write(image, "PNG", new File("/home/szfts/tomcat/apache-tomcat-8.0.50/webapps/szfts/resources/file/wxmbImg/"+imgName+(i+1)+".png"));
                out = i;
                }
            } catch (IOException e) {
              e.printStackTrace();
            }
            return out;
    }




}
