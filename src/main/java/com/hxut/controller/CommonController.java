package com.hxut.controller;


import com.hxut.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


/**
 * description: CommonController
 * date: 2022/6/24 20:37
 * author: MR.孙
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String baseUrl;

    /**
     * @description:  文件上传
     * @param file
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/24 20:55
    */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info("上传的文件=>{}",file);
        //获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename=UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir=new File(baseUrl);
        //判断目录是否存在
        if(!dir.exists()){
            //不存在,则新建一个目录
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(baseUrl+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream inputStream = new FileInputStream(new File(baseUrl + name));
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int len=0;
            //读取图片的输入io流放到bytes数组中
            while((len=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
