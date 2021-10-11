package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.service.PaymentWithRedirectionService;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    @Override
    public PaymentResponse finalizeRedirectionPayment(final RedirectionPaymentRequest redirectionPaymentRequest) {
        return null;
    }

    @Override
    public PaymentResponse handleSessionExpired(final TransactionStatusRequest transactionStatusRequest) {
        return null;
    }
}
