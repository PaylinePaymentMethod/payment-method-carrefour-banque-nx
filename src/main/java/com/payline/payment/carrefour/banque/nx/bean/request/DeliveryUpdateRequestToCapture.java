package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class DeliveryUpdateRequestToCapture {
    HeaderSimple header;
    DeliveryUpdateRequest request;
}
