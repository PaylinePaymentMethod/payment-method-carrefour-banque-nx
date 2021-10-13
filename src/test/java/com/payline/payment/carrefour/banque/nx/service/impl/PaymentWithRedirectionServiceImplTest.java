package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.service.business.impl.CirceoPaymentService;
import com.payline.payment.carrefour.banque.nx.service.business.impl.StatusToResponseConverter;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PaymentWithRedirectionServiceImplTest {

    @Mock
    private CirceoPaymentService circeoPaymentService;

    @Mock
    private StatusToResponseConverter statusToResponseConverter;

    @InjectMocks
    @Spy
    private PaymentWithRedirectionServiceImpl underTest;

    @Test
    void shouldFinalizeRedirectionPayment() {
        final PaymentResponseSuccess paymentResponseSuccess = MockUtils.aPaymentResponseSuccess();
        final RedirectionPaymentRequest redirectionPaymentRequest = MockUtils.aRedirectionPaymentRequest();
        doReturn(paymentResponseSuccess).when(underTest).retrieveTransactionStatus("financingId",
                redirectionPaymentRequest.getPartnerConfiguration());

        final PaymentResponse paymentResponse = underTest.finalizeRedirectionPayment(redirectionPaymentRequest);

        assertEquals(paymentResponseSuccess, paymentResponse);
    }

    @Test
    void shouldHandleSessionExpired() {
        final PaymentResponseSuccess paymentResponseSuccess = MockUtils.aPaymentResponseSuccess();
        final TransactionStatusRequest transactionStatusRequest = MockUtils.aTransactionStatusRequest();
        doReturn(paymentResponseSuccess).when(underTest).retrieveTransactionStatus("financingId",
                transactionStatusRequest.getPartnerConfiguration());

        final PaymentResponse paymentResponse = underTest.handleSessionExpired(transactionStatusRequest);

        assertEquals(paymentResponseSuccess, paymentResponse);
    }

    @Nested
    class RetrieveTransactionStatus {

        @Test
        void shouldReturnPaymentResponse() throws HttpErrorException {
            final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
            final FinancingRequestStatus financingRequestStatus = MockUtils.aFinancingRequestStatus();
            final PaymentResponseSuccess paymentResponseSuccess = MockUtils.aPaymentResponseSuccess();

            doReturn(financingRequestStatus).when(circeoPaymentService).getStatus("financingId", partnerConfiguration);
            doReturn(paymentResponseSuccess).when(statusToResponseConverter).financingStatusToPaymentResponse(financingRequestStatus);

            final PaymentResponse paymentResponse = underTest.retrieveTransactionStatus("financingId", partnerConfiguration);

            assertEquals(paymentResponseSuccess, paymentResponse);
        }

        @Test
        void shouldReturnPaymentResponseFailureIfFinancingIdNull() {
            final PaymentResponse paymentResponse = underTest.retrieveTransactionStatus(null, MockUtils.aPartnerConfiguration());

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;

            assertEquals(FailureCause.INVALID_DATA, paymentResponseFailure.getFailureCause());
            assertEquals("Missing financing ID from request context", paymentResponseFailure.getErrorCode());
        }

        @Test
        void shouldReturnPaymentResponseFailureOnHttpErrorException() throws HttpErrorException {
            final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
            doThrow(new HttpErrorException("code", "erreur")).when(circeoPaymentService).getStatus("financingId", partnerConfiguration);

            final PaymentResponse paymentResponse = underTest.retrieveTransactionStatus("financingId", partnerConfiguration);

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;

            assertEquals(FailureCause.COMMUNICATION_ERROR, paymentResponseFailure.getFailureCause());
            assertEquals("financingId", paymentResponseFailure.getPartnerTransactionId());
            assertEquals("erreur", paymentResponseFailure.getErrorCode());
        }

        @Test
        void shouldReturnPaymentResponseFailureOnRuntimeException() throws HttpErrorException {
            final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
            doThrow(new RuntimeException("erreur")).when(circeoPaymentService).getStatus("financingId", partnerConfiguration);

            final PaymentResponse paymentResponse = underTest.retrieveTransactionStatus("financingId", partnerConfiguration);

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;

            assertEquals(FailureCause.INTERNAL_ERROR, paymentResponseFailure.getFailureCause());
            assertEquals("plugin error: RuntimeException: erreur", paymentResponseFailure.getErrorCode());
        }
    }
}