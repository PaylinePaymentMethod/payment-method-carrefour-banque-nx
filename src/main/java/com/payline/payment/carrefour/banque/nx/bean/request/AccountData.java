package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class AccountData {
    String creationDateTime;
    boolean hadPreviousOrdersInLast2Years;
    PreviousOrders previousOrders;
    int canceledOrderCountInLast2Years;
    int paymentIncidentCountInLast2Years;
    boolean changedShippingAddressToday;
    PartnerAddress previousShippingAddress;
    boolean changedPhoneNumberToday;
    String previousMobileNumber;
}
