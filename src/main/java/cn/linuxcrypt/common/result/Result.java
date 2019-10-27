package cn.linuxcrypt.common.result;

import cn.linuxcrypt.common.result.Codes.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * AbstractResult 为结果返回类型
 *
 * @param <T>
 * @author clibing
 */
@Getter
@Setter
public class Result<T extends Object> {
    private Integer code;

    private T data;

    private String message;

    private Long total;

    public Result() {
    }

    public Result(Code code) {
        if (null == code) {
            throw new NullPointerException("code is null");
        }
        this.code = code.getValue();
    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(Code code, T data) {
        this(code);
        this.data = data;
    }

    public Result(Code code, String message) {
        this(code);
        this.message = message;
    }

    public Result(Code code, T data, String message) {
        this(code, data);
        this.message = message;
    }

    public Result(Integer code, T data, String message) {
        this(code, data);
        this.message = message;
    }

    public static Result success() {
        return success(null, null);
    }

    public static Result success(String message) {
        return success(null, message);
    }

    public static Result success(Object data, String message) {
        if (data != null && !StringUtils.isBlank(message)) {
            return new Result(Codes.SUCCESS, data, message);
        }

        if (data != null) {
            return new Result(Codes.SUCCESS, data, null);
        }

        if (!StringUtils.isBlank(message)) {
            return new Result(Codes.SUCCESS, null, message);
        }

        return new Result(Codes.SUCCESS);
    }

    public Result total(Long total){
        this.setTotal(total);
        return this;
    }

    public static Result fail(String message) {
        return fail(null, message);
    }

    public static Result fail(Object data, String message) {
        if (data != null && !StringUtils.isBlank(message)) {
            return new Result(Codes.FAIL, data, message);
        }

        if (data != null) {
            return new Result(Codes.FAIL, data, null);
        }

        if (!StringUtils.isBlank(message)) {
            return new Result(Codes.FAIL, null, message);
        }

        return new Result(Codes.FAIL);
    }

    public static Result notFound(String message) {
        if (StringUtils.isBlank(message)) {
            return new Result(Codes.E404);
        }
        return new Result(Codes.E404, message);
    }

    public boolean hasError() {
        return this.code.equals(Codes.FAIL.getValue());
    }
}