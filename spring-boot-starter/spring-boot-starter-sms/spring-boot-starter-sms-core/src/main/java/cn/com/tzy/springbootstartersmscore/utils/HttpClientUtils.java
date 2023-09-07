package cn.com.tzy.springbootstartersmscore.utils;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
public class HttpClientUtils {

    public static class HttpResp {
        public int status;
        public String content;

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    final static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static HttpResp uploadFile0(String url, Map<String, File> files, Map<String, String> params, Map<String, String> headers) throws IOException {

        /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder builder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);

        for(Map.Entry<String, File> entry : files.entrySet()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream") , entry.getValue());
            builder.addFormDataPart("file", entry.getKey(), fileBody);
        }

        for(Map.Entry<String, String> entry : params.entrySet()) {
            builder.addFormDataPart(entry.getKey() , entry.getValue());
        }
        MultipartBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers))
                .build();
        Response response = client.newCall(request).execute();

        HttpResp httpResp = new HttpResp();
        httpResp.status = response.code();
        httpResp.content = response.body().string();
        return httpResp;
    }

    public static HttpResp uploadFile(String url, Map<String, File> files, Map<String, String> param, Map<String, String> header) {
        IOException ex = null;

        for(int i = 0; i < 10; i++) {
            try {
                return uploadFile0(url, files, param, header);
            } catch (IOException e) {
                ex = e;
            }
        }
        throw new RuntimeException(ex);
    }

    private static HttpResp post0(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        if(log.isDebugEnabled()) {
            log.debug("from url: {}", url);
        }

        FormBody.Builder builder = new FormBody.Builder();

        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(log.isDebugEnabled()) {
                log.debug("from param: {}={}", entry.getKey(), entry.getValue());
            }
            builder.add(entry.getKey(),entry.getValue());
        }

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .headers(Headers.of(headers))
                .build();
        Response response = client.newCall(request).execute();

        HttpResp httpResp = new HttpResp();
        httpResp.status = response.code();
        httpResp.content = response.body().string();

        if(log.isDebugEnabled()) {
            log.debug(" recv httpResp.status:{}, ", httpResp.status);
            log.debug(" recv httpResp.content:{}, ", httpResp.content);
        }


        return httpResp;
    }

    public static HttpResp post(String url, Map<String, String> param, Map<String, String> header) {
        IOException ex = null;

        for(int i = 0; i < 10; i++) {
            try {
                return post0(url, param, header);
            } catch (IOException e) {
                ex = e;
            }
        }
        throw new RuntimeException(ex);
    }

    private static HttpResp get0(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(builder.length() != 0) {
                builder.append("&");
            }

            builder.append(entry.getKey());
            builder.append("=");
            builder.append(AppUtils.encodeUrl(entry.getValue(), "UTF-8"));
        }

        if(url.indexOf("?") >= 0) {
            url += "?" + builder.toString();
        }

        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(Headers.of(headers))
                .build();
        Response response = client.newCall(request).execute();

        HttpResp httpResp = new HttpResp();
        httpResp.status = response.code();
        httpResp.content = response.body().string();
        return httpResp;
    }

    public static HttpResp get(String url, Map<String, String> param, Map<String, String> header) {
        IOException ex = null;

        for(int i = 0; i < 10; i++) {
            try {
                return get0(url, param, header);
            } catch (IOException e) {
                ex = e;
            }
        }
        throw new RuntimeException(ex);
    }

//    public static HttpResp postJson(String url, String json) throws IOException {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody requestBody = FormBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//        Response response = okHttpClient.newCall(request).execute();
//
//        HttpResp httpResp = new HttpResp();
//        httpResp.status = response.code();
//        httpResp.content = response.body().string();
//        return httpResp;
//    }

    private static final OkHttpClient build() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };

            SSLContext e = SSLContext.getInstance("TLS");

            e.init(null, new TrustManager[]{tm}, null);

            builder.sslSocketFactory(e.getSocketFactory());

            builder.hostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        } catch (Exception e) {
            throw new AssertionError();
        }

        return builder.build();
    }

    public static HttpResp postJson(String url, String json) throws IOException {
        OkHttpClient okHttpClient = build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();

        HttpResp httpResp = new HttpResp();
        httpResp.status = response.code();
        httpResp.content = response.body().string();
        return httpResp;
    }

    public static HttpResp postJson2(String url, Map<String, String> params,  String json) throws IOException {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(builder.length() != 0) {
                builder.append("&");
            }

            builder.append(entry.getKey());
            builder.append("=");
            builder.append(AppUtils.encodeUrl(entry.getValue(), "UTF-8"));
        }

        if(url.indexOf("?") >= 0) {
            url += "&" + builder.toString();
        } else {
            url += "?" + builder.toString();
        }

        OkHttpClient okHttpClient = client;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();

        HttpResp httpResp = new HttpResp();
        httpResp.status = response.code();
        httpResp.content = response.body().string();
        return httpResp;
    }

    public static HttpResp download(String url, File file) {
        IOException ex = null;

        for(int i = 0; i < 10; i++) {
            try {
                return downlaod0(url, file);
            } catch (IOException e) {
                ex = e;
            }
        }
        throw new RuntimeException(ex);
    }

    private static HttpResp downlaod0(String url, File file) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();

        HttpResp httpResp = new HttpResp();
        httpResp.status = response.code();

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            IOUtils.copy(response.body().byteStream(), stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return httpResp;
    }

    public static String getAddrByIp(String ip) {
        String uri = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
        try {
            HttpResp httpResp = get0(uri, new HashMap<String, String>(), new HashMap<String, String>());
            System.out.println(httpResp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        getAddrByIp("122.224.164.50");
    }

}
