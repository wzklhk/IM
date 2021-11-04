package net.zpavelocity.springboot.test.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
public class FileUploadController {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss/");

    @PostMapping("/upload")
    public String upload(MultipartFile uploadFile, HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("/uploadFile/");
        System.out.println("path: " + path);

        String format = sdf.format(new Date());
        File folder = new File(path + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        String oldName = uploadFile.getOriginalFilename();
        System.out.println("oldName: " + oldName);

        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."), oldName.length()); // UUID.png
        System.out.println("newName: " + newName);


        try {
            uploadFile.transferTo(new File(folder, newName));
            String filePath = request.getScheme() + "://"
                    + request.getServerName() + ":"
                    + request.getServerPort()
                    + "/uploadFile/" + format + newName;
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";
    }
}
