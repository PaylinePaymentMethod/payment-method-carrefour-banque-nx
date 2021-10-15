package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FinancingRequestToCancel {
    HeaderSimple header;
    CancelationRequest request;
}
