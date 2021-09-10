package com.payline.payment.carrefour.banque.nx.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service;


    @BeforeEach
    void setup() {
        service = new PaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

}
