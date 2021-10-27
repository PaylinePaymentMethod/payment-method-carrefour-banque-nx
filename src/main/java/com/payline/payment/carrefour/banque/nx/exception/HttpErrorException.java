package com.payline.payment.carrefour.banque.nx.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Generated
public class HttpErrorException extends Exception {
    String code;
    String message;
}
