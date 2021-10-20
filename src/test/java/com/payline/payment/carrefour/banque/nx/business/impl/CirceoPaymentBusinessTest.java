package com.payline.payment.carrefour.banque.nx.business.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.business.CirceoPaymentBusiness;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CirceoPaymentBusinessTest {

    private CirceoPaymentBusiness underTest = new CirceoPaymentBusinessImpl();

    @Test
    void handleRefundResponse_Donne() {
        final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
        RefundResponse refundResponse = underTest.handleRefundResponse(cancelationResponse);
        assertEquals(CancelationRequestState.DONE.name(), ((RefundResponseSuccess) refundResponse).getStatusCode());
    }
    @Test
    void handleRefundResponse_RECEIVED() {
        final CancelationResponse cancelationResponse = MockUtils.aCancelationReceivedFailureResponse();
        RefundResponse refundResponse = underTest.handleRefundResponse(cancelationResponse);
        assertEquals(CancelationRequestState.RECEIVED.name(), ((RefundResponseFailure) refundResponse).getErrorCode());
        assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((RefundResponseFailure) refundResponse).getFailureCause());

    }
    @Test
    void handleRefundResponse_Failure() {
        final CancelationResponse cancelationResponse = MockUtils.aCancelationFailureResponse();
        RefundResponse refundResponse = underTest.handleRefundResponse(cancelationResponse);
        assertEquals(CancelationRequestState.FAILED.name(), ((RefundResponseFailure) refundResponse).getErrorCode());
        assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((RefundResponseFailure) refundResponse).getFailureCause());
    }

    @Test
    void handleResetResponse_Donne() {
        final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
        ResetResponse resetResponse = underTest.handleResetResponse(cancelationResponse);
        assertEquals(CancelationRequestState.DONE.name(), ((ResetResponseSuccess) resetResponse).getStatusCode());
    }

    @Test
    void handleResetResponse_RECEIVED() {
        final CancelationResponse cancelationResponse = MockUtils.aCancelationReceivedFailureResponse();
        ResetResponse resetResponse = underTest.handleResetResponse(cancelationResponse);
        assertEquals(CancelationRequestState.RECEIVED.name(), ((ResetResponseFailure) resetResponse).getErrorCode());
        assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((ResetResponseFailure) resetResponse).getFailureCause());

    }

    @Test
    void handleResetResponse_Failure() {
        final CancelationResponse cancelationResponse = MockUtils.aCancelationFailureResponse();
        ResetResponse resetResponse = underTest.handleResetResponse(cancelationResponse);
        assertEquals(CancelationRequestState.FAILED.name(), ((ResetResponseFailure) resetResponse).getErrorCode());
        assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((ResetResponseFailure) resetResponse).getFailureCause());
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
