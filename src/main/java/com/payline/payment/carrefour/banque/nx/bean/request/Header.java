package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Header {
    String merchantId;
    String messageId;
    String creationDateTime;
    String redirectPaymentSuccessUrl;
    String redirectPaymentFailureUrl;
    String redirectPaymentCancelUrl;
    String callbackUrl;
}
