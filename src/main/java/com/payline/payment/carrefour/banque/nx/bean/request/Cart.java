package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class Cart {
    String totalFinancedAmount;
    String currency;
    DeliveryMode deliveryMode;
    String ipV4;
    String ipV6;
    List<CartItem> cartItems;
}
