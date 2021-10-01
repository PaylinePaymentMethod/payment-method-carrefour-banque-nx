package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Customer {
    String customerId;
    Identification identification;
    AccountData accountData;
    ContactData contactData;
    PartnerAddress billingAddress;
    PartnerAddress shippingAddress;
}
