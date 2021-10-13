package com.payline.payment.carrefour.banque.nx.service.business.impl;

import com.payline.payment.carrefour.banque.nx.bean.request.State;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;

public class StatusToResponseConverter {

    private static class Holder {
        private static final StatusToResponseConverter INSTANCE = new StatusToResponseConverter();
    }
    public static StatusToResponseConverter getInstance() {
        return StatusToResponseConverter.Holder.INSTANCE;
    }

    private StatusToResponseConverter() {
    }

    /**
     * Convertit un FinancingRequestStatus en PaymentResponse à l'issue d'une demande de financement
     * @param financingRequestStatus le FinancingRequestStatus à convertir
     * @return la PaymentResponse
     */
    public PaymentResponse financingStatusToPaymentResponse(final FinancingRequestStatus financingRequestStatus) {
        final PaymentResponse paymentResponse;

        final String financingId = financingRequestStatus.getFinancingId();
        final State financingState = financingRequestStatus.getState();
        switch (financingState) {
            case AUTHORISED:
            case FINANCED:
                paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                        .withPartnerTransactionId(financingId)
                        .withStatusCode(financingState.name())
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .build();
                break;
            case CANCELED:
                paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withErrorCode(financingState.name())
                        .withPartnerTransactionId(financingId)
                        .withFailureCause(FailureCause.SESSION_EXPIRED)
                        .build();
                break;
            case CANCELED_BY_CLIENT:
                paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withErrorCode(financingState.name())
                        .withPartnerTransactionId(financingId)
                        .withFailureCause(FailureCause.CANCEL)
                        .build();
                break;
            case FAILED_AUTHENTICATION:
            case REJECTED:
            case REFUSED_AUTHORISATION:
                paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withErrorCode(financingState.name())
                        .withPartnerTransactionId(financingId)
                        .withFailureCause(FailureCause.REFUSED)
                        .build();
                break;
            default:
                paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withErrorCode(financingState.name())
                        .withPartnerTransactionId(financingId)
                        .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                        .build();
        }
        return paymentResponse;
    }
}
