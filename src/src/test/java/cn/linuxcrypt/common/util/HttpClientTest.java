package cn.linuxcrypt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

@Slf4j
public class HttpClientTest {

    @Test
    public void getDownloadMeta() {
        String source = "https://pic1.zhimg.com/80/v2-7ac400cb745b36f0667391999e828d14_720w.jpg?source=1940ef5c";

        HttpClient.Meta downloadMeta = HttpClient.getDownloadMeta(source, new HashMap<>());

        System.out.println(JsonUtil.toJson(downloadMeta));
    }
}