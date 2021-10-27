package com.payline.payment.carrefour.banque.nx.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class InvalidDataException extends PluginException {

    public InvalidDataException(final String message) {
        super(message, FailureCause.INVALID_DATA);
    }

    public InvalidDataException(final String message, final Exception cause) {
        super(message, FailureCause.INVALID_DATA, cause);
    }
}
