package cn.linuxcrypt.common.util;

import cn.linuxcrypt.common.Constant;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtils {

    @Slf4j
    public static class DomainUtils {
        /**
         * 定义正则表达式，域名的根需要自定义，这里不全
         */
        private static final String RE_TOP = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co)\\b()*";
        private static final String DOMAIN = "(http|https)?(://)?(www+\\.)?(\\w+\\.)+(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co){1}";
        private static final Pattern pattern = Pattern.compile(RE_TOP, Pattern.CASE_INSENSITIVE);
        private static final Pattern domain_pattern = Pattern.compile(DOMAIN, Pattern.CASE_INSENSITIVE);

        public static String rootDomain(String url) {
            String result = url;
            try {
                Matcher matcher = pattern.matcher(url);
                matcher.find();
                result = matcher.group();
            } catch (Exception e) {
                log.error("get domain from url, execption: ", e);
            }
            return result;
        }

        public static String domain(String url) {
            if (!url.startsWith(Constant.Punctuation.HTTP)
                    && !url.startsWith(Constant.Punctuation.HTTPS)) {
                url = Constant.Punctuation.HTTP + url;
            }
            try {
                URL value = new URL(url);
                return value.getHost();
            } catch (MalformedURLException e) {
                log.error("get domain from url, execption: ", e);
            }

            return null;
        }

        public static String domain2(String url) {
            String result = url;
            try {
                Matcher matcher = domain_pattern.matcher(url);
                matcher.find();
                result = matcher.group(1);
                return result;
            } catch (Exception e) {
                log.error("get domain from url, execption: ", e);
            }
            return null;
        }
    }

//    public static void main(String[] args) {
//        String url1 = "http://a.b.c.cnblogs.com/0201zcr/p/6580192.html";
//        String url2 = "www.cnblogs.com/0201zcr/p/6580192.html";
//        System.out.println(CommonUtils.DomainUtils.domain2(url1));
//        System.out.println(CommonUtils.DomainUtils.domain2(url2));
//    }
}
