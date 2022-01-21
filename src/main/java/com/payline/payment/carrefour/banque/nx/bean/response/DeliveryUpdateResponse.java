package com.payline.payment.carrefour.banque.nx.bean.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class DeliveryUpdateResponse {
    String financingId;
    String orderId;
    DeliveryUpdateRequestState deliveryRequestState;
    String reason;
}
