package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class Request {
    String offerId;
    int duration;
    CaptureTime captureTime;
    PartnerOrder order;
    Map<String, Object> miscData;
}
