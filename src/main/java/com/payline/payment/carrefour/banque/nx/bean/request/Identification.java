package com.payline.payment.carrefour.banque.nx.bean.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Identification {
    Title title;
    String surname;
    String firstname;
    String birthName;
    String dateOfBirth;
    String placeOfBirth;
    String placeOfBirthCode;
    String placeOfBirthCountry;
}
