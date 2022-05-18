package ch.uzh.ifi.sopra22.service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    public void save(MultipartFile file, String name) {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }
            //Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
            System.out.println(file.getOriginalFilename());
            Files.copy(file.getInputStream(), root.resolve(name));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadPath)
                    .resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public String createNameWithTimestampAndID(String originalFilename,Long id) {
        //String randomString = RandomStringUtils.random(20,true,true);
        String endtype = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String eventIdString = id.toString();
        String DateString= new java.util.Date().toString();
        String DateStringSpaceless = DateString.replaceAll("\\s", "");
        return DateStringSpaceless + "_" + eventIdString + "." + endtype;
    }
}
