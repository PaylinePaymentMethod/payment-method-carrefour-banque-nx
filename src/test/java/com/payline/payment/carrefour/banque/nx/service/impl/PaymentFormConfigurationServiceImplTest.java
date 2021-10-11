package com.payline.payment.carrefour.banque.nx.service.impl;


import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PaymentFormConfigurationServiceImplTest {

    @Mock
    private I18nService i18n;

    @InjectMocks
    private PaymentFormConfigurationServiceImpl underTest;

    @Test
    void shouldReturnPaymentFormConfigurationResponseSpecific() {
        final PaymentFormConfigurationRequest paymentFormConfigurationRequest = MockUtils.aPaymentFormConfigurationRequest();
        doReturn("button").when(i18n).getMessage(PaymentFormConfigurationServiceImpl.BUTTON_TEXT,
                paymentFormConfigurationRequest.getLocale());

        final PaymentFormConfigurationResponse paymentFormConfiguration = underTest.getPaymentFormConfiguration(paymentFormConfigurationRequest);

        assertTrue(paymentFormConfiguration instanceof PaymentFormConfigurationResponseSpecific);
        final PaymentFormConfigurationResponseSpecific pfcSpecific = (PaymentFormConfigurationResponseSpecific) paymentFormConfiguration;
        assertTrue(pfcSpecific.getPaymentForm() instanceof NoFieldForm);
        final NoFieldForm paymentForm = (NoFieldForm) pfcSpecific.getPaymentForm();
        assertEquals("", paymentForm.getDescription());
        assertTrue(paymentForm.isDisplayButton());
        assertEquals("button", paymentForm.getButtonText());
    }
}
