package cn.linuxcrypt.common.result;

/**
 * @author clibing
 */
public interface Codes {
    /**
     * 请求并成功返回
     */
    Code SUCCESS = new Code(200);
    /**
     * 请求失败
     */
    Code FAIL = new Code(0);

    // Bad Request
    Code E400 = new Code(400);
    Code E401 = new Code(401);
    Code E403 = new Code(403);
    Code E404 = new Code(404);

    Code E500 = new Code(500);
    Code E501 = new Code(501);

    /**
     * 通用的业务状态码
     */
    class Code {
        private int value;

        public Code(int value) {
            super();
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
