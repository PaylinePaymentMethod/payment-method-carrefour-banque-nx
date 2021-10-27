package com.payline.payment.carrefour.banque.nx.service.business.impl;

import com.payline.payment.carrefour.banque.nx.bean.request.State;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusToResponseConverterTest {

    private final StatusToResponseConverter underTest = StatusToResponseConverter.getInstance();

    @Nested
    class FinancingStatusToPaymentResponse {

        @ParameterizedTest
        @EnumSource(value = State.class, names = {"AUTHORISED", "FINANCED", "ACCEPTED"})
        void shouldConvertIntoPaymentSuccess(final State state) {
            final FinancingRequestStatus financingRequestStatus = FinancingRequestStatus.builder()
                    .financingId("financingId")
                    .state(state)
                    .build();

            final PaymentResponse paymentResponse = underTest.financingStatusToPaymentResponse(financingRequestStatus);

            assertTrue(paymentResponse instanceof PaymentResponseSuccess);
            final PaymentResponseSuccess paymentResponseSuccess = (PaymentResponseSuccess) paymentResponse;
            assertEquals("financingId", paymentResponseSuccess.getPartnerTransactionId());
            assertEquals(state.name(), paymentResponseSuccess.getStatusCode());
            assertTrue(paymentResponseSuccess.getTransactionDetails() instanceof EmptyTransactionDetails);
        }

        @ParameterizedTest
        @EnumSource(value = State.class, names = {"CANCELED"})
        void shouldConvertIntoPaymentResponseFailureSessionExpired(final State state) {
            final FinancingRequestStatus financingRequestStatus = FinancingRequestStatus.builder()
                    .financingId("financingId")
                    .state(state)
                    .build();

            final PaymentResponse paymentResponse = underTest.financingStatusToPaymentResponse(financingRequestStatus);

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals("financingId", paymentResponseFailure.getPartnerTransactionId());
            assertEquals(state.name(), paymentResponseFailure.getErrorCode());
            assertEquals(FailureCause.SESSION_EXPIRED, paymentResponseFailure.getFailureCause());
        }

        @ParameterizedTest
        @EnumSource(value = State.class, names = {"CANCELED_BY_CLIENT"})
        void shouldConvertIntoPaymentResponseFailureCancel(final State state) {
            final FinancingRequestStatus financingRequestStatus = FinancingRequestStatus.builder()
                    .financingId("financingId")
                    .state(state)
                    .build();

            final PaymentResponse paymentResponse = underTest.financingStatusToPaymentResponse(financingRequestStatus);

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals("financingId", paymentResponseFailure.getPartnerTransactionId());
            assertEquals(state.name(), paymentResponseFailure.getErrorCode());
            assertEquals(FailureCause.CANCEL, paymentResponseFailure.getFailureCause());
        }

        @ParameterizedTest
        @EnumSource(value = State.class, names = {"FAILED_AUTHENTICATION", "REJECTED", "REFUSED_AUTHORISATION"})
        void shouldConvertIntoPaymentResponseFailureRefused(final State state) {
            final FinancingRequestStatus financingRequestStatus = FinancingRequestStatus.builder()
                    .financingId("financingId")
                    .state(state)
                    .build();

            final PaymentResponse paymentResponse = underTest.financingStatusToPaymentResponse(financingRequestStatus);

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals("financingId", paymentResponseFailure.getPartnerTransactionId());
            assertEquals(state.name(), paymentResponseFailure.getErrorCode());
            assertEquals(FailureCause.REFUSED, paymentResponseFailure.getFailureCause());
        }

        @ParameterizedTest
        @EnumSource(value = State.class, names = {"STARTED", "WAITING_FOR_CAPTURE"})
        void shouldConvertIntoPaymentResponseFailurePartnerUnknownError(final State state) {
            final FinancingRequestStatus financingRequestStatus = FinancingRequestStatus.builder()
                    .financingId("financingId")
                    .state(state)
                    .build();

            final PaymentResponse paymentResponse = underTest.financingStatusToPaymentResponse(financingRequestStatus);

            assertTrue(paymentResponse instanceof PaymentResponseFailure);
            final PaymentResponseFailure paymentResponseFailure = (PaymentResponseFailure) paymentResponse;
            assertEquals("financingId", paymentResponseFailure.getPartnerTransactionId());
            assertEquals(state.name(), paymentResponseFailure.getErrorCode());
            assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, paymentResponseFailure.getFailureCause());
        }
    }
}