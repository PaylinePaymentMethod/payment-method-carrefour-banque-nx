package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {
    @Override
    public NotificationResponse parse(final NotificationRequest notificationRequest) {
        return null;
    }

    @Override
    public void notifyTransactionStatus(final NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // to implement
    }
}
