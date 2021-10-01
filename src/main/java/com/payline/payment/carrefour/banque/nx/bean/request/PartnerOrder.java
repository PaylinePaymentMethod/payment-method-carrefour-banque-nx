package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PartnerOrder {
    String orderId;
    String orderDateTime;
    Channel channel;
    Cart cart;
}
