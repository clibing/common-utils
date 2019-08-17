package cn.linuxcrypt.common.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class NetworkUtils {

    private static final Set<String> SERVER_IPS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "192.168.2.53"
    )));

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ip.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }

    public static boolean isHandlingInternalRequest() {
        RequestAttributes reqAttrs = RequestContextHolder.getRequestAttributes();
        if (reqAttrs instanceof ServletRequestAttributes) {
            HttpServletRequest request = (((ServletRequestAttributes) reqAttrs).getRequest());
            return isInternalHost(getIpAddress(request));
        }
        return false;
    }

    private static boolean isInternalHost(String hostOrIP) {
        if (StringUtils.isBlank(hostOrIP)) {
            return false;
        }

        InetAddress addr;
        try {
            addr = InetAddress.getByName(hostOrIP);
        } catch (UnknownHostException e) {
            log.info("Fail to parse host :{}", hostOrIP, e);
            return false;
        }

        return SERVER_IPS.contains(addr.getHostAddress()) || isLocalAddress(addr);
    }

    private static boolean isLocalAddress(InetAddress addr) {
        return addr.isLoopbackAddress()
                || addr.isLinkLocalAddress()
                || addr.isSiteLocalAddress();
    }
}
