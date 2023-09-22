package cn.com.tzy.springbootvideo.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;

public class FileSystemResourceMultipartFile implements MultipartFile {

    private final File file;

    public FileSystemResourceMultipartFile(String path) {
        this.file = new File(path);
    }

    public FileSystemResourceMultipartFile(File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        return new MimetypesFileTypeMap().getContentType(file);
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(File destination) throws IOException, IllegalStateException {
        try (InputStream in = new FileInputStream(this.file);
             OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
