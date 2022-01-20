package com.payline.payment.carrefour.banque.nx.business.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CirceoPaymentBusinessTest {

    private CirceoPaymentBusiness underTest = new CirceoPaymentBusinessImpl();

    @Nested
    class testHandleRefundResponse {
        @Test
        void withDoneStatus() {
            final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
            final RefundResponse refundResponse = underTest.handleRefundResponse(cancelationResponse);
            assertEquals(CancelationRequestState.DONE.name(), ((RefundResponseSuccess) refundResponse).getStatusCode());
        }
        @Test
        void withReceivedStatus() {
            final CancelationResponse cancelationResponse = MockUtils.aCancelationReceivedFailureResponse();
            final RefundResponse refundResponse = underTest.handleRefundResponse(cancelationResponse);
            assertEquals(CancelationRequestState.RECEIVED.name(), ((RefundResponseFailure) refundResponse).getErrorCode());
            assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((RefundResponseFailure) refundResponse).getFailureCause());

        }
        @Test
        void withFailureStatus() {
            final CancelationResponse cancelationResponse = MockUtils.aCancelationFailureResponse();
            final RefundResponse refundResponse = underTest.handleRefundResponse(cancelationResponse);
            assertEquals(CancelationRequestState.FAILED.name(), ((RefundResponseFailure) refundResponse).getErrorCode());
            assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((RefundResponseFailure) refundResponse).getFailureCause());
        }
    }

    @Nested
    class handleResetResponse {
        @Test
        void withDoneStatus() {
            final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
            final ResetResponse resetResponse = underTest.handleResetResponse(cancelationResponse);
            assertEquals(CancelationRequestState.DONE.name(), ((ResetResponseSuccess) resetResponse).getStatusCode());
        }

        @Test
        void withReceivedStatus() {
            final CancelationResponse cancelationResponse = MockUtils.aCancelationReceivedFailureResponse();
            final ResetResponse resetResponse = underTest.handleResetResponse(cancelationResponse);
            assertEquals(CancelationRequestState.RECEIVED.name(), ((ResetResponseFailure) resetResponse).getErrorCode());
            assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((ResetResponseFailure) resetResponse).getFailureCause());

        }

        @Test
        void withFailureStatus() {
            final CancelationResponse cancelationResponse = MockUtils.aCancelationFailureResponse();
            final ResetResponse resetResponse = underTest.handleResetResponse(cancelationResponse);
            assertEquals(CancelationRequestState.FAILED.name(), ((ResetResponseFailure) resetResponse).getErrorCode());
            assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((ResetResponseFailure) resetResponse).getFailureCause());
        }
    }

    @Nested
    class handleCaptureResponse {
        @Test
        void withDoneStatus() {
            final DeliveryUpdateResponse deliveryUpdateResponse = MockUtils.aDeliveryUpdateResponse();
            final CaptureResponse captureResponse = underTest.handleCaptureResponse(deliveryUpdateResponse);
            assertEquals(CancelationRequestState.DONE.name(), ((CaptureResponseSuccess) captureResponse).getStatusCode());
        }

        @Test
        void withReceivedStatus() {
            final DeliveryUpdateResponse deliveryUpdateResponse = MockUtils.aDeliveryUpdateReceivedFailureResponse();
            CaptureResponse resetResponse = underTest.handleCaptureResponse(deliveryUpdateResponse);
            assertEquals(CancelationRequestState.RECEIVED.name(), ((CaptureResponseFailure) resetResponse).getErrorCode());
            assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((CaptureResponseFailure) resetResponse).getFailureCause());

        }

        @Test
        void withFailureStatus() {
            final DeliveryUpdateResponse deliveryUpdateResponse = MockUtils.aDeliveryUpdateFailureResponse();
            CaptureResponse resetResponse = underTest.handleCaptureResponse(deliveryUpdateResponse);
            assertEquals(CancelationRequestState.FAILED.name(), ((CaptureResponseFailure) resetResponse).getErrorCode());
            assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((CaptureResponseFailure) resetResponse).getFailureCause());
        }
    }

    @Test
    void convertToResetRequestOK() {
        final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(1000);
        ResetRequest resetRequest = underTest.convertToResetRequest(refundRequest);
        assertEquals(resetRequest.getTotalResetedAmount(), refundRequest.getTotalRefundedAmount());
        assertEquals(resetRequest.getAmount(), refundRequest.getAmount());
        assertEquals(resetRequest.getPartnerConfiguration(), refundRequest.getPartnerConfiguration());
    }
}
