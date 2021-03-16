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

/**
 * fileuploader
 *
 * uploadPath以及tmpPath均不需要在最后加/
 */
public class FileuploadUtils {

    private HttpServletRequest request;

    private String uploadPath = "F:/treedata_repo";

    private String tmpPath = "F:/treedata_repo";

    private String uuidPath;

    public FileuploadUtils(HttpServletRequest request, String uploadPath, String tmpPath) {
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

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 检测是否为一个文件上传的请求
     * @return bool
     */
    private Boolean hasFile(){
        if(!ServletFileUpload.isMultipartContent(request)){
            return false;
        }
        return true;
    }

    /**
     *
     * @param uploadFile
     * @param tmpFile
     */
    private void initPath(File uploadFile, File tmpFile){

        System.out.println("上传根目录为：" + uploadPath);

        //不存在上传根目录则创建
        if(!uploadFile.exists()){
            uploadFile.mkdir();
            System.out.println(uploadPath + "创建成功");
        }

        System.out.println("临时文件目录为：" + tmpPath);

        //不存在临时文件目录为则创建
        if(!tmpFile.exists()){
            tmpFile.mkdir();
            System.out.println(tmpPath + "创建成功");
        }

    }

    /**
     *
     * @param session
     * @return
     */
    public String upload(HttpSession session){

        //判断是否为文件上传请求
        if(!hasFile()){
            return "null";
        }

        File uploadFile = new File(uploadPath);
        File tmpFile = new File(tmpPath);
        //创建文件保存目录
        initPath(uploadFile, tmpFile);

        //DiskFileItemFactory
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置缓冲区: 100MB，以及缓冲区路径
        factory.setSizeThreshold(1024 * 1024 * 100);
        factory.setRepository(tmpFile);

        //ServletFileUpload
        ServletFileUpload upload = new ServletFileUpload(factory);
        //设置监听
        upload.setProgressListener(new ProgressListener(){
            @Override
            public void update(long ready, long all, int i) {
                float process = (float)ready / all;
                //session.removeAttribute("process");
                session.setAttribute("process", "" + process);
                //System.out.println(process);
            }
        });
        //设置编码
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件最大值: 100MB
        upload.setFileSizeMax(1024*1024*100);
        //设置上传上限: 10GB
        upload.setSizeMax(1024*1024*1024*10);

        //设置本次上传uuid
        uuidPath = UUID.randomUUID().toString();
        //图片个数
        int count = 0;

        try{
            //解析request
            List<FileItem> fileItems = upload.parseRequest(request);
            System.out.println(fileItems.size());
            for(FileItem item : fileItems){
                //判断FileItem是否包含表单
                if(item.isFormField()){
                    //此处为普通表单
                    String value = item.getString("UTF-8");
                    System.out.println("上传人: " + value);
                }
                else{
                    //此处为带文件表单
                    //获取文件名
                    String uploadFileName = item.getName();
                    if(uploadFileName.trim().equals("") || uploadFileName == null){
                        continue;
                    }
                    String fileName = uploadFileName.substring(
                            uploadFileName.lastIndexOf("/") + 1
                    );
                    String fileExtName = uploadFileName.substring(
                            uploadFileName.lastIndexOf('.') + 1
                    );
                    if(!fileExtName.equals("JPG") && !fileExtName.equals("PNG") && !fileExtName.equals("png") && !fileExtName.equals("jpg")){
                        //如果不为图片文件则掠过: png、jpg、PNG、JPG
                        continue;
                    }

                    //通过图片检查，则图片数量+1
                    ++count;
                    String realPath = uploadPath + "/" + uuidPath;
                    File realPathFile = new File(realPath);
                    if(!realPathFile.exists()){
                        realPathFile.mkdir();
                    }

                    //获得输出流
                    InputStream inputStream = item.getInputStream();
                    //获得输出流
                    FileOutputStream fos = new FileOutputStream(realPath + "/" + fileName);
                    byte[] buffer = new byte[1024*1024*100];

                    int len = 0;
                    while((len=inputStream.read(buffer))>0){
                        fos.write(buffer,0, len);
                    }

                    fos.close();
                    inputStream.close();
                    item.delete();

                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(count == 0){
            return "null";
        }

        return uuidPath;
    }


}
