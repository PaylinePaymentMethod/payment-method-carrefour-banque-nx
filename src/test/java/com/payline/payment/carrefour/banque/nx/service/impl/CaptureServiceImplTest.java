package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateResponse;
import com.payline.payment.carrefour.banque.nx.business.CirceoPaymentBusiness;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.capture.response.CaptureResponse;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseFailure;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseSuccess;
import com.payline.pmapi.bean.common.FailureCause;
import jdk.jfr.Unsigned;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaptureServiceImplTest {

    @Mock
    private CirceoProxy circeoProxy;

    @Mock
    private CirceoPaymentBusiness circeoPaymentBusiness;

    @InjectMocks
    private CaptureServiceImpl underTest;

    @Test
    void assertFalseMultiple() {
        assertFalse(underTest.canMultiple());
    }

    @Test
    void assertTruePartial() {
        assertTrue(underTest.canPartial());
    }

    @Nested
    class testCaptureRequest {

        @Test
        void testCaptureRequestOK() throws Exception {
            final DeliveryUpdateResponse deliveryUpdateResponse = MockUtils.aDeliveryUpdateResponse();
            final CaptureRequest captureRequest = MockUtils.aPaylineCaptureRequest(4000);
            final CaptureResponse expectedCaptureResponse = CaptureResponseSuccess.
                    CaptureResponseSuccessBuilder.aCaptureResponseSuccess().build();

            doReturn(deliveryUpdateResponse).when(circeoProxy).doCapture(captureRequest);
            doReturn(expectedCaptureResponse).when(circeoPaymentBusiness).handleCaptureResponse(eq(deliveryUpdateResponse));
            final CaptureResponse captureResponse = underTest.captureRequest(captureRequest);
            assertEquals(expectedCaptureResponse, captureResponse);
        }

        @Test
        void testWithHttpException() throws Exception{
            final CaptureRequest captureRequest = MockUtils.aPaylineCaptureRequest(4000);
            doThrow(HttpErrorException.class).when(circeoProxy).doCapture(captureRequest);
            final CaptureResponse captureResponse = underTest.captureRequest(captureRequest);
            assertNotNull(captureResponse);
            assertTrue(captureResponse instanceof CaptureResponseFailure);
            final CaptureResponseFailure captureResponseFailure = (CaptureResponseFailure) captureResponse;
            assertEquals(FailureCause.INVALID_DATA, captureResponseFailure.getFailureCause());
        }
    }
}