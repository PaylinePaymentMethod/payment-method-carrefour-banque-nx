package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetServiceImplTest {

    @Mock
    private CirceoProxy circeoProxy;

    @InjectMocks
    private ResetServiceImpl underTest;

    @Test
    void reset_RequestTestOK() throws HttpErrorException {
        final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
        final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
        doReturn(cancelationResponse).when(circeoProxy).doCancel(resetRequest, resetRequest.getPartnerConfiguration());
        //Test
        final ResetResponse resetResponse = underTest.resetRequest(resetRequest);

        assertNotNull(resetResponse);
        assertEquals("financingId", resetResponse.getPartnerTransactionId());
        assertEquals(CancelationRequestState.DONE.name(), ((ResetResponseSuccess) resetResponse).getStatusCode());
        assertEquals(ResetResponseSuccess.class, resetResponse.getClass());
    }

    @Test
    void reset_RequestTestFailed() throws HttpErrorException {
        final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
        final CancelationResponse cancelationResponse = MockUtils.aCancelationFailureResponse();
        doReturn(cancelationResponse).when(circeoProxy).doCancel(resetRequest, resetRequest.getPartnerConfiguration());
        //Test
        final ResetResponse resetResponse = underTest.resetRequest(resetRequest);

        assertNotNull(resetResponse);
        assertEquals(CancelationRequestState.FAILED.name(), ((ResetResponseFailure) resetResponse).getErrorCode());
        assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((ResetResponseFailure) resetResponse).getFailureCause());
        assertEquals(ResetResponseFailure.class, resetResponse.getClass());
    }

    @Test
    void reset_RequestTestReceived() throws HttpErrorException {
        final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
        final CancelationResponse cancelationResponse = MockUtils.aCancelationReceivedFailureResponse();
        doReturn(cancelationResponse).when(circeoProxy).doCancel(resetRequest, resetRequest.getPartnerConfiguration());
        //Test
        final ResetResponse resetResponse = underTest.resetRequest(resetRequest);

        assertNotNull(resetResponse);
        assertEquals(CancelationRequestState.RECEIVED.name(), ((ResetResponseFailure) resetResponse).getErrorCode());
        assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, ((ResetResponseFailure) resetResponse).getFailureCause());
        assertEquals(ResetResponseFailure.class, resetResponse.getClass());
    }

    @Test
    void reset_RequestShouldThrowHttpErrorException() throws HttpErrorException {
        final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
        final HttpErrorException exception = new HttpErrorException(null, "server error");
        doThrow(exception).when(circeoProxy).doCancel(resetRequest, resetRequest.getPartnerConfiguration());
        //Test
        final ResetResponse resetResponse = underTest.resetRequest(resetRequest);

        assertNotNull(resetResponse);
        final ResetResponseFailure resetResponseFailure = (ResetResponseFailure) resetResponse;
        assertEquals(FailureCause.INVALID_DATA, resetResponseFailure.getFailureCause());
        assertEquals("server error", resetResponseFailure.getErrorCode());
    }
}
