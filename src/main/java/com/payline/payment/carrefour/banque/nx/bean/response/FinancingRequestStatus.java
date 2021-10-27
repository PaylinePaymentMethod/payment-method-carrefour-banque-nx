package com.payline.payment.carrefour.banque.nx.bean.response;

import com.payline.payment.carrefour.banque.nx.bean.request.State;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FinancingRequestStatus {
    String financingId;
    String orderId;
    State state;
}
