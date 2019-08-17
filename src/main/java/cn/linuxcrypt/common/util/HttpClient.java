package cn.linuxcrypt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于Apache HttpClient的工具类
 *
 * @author clibing
 */
@Slf4j
public class HttpClient {
    /*
     * 默认编码
     */
    private static final String DEFAULT_CHARSET = "UTF-8";
    /*
     * 默认连接池中最大连接数
     */
    private static final int DEFAULT_CONNECTION_MAX_TOTAL = 200;
    /*
     * 从连接池中获取请求连接的超时时间
     * 0 无限制
     * -1 走系统默认设置
     */
    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;

    /*
     * 默认连接超时时间
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    /*
     * 默认socket读取数据超时时间,具体的长耗时请求中(如文件传送等)必须覆盖此设置
     */
    private static final int DEFAULT_SO_TIMEOUT = 15000;
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";
    private static final PoolingHttpClientConnectionManager DEFAULT_CONNECTION_MANAGER;
    private static final ConnectionConfig DEFAULT_CONNECTION_CONFIG;
    private static final RequestConfig DEFAULT_REQUEST_CONFIG;
    private static final Registry<CookieSpecProvider> DEFAULT_COOKIE_SPEC_REGISTRY;
    private static final CloseableHttpClient HTTP_CLIENT;

    static {
        try {
            SSLContext sslContext = SSLContexts.custom().build();
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext)).build();
            // 消息约束
            /*MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
	                .setMaxLineLength(2000)
	                .build();*/
            // 默认连接配置
            DEFAULT_CONNECTION_CONFIG = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .build();
            // 默认请求配置
            DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                    .setSocketTimeout(DEFAULT_SO_TIMEOUT)
                    .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)
                    .build();
            DEFAULT_CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            DEFAULT_CONNECTION_MANAGER.setMaxTotal(DEFAULT_CONNECTION_MAX_TOTAL);
            DEFAULT_CONNECTION_MANAGER.setDefaultMaxPerRoute(20);
            // 设置连接配置
            DEFAULT_CONNECTION_MANAGER.setDefaultConnectionConfig(DEFAULT_CONNECTION_CONFIG);
            // 注册Cookie策略
            DEFAULT_COOKIE_SPEC_REGISTRY = RegistryBuilder.<CookieSpecProvider>create()
                    .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                    .build();
            HTTP_CLIENT = HttpClients.custom().setConnectionManager(DEFAULT_CONNECTION_MANAGER)
                    .setDefaultCookieSpecRegistry(DEFAULT_COOKIE_SPEC_REGISTRY)
                    .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
                    .setDefaultConnectionConfig(DEFAULT_CONNECTION_CONFIG)
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                    .setRetryHandler(new DefaultHttpRequestRetryHandler())
                    .setUserAgent(DEFAULT_USER_AGENT)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    private static CloseableHttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    private static void applyDefaultHeaders(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/json,application/xml;q=0.9,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpRequestBase.setHeader("Connection", "keep-alive");
        httpRequestBase.setHeader("DNT", "1");
        httpRequestBase.setHeader("User-Agent", DEFAULT_USER_AGENT);
    }

    /**
     * <p>执行Get请求</p>
     *
     * @param url get url
     * @return - 默认返回text/html
     */
    public static String doGet(String url) {
        log.debug(">>> doGet[url = {}]", url);
        HttpGet httpGet = new HttpGet(url);
        applyDefaultHeaders(httpGet);
        return doHttpRequest(httpGet, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>执行Get请求</p>
     *
     * @param url
     * @param responseCharset - 指定响应html字符集
     * @return - 默认返回text/html
     */
    public static String doGet(String url, String responseCharset) {
        log.debug(">>> doGet[url = {}]", url);
        HttpGet httpGet = new HttpGet(url);
        applyDefaultHeaders(httpGet);
        return doHttpRequest(httpGet, null, new DefaultStringResponseHandler(responseCharset));
    }

    /**
     * <p>执行Get请求</p>
     *
     * @param url
     * @param responseHandler - 响应处理器
     * @return - 默认返回text/html
     */
    public static <T> T doGet(String url, ResponseHandler<T> responseHandler) {
        log.debug(">>> doGet[url = {}]", url);
        HttpGet httpGet = new HttpGet(url);
        applyDefaultHeaders(httpGet);
        return doHttpRequest(httpGet, null, responseHandler);
    }

    /**
     * <p>执行Post请求</p>
     */
    public static String doPost(String url, Map<String, String> paramMap) {
        log.debug(">>> doPost[url = {}, paramMap = {}]", url, paramMap);
        HttpPost httpPost = createHttpPost(url, paramMap);
        applyDefaultHeaders(httpPost);
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>执行Post请求</p>
     *
     * @param url             post的请求url
     * @param paramMap        form参数体
     * @param responseCharset - 指定响应html字符集
     * @return
     */
    public static String doPost(String url, Map<String, String> paramMap, String responseCharset) {
        log.debug(">>> doPost[url = {}, paramMap = {}]", url, paramMap);
        HttpPost httpPost = createHttpPost(url, paramMap);
        applyDefaultHeaders(httpPost);
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler(responseCharset));
    }

    /**
     * <p>根据URL和参数创建HttpPost对象</p>
     *
     * @param url      post的url
     * @param paramMap post的form参数
     * @return
     */
    public static HttpPost createHttpPost(String url, Map<String, String> paramMap) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (paramMap != null && !paramMap.isEmpty()) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, DEFAULT_CHARSET);
                httpPost.setEntity(formEntity);
            }
            return httpPost;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    /**
     * <p>执行http请求</p>
     *
     * @param httpMethod              - HTTP请求(HttpGet、HttpPost等等)
     * @param httpClientContextSetter - 可选参数,请求前的一些参数设置(如：cookie、鉴权认证等)
     * @param responseHandler         - 必选参数,响应处理类(如针对httpstatu的各种值做一些策略处理等等)
     * @return
     */
    public static <T> T doHttpRequest(HttpRequestBase httpMethod, HttpClientContextSetter httpClientContextSetter, ResponseHandler<T> responseHandler) {
        Args.notNull(responseHandler, "Parameter 'httpMethod' can not be null!");
        Args.notNull(responseHandler, "Parameter 'responseHandler' can not be null!");
        CloseableHttpResponse response = null;
        try {
            if (httpClientContextSetter != null) {
                HttpClientContext context = HttpClientContext.create();
                httpClientContextSetter.setHttpClientContext(context);
                response = getHttpClient().execute(httpMethod, context);
            } else {
                response = getHttpClient().execute(httpMethod);
            }
            return response == null ? null : responseHandler.handleResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpClientException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * <p>对于特殊请求(比如请求涉及到cookie的处理,鉴权认证等),默认的一些配置已经满足不了了,
     * 这时就可以使用一个独立于全局的配置来执行请求,这个独立于全局,又不会干扰其他线程的请求执行的机制就是使用HttpClientContext,
     * 该设置类用于对已经提供的一个基于全局配置的副本,来设置一些配置(见HttpClientContext.setXxx)</p>
     *
     * @author pengpeng
     * @version 1.0
     * @date 2014年7月20日 下午4:27:50
     */
    public static interface HttpClientContextSetter {
        public void setHttpClientContext(HttpClientContext context);
    }

    /**
     * <p>默认keepAlive策略：如果响应中存在服务器端的keepAlive超时时间则返回该时间否则返回默认的</p>
     *
     * @author pengpeng
     * @version 1.0
     * @date 2014年7月20日 下午3:26:39
     */
    public static class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            return 30 * 1000; //默认30秒
        }
    }

    /**
     * <p>默认的处理返回值为String的ResponseHandler</p>
     *
     * @author pengpeng
     * @version 1.0
     * @date 2014年7月21日 上午9:13:22
     */
    public static class DefaultStringResponseHandler implements ResponseHandler<String> {
        /**
         * 默认响应html字符集编码
         */
        private Charset defaultCharset;

        public DefaultStringResponseHandler() {
            super();
        }

        public DefaultStringResponseHandler(String defaultCharset) {
            super();
            this.defaultCharset = Charset.forName(defaultCharset);
        }

        public Charset getDefaultCharset() {
            return defaultCharset;
        }

        public void setDefaultCharset(Charset defaultCharset) {
            this.defaultCharset = defaultCharset;
        }

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity, defaultCharset == null ? ContentType.getOrDefault(httpEntity).getCharset() : defaultCharset);
            }
            return null;
        }
    }

    public static class HttpClientException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpClientException(String message) {
            super(message);
        }

        public HttpClientException(Throwable cause) {
            super(cause);
        }

        /**
         * Return the detail message, including the message from the nested exception
         * if there is one.
         */
        @Override
        public String getMessage() {
            return buildMessage(super.getMessage(), getCause());
        }

        /**
         * Retrieve the innermost cause of this exception, if any.
         *
         * @return the innermost exception, or {@code null} if none
         * @since 2.0
         */
        public Throwable getRootCause() {
            Throwable rootCause = null;
            Throwable cause = getCause();
            while (cause != null && cause != rootCause) {
                rootCause = cause;
                cause = cause.getCause();
            }
            return rootCause;
        }

        /**
         * Build a message for the given base message and root cause.
         *
         * @param message the base message
         * @param cause   the root cause
         * @return the full exception message
         */
        protected String buildMessage(String message, Throwable cause) {
            if (cause != null) {
                StringBuilder sb = new StringBuilder();
                if (message != null) {
                    sb.append(message).append("; ");
                }
                sb.append("nested exception is ").append(cause);
                return sb.toString();
            } else {
                return message;
            }
        }
    }
}
