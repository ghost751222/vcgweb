package com.consilium.vcg.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    public static List<Path> saveMultipartFiles(String path, MultipartFile[] files) throws IOException {
        List<Path> paths = new ArrayList<>();
        File file = Paths.get(path).toFile();
        if (!file.exists()) file.mkdir();
        for (MultipartFile _file : files) {
            if (!_file.isEmpty()) {
                Path _path = Paths.get(path, _file.getOriginalFilename());
                Files.write(_path, _file.getBytes());
                paths.add(_path);
            }
        }
        return paths;
    }
}
