package com.nebulord.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;
import java.util.UUID;

public class FileuploadUtils {
    private Model model;

    private HttpServletRequest request;

    private String uploadPath = "F:/treedata_repo/";

    private String tmpPath = "F:/treedata_repo/";

    public FileuploadUtils(Model model, HttpServletRequest request, String uploadPath, String tmpPath) {
        this.model = model;
        this.request = request;
        this.uploadPath = uploadPath;
        this.tmpPath = tmpPath;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    private Boolean hasFile(){
        //如果没有文件
        if(!ServletFileUpload.isMultipartContent(request)){
            System.out.println("不带文件");
            model.addAttribute("msg", "无法上传图片,原因：无文件");
            return false;
        }
        return true;
    }

    public String upload(){
        //如果没有文件
        if(!hasFile()){
            return "null";
        }

        /**
         * 这里getRealPath总是转至tomcat Temp文件夹，不是理想的保存位置
         * 后期需要指定上传的位置
         */
        System.out.println("上传的根目录为: " + uploadPath);

        //判断uploadpath是否存在，不存在则创建
        File uploadFile = new File(uploadPath);
        if(!uploadFile.exists()){
            uploadFile.mkdir();
            System.out.println("WARNNING: 无" + uploadPath + "路径，但是已经创建成功");
        }

        //创建临时文件的保存路径
        System.out.println("临时文件目录为: " + tmpPath);

        //判断tmpPath是否存在，不存在则创建
        File tmpFile = new File(tmpPath);
        if(!tmpFile.exists()){
            tmpFile.mkdir();
            System.out.println("WARNNING: 无" + tmpFile + "路径，但是已经创建成功");
        }

        //创建DiskFileItemFactory,限制上传大小，设置上传路径
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //缓冲区100MB
        factory.setSizeThreshold(1024*1024*100);
        factory.setRepository(tmpFile);

        //ServletFileUpload
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setProgressListener(new ProgressListener(){
            @Override
            public void update(long ready, long all, int i){
                float process = (float)ready / all;
                request.getSession().removeAttribute("process");
                request.getSession().setAttribute("process", "" + process);
                System.out.println(ready + " / " + all);
            }
        });

        //设置编码
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件的最大值
        upload.setFileSizeMax(1024*1024*100);
        //设置总共能够上传的文件大小
        upload.setSizeMax(1024*1024*1024*10);
        String uuidPath = "";
        try{
            List<FileItem> fileItems = upload.parseRequest(request);
            uuidPath = UUID.randomUUID().toString();
            System.out.println("此次上传路径为：" + uploadPath + "/" + uuidPath);
            System.out.println(fileItems.size());
            for(FileItem fileItem : fileItems) {
                //判断是普通表单还是带文件表单
                if (fileItem.isFormField()) {

                    //普通表单
                    String name = fileItem.getFieldName();
                    String value = fileItem.getString("UTF-8");
                    System.out.println(name + ":" + value);
                }
                else {
                    //带文件表单
                    String uploadFileName = fileItem.getName();
                    System.out.println("全文件名: " + uploadFileName);
                    if(uploadFileName.trim().equals("")||uploadFileName==null){
                        continue;
                    }
                    String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);
                    System.out.println("文件名: " + fileName);
                    //获得上传文件的后缀
                    String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);

                    //设置存放地址
                    String realPath = uploadPath + "/" + uuidPath;

                    //不存在该文件夹则创建
                    File realPathFile = new File(realPath);
                    if(!realPathFile.exists()){
                        realPathFile.mkdir();
                        System.out.println("已创建路径: " + realPath);
                    }
                    System.out.println("图片保存在: " + realPath);
                    InputStream inputStream = fileItem.getInputStream();
                    //创建文件输出流
                    FileOutputStream fos = new FileOutputStream(realPath + "/" + fileName);

                    byte[] buffer = new byte[1024*1024*100];

                    int len = 0;
                    while((len=inputStream.read(buffer))>0){
                        fos.write(buffer,0, len);
                    }

                    fos.close();
                    inputStream.close();
                    fileItem.delete();
                }
            }
        }catch(FileUploadException | UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uuidPath;
    }

}
