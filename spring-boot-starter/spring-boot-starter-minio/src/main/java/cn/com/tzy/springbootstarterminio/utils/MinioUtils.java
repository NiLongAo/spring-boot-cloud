package cn.com.tzy.springbootstarterminio.utils;

import cn.com.tzy.springbootstarterminio.properties.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * springboot集成MinIO工具类
 */
@Log4j2
public class MinioUtils {

    /**
     * 桶占位符
     */
    private static final String BUCKET_PARAM = "${bucket}";
    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只读
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";


    private final MinioProperties minioProperties;

    /**
     * 定义一个单例的MinioClient对象
     */
    private  MinioClient minioClient;

    /**
     * 默认超时时间
     */
    private static final int DEFAULT_EXPIRY_TIME = 7 * 24 * 3600;

    public MinioUtils(MinioProperties minioProperties){
        this.minioProperties = minioProperties;
        minioClient = MinioClient.builder().endpoint(minioProperties.getEndpoint()).credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
    }


    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            return true;
        }
        return makeBucket(bucketName);
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            setBucketPolicy(minioClient,bucketName,"read-write");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新桶权限策略
     *
     * @param bucket 桶
     * @param policy 权限
     */
    public void setBucketPolicy(MinioClient minioClien,String bucket, String policy) throws Exception {
        switch (policy) {
            case "read-only":
                minioClien.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "write-only":
                minioClien.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(WRITE_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "read-write":
                minioClien.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "none":
            default:
                break;
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     */
    @SneakyThrows
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 列出所有存储桶
     *
     * @return
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }
        return listObjectNames;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    /**
     * 通过文件上传到对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName   File name
     * @return
     */
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, String fileName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.uploadObject(UploadObjectArgs.builder().object(bucketName).object(objectName).filename(fileName).build());
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        return false;

    }

    /**
     * MultipartFile格式文件上传
     *
     * @param bucketName
     * @param multipartFile
     */
    @SneakyThrows
    public void putObject(String bucketName, String objectName,MultipartFile multipartFile) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).contentType(multipartFile.getContentType()).stream(multipartFile.getInputStream(),multipartFile.getInputStream().available(), -1).build());
        }
    }


    /**
     * 通过InputStream上传对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     * @param stream     要上传的文件类型 MimeTypeUtils.IMAGE_JPEG_VALUE
     * @return
     */
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, InputStream stream, String contentType) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            /**
             * 开启公共类功能设置setContentType
             */
            if (StringUtils.isEmpty(contentType)) {
                contentType= MimeTypeUtils.IMAGE_JPEG_VALUE;
            }
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).contentType(contentType).object(objectName).stream(stream,stream.available(), -1).build());
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
            }
        }
        return null;
    }

    /**
     * 以流的形式获取一个文件对象（断点下载）
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).length(length).build());
            }
        }
        return null;
    }

    /**
     * 下载并将文件保存到本地
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName   File name
     * @return
     */
    @SneakyThrows
    public boolean getObject(String bucketName, String objectName, String fileName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                minioClient.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).object(objectName).filename(fileName).build());
                return true;
            }
        }
        return false;
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     */
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }

    /**
     * 删除指定桶的多个文件对象,返回删除错误的对象列表，全部删除成功，返回空列表
     *
     * @param bucketName  存储桶名称
     * @param objectNames 含有要删除的多个object名称的迭代器对象
     * @return
     */
    @SneakyThrows
    public List<String> removeObject(String bucketName, List<String> objectNames) {
        List<String> deleteErrorNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objectNames.stream().map(DeleteObject::new).collect(Collectors.toList())).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                deleteErrorNames.add(error.objectName());
            }
        }
        return deleteErrorNames;
    }

    /**
     * 生成一个给HTTP 请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行下载，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @param method    请求方式
     * @return
     */
    @SneakyThrows
    public String presignedObject(String bucketName, String objectName, Integer expires, Method method) {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                throw new Exception(String.format("expires must be in range of 1 to %s",DEFAULT_EXPIRY_TIME));
            }
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).method(method).expiry(expires).build());
        }
        return url;
    }

    /**
     * 获取对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    @SneakyThrows
    public StatObjectResponse statObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return statObject;
        }
        return null;
    }



    /**
     * web下载
     *
     * @param bucketName   存储桶名称
     * @param fileName     文件名
     * @param originalName 原始文件名
     * @param response     输出对象
     */
    public void downloadFile(String bucketName, String fileName, String originalName, HttpServletResponse response) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            return;
        }
        try {
            InputStream file = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            String filename = new String(fileName.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
            if (!StringUtils.isEmpty(originalName)) {
                filename = originalName;
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = file.read(buffer)) > 0) {
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();
            file.close();
            servletOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return java.util.List<io.minio.messages.Bucket>
     * @Description 获取minio所有的桶
     **/
    @SneakyThrows
    public List<Bucket> getAllBucket() {
        // 获取minio中所以的bucket
        List<Bucket> buckets = minioClient.listBuckets();
        for (Bucket bucket : buckets) {
            log.info("bucket 名称: {} ,创建时间: {}", bucket.name(), DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bucket.creationDate().withZoneSameInstant(ZoneId.of("Asia/Shanghai"))));
        }
        return buckets;
    }


    /**
     * @param inputStream: 输入流
     * @param objectName:  存储桶里的对象名称
     * @return void
     * @Description 将图片上传到minio服务器
     **/
    @SneakyThrows
    public void uploadToMinio(InputStream inputStream,String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            return;
        }
        long start = System.currentTimeMillis();
        try {
            long size = inputStream.available();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .build();
            // 上传到minio
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.error("上传异常：", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("上传文件关闭流异常", e);
                }
            }
        }
        long end = System.currentTimeMillis();
        log.info("上传 {} 文件，总耗时为：{} ms", objectName, end - start);
    }


    /**
     * @param objectName: 存储桶里的对象名称
     * @param fileName:   文件名称
     * @param dir:        文件目录
     * @return void
     * @Description 根据objectName从minio中下载文件到指定的目录
     **/
    @SneakyThrows
    public void downloadFromMinioToFile(String bucketName, String objectName, String fileName, String dir) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            return;
        }
        long start = System.currentTimeMillis();
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .build();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        //使用BufferedInputStream和BufferedOutputStream可以提高IO效率
        try (BufferedInputStream bis = new BufferedInputStream(minioClient.getObject(objectArgs));
             BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(new File(dir, fileName.substring(fileName.lastIndexOf("/") + 1)).toPath()))) {
            int length;//读取内容
            while ((length = bis.read()) != -1) {
                bos.write(length);
                bos.flush();//当读取的缓冲区到了8192字节后刷出到输出文件
            }
        } catch (Exception e) {
            log.error("下载异常", e);
        }
        long end = System.currentTimeMillis();
        log.info("下载 {} 文件，总耗时为：{} ms", objectName, end - start);
    }


    public String upload(String bucketName,String path,String fileName,MultipartFile file){
        if(StringUtils.isEmpty(bucketName)){
            bucketName = minioProperties.getBucketName();
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String suffix = split[split.length - 1];
        String format = String.format("%s/%s.%s",path, fileName, suffix);
        putObject(bucketName,format,file);
        return format;
    }
}
