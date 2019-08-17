package cn.linuxcrypt.common;

import java.util.*;

/**
 * @author t420
 */
public final class Constant {
    public static class Application {
        public static class Profiles {
            public static final String ENV_WWW = "www";
            public static final String ENV_DEV = "dev";
            public static final String ENV_LOCAL = "local";
            public static final String ENV_TEST = "test";
        }

        public static class Swagger {
        }
    }

    public static class Punctuation {
        public static final String LT = "<";
        public static final String GT = ">";
        public static final String QUOT = "\"";
        public static final String EQUAL = "=";
        public static final String DOUBLE_SLASH = "//";
        public static final String COLON = ":";
        public static final String COMMA = ",";
        public static final String COLON_DOUBLE_SLASH = COLON + DOUBLE_SLASH;
        public static final String HTTP = "http://";
        public static final String HTTPS = "https://";
        public static final String SINGLE_SLASH = "/";
        public static final String SPOT = ".";
        public static final String WELL_NUMBER = "#";
        public static final String STAR = "*";
        public static final String NULL = "null";
    }

    public static class Http {
        public static final String HEALTH_URI = "/health";
        public static final String HTTP_HEADER_REFERER = "referer";
        public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
        public static final String HTTP = "http";
        public static final String HTTPS = "https";
        public static final String UTF8 = "UTF-8";
    }

    public static class Collection {
        public static final Map MAP_EMPTY = new HashMap<>(Number.I.ZERO);
        public static final Set SET_EMPTY = new HashSet<>(Number.I.ZERO);
        public static final List LIST_EMPTY = new ArrayList(Number.I.ZERO);
    }

    public final static class Number {
        public final static class I {
            public static final int ONE = 1;
            public static final int ZERO = 0;
        }

        public final static class L {
            public static final Long ONE = 1L;
            public static final long ZERO = 0L;
        }
    }

    public final static class Page {
        public static final int DEFAULT_PAGE_NUMBER = Number.I.ONE;
        public static final int DEFAULT_PAGE_SIZE = 25;
        public static final long DEFAULT_OFFSET = Number.L.ZERO;
        public static final long DEFAULT_LIMIT = 25L;
        public static final long DEFAULT_MAX_LIMIT = 1024;
        public static final long ALL = Integer.MAX_VALUE;
    }
}
