package com.moshui.mall.controller;

import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UploadController {

    // 文件保存路径为在D盘下的upload文件夹，可以按照自己的习惯来修改
    private final static String FILE_UPLOAD_PATH = "D:\\Workspace\\NewIDEA\\mall-image\\";

    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    //单文件上传
    @PostMapping("/uploadFile")
    @ResponseBody
    public String uploadFile(MultipartFile file){
        if (file.isEmpty()){
            return "上传失败";
        }

        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf("."));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = simpleDateFormat.format(new Date());
        String newFileName = date + new Random().nextInt(100) + suffixName;
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_PATH + newFileName);
            Files.write(path,bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
        return "上传成功，地址为：/upload/" + newFileName;
    }

    //同名（name）多文件上传
    @PostMapping("/uploadFilesBySameName")
    @ResponseBody
    public String uploadFilesBySameName(MultipartFile[] files){
        if (files == null || files.length == 0){
            return "文件上传失败";
        }
        if (files.length > 5){
            return "上传文件数最多为5个";
        }

        String uploadResult = "上传成功，地址为：<br>";
        for (MultipartFile file : files) {
            if (file.isEmpty()){
                continue;
            }

            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf("."));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String date = simpleDateFormat.format(new Date());
            String newFileName = date + new Random().nextInt(100) + suffixName;
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(FILE_UPLOAD_PATH + newFileName);
                Files.write(path,bytes);
                uploadResult += "/upload/" + newFileName + "<br>";
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败";
            }
        }
        return uploadResult;
    }

    //不同名（name）多文件上传
    @PostMapping("/uploadFilesByDifferentName")
    @ResponseBody
    public String uploadFilesByDifferentName(HttpServletRequest request){
        List<MultipartFile> files = new ArrayList<>();
        // 如果不是文件上传请求则不处理
        if (!standardServletMultipartResolver.isMultipart(request)){
            return "请选择文件";
        }

        // 将 HttpServletRequest 对象转换为 MultipartHttpServletRequest 对象，之后读取文件
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartHttpServletRequest.getFileNames();
        int total = 0;
        while (fileNames.hasNext()){
            if (total > 5){
                return "最多上传5个文件";
            }
            total++;
            files.add(multipartHttpServletRequest.getFile(fileNames.next()));
        }
        if (CollectionUtils.isEmpty(files)){
            return "文件为空";
        }

        String uploadResult = "上传成功，地址为：<br>";
        for (MultipartFile file : files) {
            if (file.isEmpty()){
                continue;
            }

            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf("."));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String date = simpleDateFormat.format(new Date());
            String newFileName = date + new Random().nextInt(100) + suffixName;
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(FILE_UPLOAD_PATH + newFileName);
                Files.write(path,bytes);
                uploadResult += "/upload/" + newFileName + "<br>";
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败";
            }
        }
        return uploadResult;
    }

}
