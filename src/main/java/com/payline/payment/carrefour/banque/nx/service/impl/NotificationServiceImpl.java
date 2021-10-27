package com.payline.payment.carrefour.banque.nx.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.carrefour.banque.nx.bean.request.State;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.service.business.impl.StatusToResponseConverter;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.service.NotificationService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private StatusToResponseConverter statusToResponseConverter = StatusToResponseConverter.getInstance();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NotificationResponse parse(final NotificationRequest notificationRequest) {
        NotificationResponse notificationResponse;

        final String notificationContent = PluginUtils.inputStreamToString(notificationRequest.getContent());
        final FinancingRequestStatus financingRequestStatus;
        try {
            financingRequestStatus = objectMapper.readValue(notificationContent, FinancingRequestStatus.class);
            final State state = financingRequestStatus.getState();
            final String financingId = financingRequestStatus.getFinancingId();
            if (State.STARTED.equals(state)) {
                // Dans le cas où circeo envoie une notif au moment de la redirection, on ne cherche pas à créer de transaction (c'est trop tôt)
                log.info("Notification with status : {} ignored for financingId : {}", state, financingId);
                notificationResponse = IgnoreNotificationResponse.IgnoreNotificationResponseBuilder.aIgnoreNotificationResponseBuilder().build();
            } else {
                final PaymentResponse paymentResponse = statusToResponseConverter.financingStatusToPaymentResponse(financingRequestStatus);

                final TransactionCorrelationId correlationId = TransactionCorrelationId.TransactionCorrelationIdBuilder
                        .aCorrelationIdBuilder()
                        .withType(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID)
                        .withValue(financingId)
                        .build();

                notificationResponse = PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder
                        .aPaymentResponseByNotificationResponseBuilder()
                        .withPaymentResponse(paymentResponse)
                        .withTransactionCorrelationId(correlationId)
                        .build();
            }
        } catch (final JsonProcessingException | RuntimeException e) {
            log.warn("Ignoring notification because of a json parsing error for string : {}", notificationContent, e);
            notificationResponse = IgnoreNotificationResponse.IgnoreNotificationResponseBuilder.aIgnoreNotificationResponseBuilder().build();
        }
        return notificationResponse;
    }

    @Override
    public void notifyTransactionStatus(final NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // does nothing
    }
}
