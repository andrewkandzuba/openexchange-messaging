package io.openexchange.tx;

import org.springframework.transaction.TransactionException;

public class UnsupportedTransactionTypeException extends TransactionException {
    public UnsupportedTransactionTypeException(String msg) {
        super(msg);
    }

    public UnsupportedTransactionTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
