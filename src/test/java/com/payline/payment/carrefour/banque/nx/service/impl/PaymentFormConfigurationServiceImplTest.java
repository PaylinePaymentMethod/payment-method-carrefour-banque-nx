package com.payline.payment.carrefour.banque.nx.service.impl;


import com.payline.payment.carrefour.banque.nx.utils.i18n.I18nService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

class PaymentFormConfigurationServiceImplTest {

    @Mock
    private I18nService i18n;

    @InjectMocks
    private PaymentFormConfigurationServiceImpl underTest;

   
    @BeforeEach
    void setup() {
        underTest = new PaymentFormConfigurationServiceImpl();
        MockitoAnnotations.initMocks(this);

        // We consider by default that i18n behaves normally
        doReturn("message")
                .when(i18n)
                .getMessage(anyString(), any(Locale.class));
    }
}

