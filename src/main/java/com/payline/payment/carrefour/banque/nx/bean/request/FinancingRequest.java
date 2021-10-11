package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FinancingRequest {
    Header header;
    Request request;
    Customer customer;
}
