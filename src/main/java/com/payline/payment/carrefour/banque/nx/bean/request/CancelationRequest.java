package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class CancelationRequest {
    String orderId;
    CancelationType cancelationType;
    float newFinancedAmount;
    float canceledAmount;
    String currency;
    CancelationCart cart;
    Map<String, Object> miscData;
}
