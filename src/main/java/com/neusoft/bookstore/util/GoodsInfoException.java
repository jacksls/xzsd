package com.neusoft.bookstore.util;

/**
 * @author Liang
 * @date 2020/5/15 8:56
 */
public class GoodsInfoException extends RuntimeException {
    public GoodsInfoException() {
        super();
    }

    public GoodsInfoException(String message) {
        super(message);
    }

    public GoodsInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoodsInfoException(Throwable cause) {
        super(cause);
    }

    protected GoodsInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
