package com.general;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by andrii on 24.01.16.
 */
public class FileSystemServiceImpl implements FileSystemService {
    private static String projectDir = System.getProperty("user.dir");
    private static String fileWithCars = projectDir + "/dataFolder/carsWithFilters.txt";


    @Override
    public Map<String, String> getCarsAndTheirUrls() {
        List<String> stringsWithCars = null;
        StringBuilder currentCarName = new StringBuilder();
        Map<String, String> carsAndTheirUrls = new HashMap<>();

        try {
            stringsWithCars = IOUtils.readLines(new FileInputStream(fileWithCars));
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
}
