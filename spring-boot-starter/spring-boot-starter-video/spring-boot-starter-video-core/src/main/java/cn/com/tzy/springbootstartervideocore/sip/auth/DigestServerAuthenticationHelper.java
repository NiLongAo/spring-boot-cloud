package cn.com.tzy.springbootstartervideocore.sip.auth;

import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import gov.nist.core.InternalErrorHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Random;


/**
 * 信令认证处理
 */
@Log4j2
public class DigestServerAuthenticationHelper  {


    private MessageDigest messageDigest;

    /**
     * Default constructor.
     * @throws NoSuchAlgorithmException
     */
    public DigestServerAuthenticationHelper() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(VideoConstant.DEFAULT_ALGORITHM);
    }

    public static String toHexString(byte b[]) {
        int pos = 0;
        char[] c = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            c[pos++] = VideoConstant.toHex[(b[i] >> 4) & 0x0F];
            c[pos++] = VideoConstant.toHex[b[i] & 0x0f];
        }
        return new String(c);
    }

    /**
     * Generate the challenge string.
     *
     * @return a generated nonce.
     */
    private String generateNonce() {
        long time = Instant.now().toEpochMilli();
        Random rand = new Random();
        long pad = rand.nextLong();
        String nonceString = Long.valueOf(time).toString() + Long.valueOf(pad).toString();
        byte mdbytes[] = messageDigest.digest(nonceString.getBytes());
        return toHexString(mdbytes);
    }

    public Response generateChallenge(HeaderFactory headerFactory, Response response, String realm) {
        try {
            WWWAuthenticateHeader proxyAuthenticate = headerFactory.createWWWAuthenticateHeader(VideoConstant.DEFAULT_SCHEME);
            proxyAuthenticate.setParameter("realm", realm);
            proxyAuthenticate.setParameter("qop", "auth");
            proxyAuthenticate.setParameter("nonce", generateNonce());
            proxyAuthenticate.setParameter("algorithm", VideoConstant.DEFAULT_ALGORITHM);
            response.setHeader(proxyAuthenticate);
        } catch (Exception ex) {
            InternalErrorHandler.handleException(ex);
        }
        return response;
    }
    /**
     * 对入站请求进行身份验证
     *
     * @param request - 验证请求
     * @param hashedPassword -- 明文密码的MD5哈希字符串.
     *
     * @return 如果验证成功，则为true，否则为false
     */
    public boolean doAuthenticateHashedPassword(Request request, String hashedPassword) {
        AuthorizationHeader authHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
        if ( authHeader == null ) {
            return false;
        }
        String realm = authHeader.getRealm();
        String username = authHeader.getUsername();

        if ( username == null || realm == null ) {
            return false;
        }

        String nonce = authHeader.getNonce();
        URI uri = authHeader.getURI();
        if (uri == null) {
            return false;
        }



        String A2 = request.getMethod().toUpperCase() + ":" + uri.toString();
        String HA1 = hashedPassword;


        byte[] mdbytes = messageDigest.digest(A2.getBytes());
        String HA2 = toHexString(mdbytes);

        String cnonce = authHeader.getCNonce();
        String KD = HA1 + ":" + nonce;
        if (cnonce != null) {
            KD += ":" + cnonce;
        }
        KD += ":" + HA2;
        mdbytes = messageDigest.digest(KD.getBytes());
        String mdString = toHexString(mdbytes);
        String response = authHeader.getResponse();


        return mdString.equals(response);
    }

    /**
     * 使用明文密码对入站请求进行身份验证。
     *
     * @param request - 验证请求
     * @param pass -- 纯文本密码
     * @return 如果验证成功，则为true，否则为false
     */
    public boolean doAuthenticatePlainTextPassword(Request request, String pass) {
        AuthorizationHeader authHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
        if ( authHeader == null  || authHeader.getRealm() == null) {
            return false;
        }
        String realm = authHeader.getRealm().trim();
        String username = authHeader.getUsername().trim();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(realm)) {
            return false;
        }
        String nonce = authHeader.getNonce();
        URI uri = authHeader.getURI();
        if (uri == null) {
            return false;
        }
        // qop 保护质量 包含auth（默认的）和auth-int（增加了报文完整性检测）两种策略
        String qop = authHeader.getQop();
        // 客户端随机数，这是一个不透明的字符串值，由客户端提供，并且客户端和服务器都会使用，以避免用明文文本。
        // 这使得双方都可以查验对方的身份，并对消息的完整性提供一些保护
        String cnonce = authHeader.getCNonce();
        // nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
        int nc = authHeader.getNonceCount();
        String ncStr = String.format("%08x", nc).toUpperCase();
        // String ncStr = new DecimalFormat("00000000").format(nc);
        // String ncStr = new DecimalFormat("00000000").format(Integer.parseInt(nc + "", 16));
        String A1 = username + ":" + realm + ":" + pass;
        String A2 = request.getMethod().toUpperCase() + ":" + uri.toString();
        byte mdbytes[] = messageDigest.digest(A1.getBytes());
        String HA1 = toHexString(mdbytes);
        log.debug("A1: " + A1);
        log.debug("A2: " + A2);
        mdbytes = messageDigest.digest(A2.getBytes());
        String HA2 = toHexString(mdbytes);
        log.debug("HA1: " + HA1);
        log.debug("HA2: " + HA2);
        // String cnonce = authHeader.getCNonce();
        log.debug("nonce: " + nonce);
        log.debug("nc: " + ncStr);
        log.debug("cnonce: " + cnonce);
        log.debug("qop: " + qop);
        String KD = HA1 + ":" + nonce;
        if (qop != null && qop.equalsIgnoreCase("auth") ) {
            if (nc != -1) {
                KD += ":" + ncStr;
            }
            if (cnonce != null) {
                KD += ":" + cnonce;
            }
            KD += ":" + qop;
        }
        KD += ":" + HA2;
        log.debug("KD: " + KD);
        mdbytes = messageDigest.digest(KD.getBytes());
        String mdString = toHexString(mdbytes);
        log.debug("mdString: " + mdString);
        String response = authHeader.getResponse();
        log.debug("response: " + response);
        return mdString.equals(response);

    }

}
