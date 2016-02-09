package com.services;

import com.dto.Advert;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrii on 24.01.16.
 */
public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public Map<String, String> getCarsAndTheirUrls() {
        List<String> stringsWithCars = null;
        StringBuilder currentCarName = new StringBuilder();
        Map<String, String> carsAndTheirUrls = new HashMap<>();

        try {
            stringsWithCars = IOUtils.readLines(new FileInputStream(ProjectConfig.fileWithCars));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(stringsWithCars != null){
            stringsWithCars.forEach(str->{
                if(str.startsWith("::")){
                    currentCarName.setLength(0);
                    currentCarName.append(str.replaceAll("::", ""));
                }

                if(str.startsWith("http")){
                    carsAndTheirUrls.put(currentCarName.toString(), str);
                }
            });
        }
        return carsAndTheirUrls;
    }

    public static void saveImage(Advert advertItem, String imageUrl) {
        //String destinationFile =  folderWithImages + imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        String destinationFile =  ProjectConfig.folderWithImages + advertItem.autoriaId;

        try {
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destinationFile);

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
            }

            is.close();
            os.close();
            //System.out.println("File saved : " + destinationFile);
        } catch (Exception e) {
            System.err.println("Cant save image " + destinationFile + "; " + e.getMessage());
        }

    }
}

