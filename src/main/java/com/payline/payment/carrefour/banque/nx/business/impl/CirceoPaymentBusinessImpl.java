package com.payline.payment.carrefour.banque.nx.business.impl;

import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateResponse;
import com.payline.payment.carrefour.banque.nx.business.CirceoPaymentBusiness;
import com.payline.pmapi.bean.capture.response.CaptureResponse;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseFailure;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseSuccess;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;

public class CirceoPaymentBusinessImpl implements CirceoPaymentBusiness {

    @Override
    public RefundResponse handleRefundResponse(final CancelationResponse cancelationResponse) {
        final String financingId = cancelationResponse.getFinancingId();
        final CancelationRequestState cancelResponseStatus = cancelationResponse.getCancelationRequestState();
        final RefundResponse refundResponse;
        if (CancelationRequestState.DONE.equals(cancelResponseStatus)) {
            refundResponse = RefundResponseSuccess.RefundResponseSuccessBuilder
                    .aRefundResponseSuccess()
                    .withPartnerTransactionId(financingId)
                    .withStatusCode(cancelResponseStatus.name())
                    .build();

        } else if (CancelationRequestState.RECEIVED.equals(cancelResponseStatus)) {
            refundResponse = RefundResponseFailure.RefundResponseFailureBuilder
                    .aRefundResponseFailure()
                    .withPartnerTransactionId(financingId)
                    .withErrorCode(cancelResponseStatus.name())
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .build();
        } else {
            refundResponse = RefundResponseFailure.RefundResponseFailureBuilder
                    .aRefundResponseFailure()
                    .withPartnerTransactionId(financingId)
                    .withErrorCode(cancelResponseStatus.name())
                    .withFailureCause(FailureCause.PAYMENT_PARTNER_ERROR)
                    .build();
        }
        return refundResponse;
    }

    @Override
    public ResetResponse handleResetResponse(final CancelationResponse cancelationResponse) {
        final String financingId = cancelationResponse.getFinancingId();
        final CancelationRequestState cancelResponseStatus = cancelationResponse.getCancelationRequestState();
        final ResetResponse resetResponse;
        if (CancelationRequestState.DONE.equals(cancelResponseStatus)) {
            resetResponse = ResetResponseSuccess.ResetResponseSuccessBuilder
                    .aResetResponseSuccess()
                    .withPartnerTransactionId(financingId)
                    .withStatusCode(cancelResponseStatus.name())
                    .build();

        } else if (CancelationRequestState.RECEIVED.equals(cancelResponseStatus)) {
            resetResponse = ResetResponseFailure.ResetResponseFailureBuilder
                    .aResetResponseFailure()
                    .withPartnerTransactionId(financingId)
                    .withErrorCode(cancelResponseStatus.name())
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .build();
        } else {
            resetResponse = ResetResponseFailure.ResetResponseFailureBuilder
                    .aResetResponseFailure()
                    .withPartnerTransactionId(financingId)
                    .withErrorCode(cancelResponseStatus.name())
                    .withFailureCause(FailureCause.PAYMENT_PARTNER_ERROR)
                    .build();
        }
        return resetResponse;
    }

    @Override
    public CaptureResponse handleCaptureResponse(final DeliveryUpdateResponse deliveryResponse) {
        final String financingId = deliveryResponse.getFinancingId();
        final DeliveryUpdateRequestState deliveryRequestState = deliveryResponse.getDeliveryRequestState();
        return switch (deliveryRequestState) {
            case DONE -> CaptureResponseSuccess.CaptureResponseSuccessBuilder
                    .aCaptureResponseSuccess()
                    .withPartnerTransactionId(financingId)
                    .withStatusCode(deliveryRequestState.name())
                    .build();
            case RECEIVED -> CaptureResponseFailure.CaptureResponseFailureBuilder
                    .aCaptureResponseFailure()
                    .withPartnerTransactionId(financingId)
                    .withErrorCode(deliveryRequestState.name())
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .build();
            default -> CaptureResponseFailure.CaptureResponseFailureBuilder
                    .aCaptureResponseFailure()
                    .withPartnerTransactionId(financingId)
                    .withErrorCode(deliveryRequestState.name())
                    .withFailureCause(FailureCause.PAYMENT_PARTNER_ERROR)
                    .build();
        };
    }
}
