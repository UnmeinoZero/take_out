package com.uzero.reggie.controller;

import com.uzero.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-04-03  10:07:26
 * 处理文件上传下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //定义文件上传保存路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file 文件对象
     * @return 返回结果信息
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();

        //截取文件扩展名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID() + suffix;

        //创建一个目录
        File dir = new File(basePath);
        //指定文件保存目录，不存在时，创建目录
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //将文件保存到指定位置
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("上传失败");
        }
        return R.success(fileName);
    }


    /**
     * 文件下载
     *
     * @param name 文件名
     * @param response 响应
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        log.info("文件下载：{}", name);
        //定义输入输出流
        try (FileInputStream fileInputStream = new FileInputStream(basePath + name);
             ServletOutputStream outputStream = response.getOutputStream()) {

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
