package cn.com.tzy.springbootstartervideocore.service.video;

public interface UpLoadVideoService {
    /**
     * 文件上传
     * @param filePath 原文件路径
     * @param path 上传文件路径
     * @param fileName 文件名
     */
    void send(String filePath, String path, String fileName);
}
