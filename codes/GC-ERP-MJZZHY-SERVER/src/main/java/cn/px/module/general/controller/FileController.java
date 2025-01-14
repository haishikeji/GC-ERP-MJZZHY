package cn.px.module.general.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.px.module.general.bean.ResultEnum;
import cn.px.utils.JsonUtils;
import cn.px.utils.SnowflakeIdWorker;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.px.module.general.bean.Result;
import cn.px.module.general.bean.SysUser;
import cn.px.utils.Rutils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 品讯科技
 */
@Api(tags = "文件上传下载")
@RestController
@RequestMapping("/general/file")
public class FileController {
    @Value("${file.upload.path}")
    private String filePath;
    @Value("${file.upload.url}")
    private String fileUrl;
    @Value("${file.upload.type}")
    private String[] fileTypes;
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    HttpServletResponse response;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Rutils.error(500, "文件为空");
            }
            Subject subject = SecurityUtils.getSubject();
            Object obj = subject.getPrincipals().getPrimaryPrincipal();
            if (obj == null) {
                return Rutils.error(401, "token已过期！请重新登录！");
            }
            SysUser sysUser;
            if (obj instanceof SysUser) {
                sysUser = (SysUser) obj;
            } else {
                sysUser = JsonUtils.parse(JsonUtils.toJson(obj), SysUser.class);
            }
            Map<String, String> map = new HashMap<>(16);
            // 获取文件名
            String fileName = file.getOriginalFilename();
            map.put("name", fileName);
            long fileSize = file.getSize();
            // 获取文件的后缀名
            assert fileName != null;
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!Arrays.asList(fileTypes).contains(fileType.toLowerCase())) {
                return Rutils.error(500, "抱歉!暂不支持该类型文件的上传(" + fileType + ")");
            }
            /**
             两种获取项目 根目录的方法
             String path1 = ClassUtils.getDefaultClassLoader().getResource("").getPath();
             String path2 = ResourceUtils.getURL("classpath:").getPath();
             File upload = new File(path.getAbsolutePath(),"static/images/upload/");
             */
            fileName = String.valueOf(snowflakeIdWorker.nextId());
            fileName = fileName + "." + fileType;

            String relativePath = fileUrl + sysUser.getUserName() + "/" + fileName;
            map.put("url", relativePath);

            String path = filePath + sysUser.getUserName() + "/" + fileName;
            File dest = new File(path);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                // 新建文件夹
                dest.getParentFile().mkdirs();
            }
            // 文件写入
            file.transferTo(dest);

            map.put("rawFileName", fileName);
            map.put("fileName", fileName);
            map.put("fileSize", fileSize + "");
            map.put("fileType", fileType);
            map.put("fileUrl", relativePath);

            return Rutils.success("上传成功", map);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return Rutils.error(500, "上传失败");
    }

    @PostMapping("/batch")
    @ApiOperation(value = "批量上传", notes = "批量上传")
    public Result batchUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + fileUrl;
        MultipartFile file;
        BufferedOutputStream stream;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    String fileName = file.getOriginalFilename();
                    assert fileName != null;
                    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                    if (!Arrays.asList(fileTypes).contains(fileType)) {
                        return Rutils.error(ResultEnum.NOT_SUCCESS.getCode(), "抱歉!暂不支持第" + i + " 个文件类型文件的上传(" + fileType + ")");
                    }
                    byte[] bytes = file.getBytes();
                    // 设置文件路径及名字
                    stream = new BufferedOutputStream(new FileOutputStream(new File(path + fileName)));
                    // 写入
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    return Rutils.error(ResultEnum.SERVER_ERROR.getCode(), "第 " + i + " 个文件上传失败 ==> " + e.getMessage());
                }
            } else {
                return Rutils.error(ResultEnum.NOT_SUCCESS.getCode(), "第 " + i + " 个文件上传失败因为文件为空");
            }
        }
        return Rutils.success();
    }

    @ApiOperation(value = "文件下载", notes = "文件下载")
    @PostMapping("/download")
    public Result downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        if (fileName != null) {
            //设置文件路径
            String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + fileUrl;
            File file = new File(path + fileName);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
                // 设置强制下载不打开
                response.setContentType("application/force-download");
                // 设置文件名
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    return Rutils.success("下载成功");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return Rutils.error(500, "下载失败");
    }
}
