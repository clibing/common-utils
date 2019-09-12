package cn.linuxcrypt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 签名功能
 *
 * @author clibing
 */
@Slf4j
public class SignUtils {

    public enum SignTypeEnum {
        /**
         * 签名方式 Base64
         */
//        BASE64,
        /**
         * 签名方式 MD5
         */
        MD5,
        /**
         * 签名方式 RSA
         */
        RSA
    }

    private interface Sing {
        String sign(String content, String secretKey);
    }

    /**
     * 签名
     *
     * @param data
     * @param secretKey 密钥
     * @param signType
     * @return
     */

    public static void sign(Map<String, String> data, String charset, String secretKey, SignTypeEnum signType) {
        Assert.notNull(data, "参数不能为空");
        Assert.notNull(secretKey, "密钥不能为空");
        Assert.notNull(signType, "签名类型不能为空");

        String value = null;
        String content = mapConvertStringAndSign(data);

        if (signType == SignTypeEnum.RSA) {
            value = RSA.sign(content, secretKey, charset);
        }

        if (signType == SignTypeEnum.MD5) {
            value = MD5.sign(content, secretKey, charset);
        }

        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("签名异常");
        }

        //签名结果与签名方式加入请求提交参数组中
        data.put("sign", value);
    }

    public static String mapConvertStringAndSign(Map<String, String> data) {
        data.remove("sign");
        data.remove("signType");
        return mapConvertStringWithAnd(data, "&", false);
    }

    public static String mapConvertStringAndEncode(Map<String, String> data) {
        return mapConvertStringWithAnd(data, "&", true);
    }

    /**
     * map转字符串以字符拼接，并按照ascii排序key
     *
     * @param data
     * @param separator
     * @return
     */
    private static String mapConvertStringWithAnd(Map<String, String> data, String separator, boolean valueEncode) {
        Assert.notNull(separator, "拼接的字符串不能为空");

        List<String> keyAndValue = data.entrySet().stream()
                .filter(e -> StringUtils.isNotBlank(e.getValue()))
                .sorted(Map.Entry.comparingByKey())
                // 将值进行编码，注意在计算签名的时候不需要进行encode 注意 encode时只有get请求使用
                .map(e -> String.format("%s=%s", e.getKey(), valueEncode ? encode(e.getValue()) : e.getValue()))
                .collect(Collectors.toList());
        String content = StringUtils.join(keyAndValue, separator);
        return content;
    }

    /**
     * value进行URL编码
     * 注意只有在进行Get请求时，使用编码，应为httpclient封装类在get请求不进行编码
     *
     * @param param
     * @return
     */
    private static String encode(String param) {
        if (StringUtils.isBlank(param)) {
            return param;
        }
        try {
            param = URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("url中的请求参数编码异常，返回原值");
        }
        return param;
    }

    static class MD5 {

        /**
         * 签名字符串
         *
         * @param text          需要签名的字符串
         * @param key           密钥
         * @param input_charset 编码格式
         * @return 签名结果
         */
        public static String sign(String text, String key, String input_charset) {
            text = text + key;
            return DigestUtils.md5Hex(getContentBytes(text, input_charset));
        }

        /**
         * 签名字符串
         *
         * @param text          需要签名的字符串
         * @param sign          签名结果
         * @param key           密钥
         * @param input_charset 编码格式
         * @return 签名结果
         */
        public static boolean verify(String text, String sign, String key, String input_charset) {
            text = text + key;
            String md5Hex = DigestUtils.md5Hex(getContentBytes(text, input_charset));
            if (md5Hex.equals(sign)) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * @param content
         * @param charset
         * @return
         * @throws SignatureException
         * @throws UnsupportedEncodingException
         */
        private static byte[] getContentBytes(String content, String charset) {
            if (charset == null || "".equals(charset)) {
                return content.getBytes();
            }
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,你目前指定的编码集是:" + charset);
            }
        }

    }


    static class RSA {

        public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

        /**
         * RSA签名
         *
         * @param content       待签名数据
         * @param privateKey    商户私钥
         * @param input_charset 编码格式
         * @return 签名值
         */
        public static String sign(String content, String privateKey, String input_charset) {
            try {
                PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
                KeyFactory keyf = KeyFactory.getInstance("RSA");
                PrivateKey priKey = keyf.generatePrivate(priPKCS8);

                java.security.Signature signature = java.security.Signature
                        .getInstance(SIGN_ALGORITHMS);

                signature.initSign(priKey);
                signature.update(content.getBytes(input_charset));

                byte[] signed = signature.sign();

                return Base64.encodeBase64String(signed);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * RSA验签名检查
         *
         * @param content        待签名数据
         * @param sign           签名值
         * @param ali_public_key 支付宝公钥
         * @param input_charset  编码格式
         * @return 布尔值
         */
        public static boolean verify(String content, String sign, String ali_public_key, String input_charset) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] encodedKey = Base64.decodeBase64(ali_public_key);
                PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


                java.security.Signature signature = java.security.Signature
                        .getInstance(SIGN_ALGORITHMS);

                signature.initVerify(pubKey);
                signature.update(content.getBytes(input_charset));

                boolean bverify = signature.verify(Base64.decodeBase64(sign));
                return bverify;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        /**
         * 解密
         *
         * @param content       密文
         * @param private_key   商户私钥
         * @param input_charset 编码格式
         * @return 解密后的字符串
         */
        public static String decrypt(String content, String private_key, String input_charset) throws Exception {
            PrivateKey prikey = getPrivateKey(private_key);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, prikey);

            InputStream ins = new ByteArrayInputStream(Base64.decodeBase64(content));
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
            byte[] buf = new byte[128];
            int bufl;

            while ((bufl = ins.read(buf)) != -1) {
                byte[] block;

                if (buf.length == bufl) {
                    block = buf;
                } else {
                    block = new byte[bufl];
                    for (int i = 0; i < bufl; i++) {
                        block[i] = buf[i];
                    }
                }

                writer.write(cipher.doFinal(block));
            }

            return new String(writer.toByteArray(), input_charset);
        }


        /**
         * 得到私钥
         *
         * @param key 密钥字符串（经过base64编码）
         * @throws Exception
         */
        public static PrivateKey getPrivateKey(String key) throws Exception {

            byte[] keyBytes = Base64.decodeBase64(key);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            return privateKey;
        }
    }
}
