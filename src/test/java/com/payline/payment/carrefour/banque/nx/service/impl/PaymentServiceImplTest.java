package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.mapper.FinancingRequestMapper;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.payment.carrefour.banque.nx.service.business.impl.RequiredDataValidator;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private RequiredDataValidator requiredDataValidator;

    @Mock
    private CirceoProxy circeoProxy;

    @Mock
    private FinancingRequestMapper financingRequestMapper;

    @InjectMocks
    private PaymentServiceImpl underTest;

    @Test
    void shouldReturnAPaymentResponseRedirect() throws MalformedURLException, HttpErrorException {
        final PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
        final FinancingRequest financingRequest = MockUtils.aFinancingRequest();
        doReturn(financingRequest).when(financingRequestMapper).map(paymentRequest);
        final FinancingRequestResponse financingRequestResponse = MockUtils.aFinancingRequestResponse();
        doReturn(financingRequestResponse).when(circeoProxy).doPayment(financingRequest, paymentRequest.getPartnerConfiguration());

        final PaymentResponse paymentResponse = underTest.paymentRequest(paymentRequest);

        verify(requiredDataValidator).validateRequiredDataForFinancingRequest(paymentRequest.getBuyer(), paymentRequest.getOrder());

        assertNotNull(paymentResponse);
        assertTrue(paymentResponse instanceof PaymentResponseRedirect);
        final PaymentResponseRedirect paymentResponseRedirect = (PaymentResponseRedirect) paymentResponse;
        assertEquals("financingId", paymentResponseRedirect.getPartnerTransactionId());
        assertEquals(1, paymentResponseRedirect.getRequestContext().getRequestData().size());
        assertEquals("financingId", paymentResponseRedirect.getRequestContext().getRequestData().get(Constants.RequestContextKeys.FINANCING_ID));
        assertEquals(PaymentResponseRedirect.RedirectionRequest.RequestType.GET, paymentResponseRedirect.getRedirectionRequest().getRequestType());
        assertEquals(new URL("http://url.com"), paymentResponseRedirect.getRedirectionRequest().getUrl());
    }

    @Nested
    class ShouldReturnAPaymentResponseFailure {

        @Test
        void whenHttpErrorException() throws HttpErrorException {
            final PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
            final FinancingRequest financingRequest = MockUtils.aFinancingRequest();
            doReturn(financingRequest).when(financingRequestMapper).map(paymentRequest);
            final HttpErrorException exception = new HttpErrorException(null, "server error");
            doThrow(exception).when(circeoProxy).doPayment(financingRequest, paymentRequest.getPartnerConfiguration());

            final PaymentResponse paymentResponse = underTest.paymentRequest(paymentRequest);

            verify(requiredDataValidator).validateRequiredDataForFinancingRequest(paymentRequest.getBuyer(), paymentRequest.getOrder());

            assertNotNull(paymentResponse);
            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals(FailureCause.INVALID_DATA, paymentResponseFailure.getFailureCause());
            assertEquals("server error", paymentResponseFailure.getErrorCode());
        }
        @Test
        void whenPluginException() {
            final PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
            final PluginException pluginException = new PluginException("message", FailureCause.CANCEL);
            doThrow(pluginException).when(requiredDataValidator).validateRequiredDataForFinancingRequest(
                    paymentRequest.getBuyer(), paymentRequest.getOrder());

            final PaymentResponse paymentResponse = underTest.paymentRequest(paymentRequest);

            assertNotNull(paymentResponse);
            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals(FailureCause.CANCEL, paymentResponseFailure.getFailureCause());
            assertEquals("message", paymentResponseFailure.getErrorCode());
        }

        @Test
        void whenRuntimeException() {
            final PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();
            final RuntimeException pluginException = new RuntimeException("message");
            doThrow(pluginException).when(requiredDataValidator).validateRequiredDataForFinancingRequest(
                    paymentRequest.getBuyer(), paymentRequest.getOrder());

            final PaymentResponse paymentResponse = underTest.paymentRequest(paymentRequest);

            assertNotNull(paymentResponse);
            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals(FailureCause.INTERNAL_ERROR, paymentResponseFailure.getFailureCause());
            assertEquals("plugin error: RuntimeException: message", paymentResponseFailure.getErrorCode());
        }
    }
}
