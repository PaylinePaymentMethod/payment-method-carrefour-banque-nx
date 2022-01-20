package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class DeliveryUpdateRequest {

    Map<String, Object> misc;
    double deliveryAmount;
    DeliveryState deliveryState;
}
