package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)

public class RefundServiceImplTest {
    @Mock
    private CirceoProxy circeoProxy;

    @InjectMocks
    private RefundServiceImpl underTest;

    @Test
    void refund_RequestTestOK() throws HttpErrorException {
        final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(1000);
        final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
        //Mock
        doReturn(cancelationResponse).when(circeoProxy).doCancel(any(), any());
        //Test
        final RefundResponse refundResponse = underTest.refundRequest(refundRequest);

        assertNotNull(refundResponse);
        assertEquals("financingId", refundResponse.getPartnerTransactionId());
        assertEquals(CancelationRequestState.DONE.name(), ((RefundResponseSuccess) refundResponse).getStatusCode());
        assertEquals(RefundResponseSuccess.class, refundResponse.getClass());
    }

    @Test
    void refund_RequestTestFailed() throws HttpErrorException {
        final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(1000);
        final CancelationResponse cancelationResponse = MockUtils.aCancelationFailureResponse();
        //Mock
        doReturn(cancelationResponse).when(circeoProxy).doCancel(any(), any());
        //Test
        final RefundResponse refundResponse = underTest.refundRequest(refundRequest);

        assertNotNull(refundResponse);
        assertEquals(CancelationRequestState.FAILED.name(), ((RefundResponseFailure) refundResponse).getErrorCode());
        assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((RefundResponseFailure) refundResponse).getFailureCause());
        assertEquals(RefundResponseFailure.class, refundResponse.getClass());
    }

    @Test
    void refund_RequestTestReceived() throws HttpErrorException {
        final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(1000);
        final CancelationResponse cancelationResponse = MockUtils.aCancelationReceivedFailureResponse();
        //Mock
        doReturn(cancelationResponse).when(circeoProxy).doCancel(any(), any());
        //Test
        final RefundResponse refundResponse = underTest.refundRequest(refundRequest);

        assertNotNull(refundResponse);
        assertEquals(CancelationRequestState.RECEIVED.name(), ((RefundResponseFailure) refundResponse).getErrorCode());
        assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((RefundResponseFailure) refundResponse).getFailureCause());
        assertEquals(RefundResponseFailure.class, refundResponse.getClass());
    }

    @Test
    void refund_RequestShouldThrowHttpErrorException() throws HttpErrorException {
        final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(1000);
        final HttpErrorException exception = new HttpErrorException(null, "server error");
        //Mock
        doThrow(exception).when(circeoProxy).doCancel(any(), any());
        //Test
        final RefundResponse refundResponse = underTest.refundRequest(refundRequest);

        assertNotNull(refundResponse);
        final RefundResponseFailure refundResponseFailure = (RefundResponseFailure) refundResponse;
        assertEquals(FailureCause.INVALID_DATA, refundResponseFailure.getFailureCause());
        assertEquals("server error", refundResponseFailure.getErrorCode());
    }
}
