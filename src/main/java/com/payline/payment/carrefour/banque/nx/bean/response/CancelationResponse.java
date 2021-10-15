package com.payline.payment.carrefour.banque.nx.bean.response;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CancelationResponse {
    String financingId;
    String orderId;
    CancelationRequestState cancelationRequestState;
    String reason;
}
