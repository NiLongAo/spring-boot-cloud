package cn.com.tzy.springbootstartercloud.utils;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
 
/**
 * @Date: 2019/1/16 下午4:33
 * @Version 1.0
 *
 * 负责将InputStream转换MultipartFile,可以少引一个jar包,本来用的是spring-test-4.3.9中的MockMultipartFile,直接提取出来使用
 */
public class MockMultipartFile implements MultipartFile {
    private final String name;
 
    private String originalFilename;
 
    private String contentType;
 
    private final byte[] content;
 
    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param content the content of the file
     */
    public MockMultipartFile(String name, byte[] content) {
        this(name, "", null, content);
    }
 
    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param contentStream the content of the file as stream
     * @throws IOException if reading from the stream failed
     */
    public MockMultipartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
    }
 
    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType the content type (if known)
     * @param content the content of the file
     */
    public MockMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename : "");
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
    }
 
    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType the content type (if known)
     * @param contentStream the content of the file as stream
     * @throws IOException if reading from the stream failed
     */
    public MockMultipartFile(String name, String originalFilename, String contentType, InputStream contentStream)
            throws IOException {
 
        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }
 
    @Override
    public String getName() {
        return this.name;
    }
 
    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }
 
    @Override
    public String getContentType() {
        return this.contentType;
    }
 
    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }
 
    @Override
    public long getSize() {
        return this.content.length;
    }
 
    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }
 
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }
 
    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }
 
}