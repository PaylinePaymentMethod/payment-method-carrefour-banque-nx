package com.payline.payment.carrefour.banque.nx.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.State;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.service.business.impl.StatusToResponseConverter;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private StatusToResponseConverter statusToResponseConverter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationServiceImpl underTest;

    @Nested
    class Parse {

        @Test
        void shouldReturnNotificationResponse() throws JsonProcessingException {
            final NotificationRequest notificationRequest = MockUtils.aPaylineNotificationRequest("body");
            final PaymentResponseSuccess paymentResponseSuccess = MockUtils.aPaymentResponseSuccess();
            final FinancingRequestStatus financingRequestStatus = MockUtils.aFinancingRequestStatus();

            doReturn(financingRequestStatus).when(objectMapper).readValue("body", FinancingRequestStatus.class);
            doReturn(paymentResponseSuccess).when(statusToResponseConverter).financingStatusToPaymentResponse(financingRequestStatus);

            final NotificationResponse response = underTest.parse(notificationRequest);

            assertTrue(response instanceof PaymentResponseByNotificationResponse);
            final PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
            assertEquals(paymentResponseSuccess, paymentResponseByNotificationResponse.getPaymentResponse());
            final TransactionCorrelationId transactionCorrelationId = paymentResponseByNotificationResponse.getTransactionCorrelationId();
            assertEquals(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID, transactionCorrelationId.getType());
            assertEquals(financingRequestStatus.getFinancingId(), transactionCorrelationId.getValue());
        }

        @ParameterizedTest
        @EnumSource(value = State.class, names = {"STARTED", "ACCEPTED"})
        void shouldIgnoreStatus(final State ignoredState) throws JsonProcessingException {
            final NotificationRequest notificationRequest = MockUtils.aPaylineNotificationRequest("body");
            final FinancingRequestStatus financingRequestStatus = MockUtils.aFinancingRequestStatus().toBuilder().state(ignoredState).build();

            doReturn(financingRequestStatus).when(objectMapper).readValue("body", FinancingRequestStatus.class);

            final NotificationResponse response = underTest.parse(notificationRequest);

            assertTrue(response instanceof IgnoreNotificationResponse);
        }

        @Test
        void shouldIgnoreOnJsonProcessingException() throws JsonProcessingException {
            doThrow(JsonProcessingException.class).when(objectMapper).readValue("body", FinancingRequestStatus.class);

            final NotificationResponse response = underTest.parse(MockUtils.aPaylineNotificationRequest("body"));

            assertTrue(response instanceof IgnoreNotificationResponse);
        }

        @Test
        void shouldIgnoreOnRuntimeException() throws JsonProcessingException {
            doThrow(RuntimeException.class).when(objectMapper).readValue("body", FinancingRequestStatus.class);

            final NotificationResponse response = underTest.parse(MockUtils.aPaylineNotificationRequest("body"));

            assertTrue(response instanceof IgnoreNotificationResponse);
        }
    }
}