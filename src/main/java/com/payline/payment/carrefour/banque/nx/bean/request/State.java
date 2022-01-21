package com.payline.payment.carrefour.banque.nx.bean.request;

public enum State {
    ACCEPTED,
    AUTHORISED,
    CANCELED,
    CANCELED_BY_CLIENT,
    FAILED_AUTHENTICATION,
    FINANCED,
    REJECTED,
    REFUSED_AUTHORISATION,
    STARTED,
    WAITING_FOR_CAPTURE,
    INITIATED
}
