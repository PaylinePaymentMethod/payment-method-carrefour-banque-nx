package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PartnerAddress {
    String streetNumberAndNameLine1;
    String streetNameLine2;
    String townName;
    String postalCode;
    String country;
}
