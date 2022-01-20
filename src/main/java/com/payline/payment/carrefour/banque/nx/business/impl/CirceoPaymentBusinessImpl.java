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
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.bean.reset.request.ResetRequest;
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
    public ResetRequest convertToResetRequest(final RefundRequest refundRequest) {

        return  ResetRequest.ResetRequestBuilder.aResetRequest()
                .withAmount(refundRequest.getAmount())
                .withTotalResetedAmount(refundRequest.getTotalRefundedAmount())
                .withBuyer(refundRequest.getBuyer())
                .withContractConfiguration(refundRequest.getContractConfiguration())
                .withEnvironment(refundRequest.getEnvironment())
                .withOrder(refundRequest.getOrder())
                .withPartnerConfiguration(refundRequest.getPartnerConfiguration())
                .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                .withPluginConfiguration(refundRequest.getPluginConfiguration())
                .withRequestContext(refundRequest.getRequestContext())
                .withSoftDescriptor(refundRequest.getSoftDescriptor())
                .withTransactionAdditionalData(refundRequest.getTransactionAdditionalData())
                .withTransactionId(refundRequest.getTransactionId())
                .build();
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
        final DeliveryUpdateRequestState cancelResponseStatus = deliveryResponse.getDeliveryRequestState();
        final CaptureResponse captureResponse;
        switch (cancelResponseStatus) {
            case DONE :
                captureResponse = CaptureResponseSuccess.CaptureResponseSuccessBuilder
                        .aCaptureResponseSuccess()
                        .withPartnerTransactionId(financingId)
                        .withStatusCode(cancelResponseStatus.name())
                        .build();
                        break;
            case RECEIVED:
                captureResponse = CaptureResponseFailure.CaptureResponseFailureBuilder
                        .aCaptureResponseFailure()
                        .withPartnerTransactionId(financingId)
                        .withErrorCode(cancelResponseStatus.name())
                        .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                        .build();
                break;
            default :
                captureResponse = CaptureResponseFailure.CaptureResponseFailureBuilder
                        .aCaptureResponseFailure()
                        .withPartnerTransactionId(financingId)
                        .withErrorCode(cancelResponseStatus.name())
                        .withFailureCause(FailureCause.PAYMENT_PARTNER_ERROR)
                        .build();
                break;
        }
        return captureResponse;
    }
}
