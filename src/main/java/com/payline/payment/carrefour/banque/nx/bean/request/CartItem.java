package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CartItem {
    String totalPrice;
    String unitPrice;
    String currency;
    String quantity;
    String universe;
    String category;
    String brand;
    String model;
    String reference;
    String description;
    String pictureUri;
}
