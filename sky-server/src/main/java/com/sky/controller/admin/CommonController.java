package com.sky.controller.admin;

import com.sky.constant.ImagePath;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /* //使用阿里云oss进行图片上传保存
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
//        原始文件名
            String originalFilename = file.getOriginalFilename();

//        截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

//        构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

//        文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件删除失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
    */

    //使用本地文件存储进行图片上传保存
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file)
    {
        log.info("文件上传：{}", file);
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        File dir = new File(ImagePath.PATH);
        if (!dir.exists()||!dir.isDirectory()) {
            if(dir.mkdirs()){
                log.info("文件夹创建成功");
            }else{
                log.info("文件夹创建失败");
            }
        }
        try {
//        原始文件名
            String originalFilename = file.getOriginalFilename();

//        截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            log.info("extension:{}", extension);
            if(!(extension.equals(".jpg")|| extension.equals(".jpeg")||extension.equals(".png"))){
                log.info("文件格式不匹配");
                return Result.error("文件格式不匹配");
            }
//        构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            String objectPath = ImagePath.PATH + objectName;
//            FileUtils.writeByteArrayToFile(objectPath, file.getBytes());
            File newFile = new File(objectPath);
            file.transferTo(newFile);
//        文件的请求路径
// 你可以根据实际情况调整返回的文件访问链接
            String fileUrl = "http://localhost:8080/static/" + objectName;
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("文件删除失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
