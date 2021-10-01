package com.payline.payment.carrefour.banque.nx.service.business.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CirceoPaymentServiceTest {

    @Captor
    private ArgumentCaptor<HttpPost> httpPostArgumentCaptor;

    @Mock
    private CirceoHttpClient circeoHttpClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CirceoPaymentService underTest;

    @Nested
    class DoPayment {

        @Test
        void shouldReturnFinancingRequestResponse() throws HttpErrorException, IOException {
            final FinancingRequest financingRequest = FinancingRequest.builder().build();
            final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
            doReturn("{}").when(objectMapper).writeValueAsString(financingRequest);
            final FinancingRequestResponse financingRequestResponse = MockUtils.aFinancingRequestResponse();
            doReturn(financingRequestResponse).when(circeoHttpClient).execute(httpPostArgumentCaptor.capture(), eq(FinancingRequestResponse.class));

            final FinancingRequestResponse response = underTest.doPayment(financingRequest, partnerConfiguration);

            verify(circeoHttpClient).init(partnerConfiguration);
            assertEquals(financingRequestResponse, response);

            final HttpPost httpPost = httpPostArgumentCaptor.getValue();
            assertNotNull(httpPost);
            assertEquals("https://recette.theloanfactory.carrefour-banque.fr/integration/service/financingRequests",
                    httpPost.getURI().toString());
            assertEquals(ContentType.APPLICATION_JSON.toString(), httpPost.getEntity().getContentType().getValue());
            assertEquals("{}", PluginUtils.inputStreamToString(httpPost.getEntity().getContent()));
        }

        @Test
        void shouldHandleJsonProcessingException() throws JsonProcessingException {
            final FinancingRequest financingRequest = FinancingRequest.builder().build();
            doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(financingRequest);

            final PluginException pluginException = assertThrows(PluginException.class,
                    () -> underTest.doPayment(financingRequest, MockUtils.aPartnerConfiguration()));

            assertEquals("Unable to convert FinancingRequest to json", pluginException.getMessage());
            assertEquals(FailureCause.INTERNAL_ERROR, pluginException.getFailureCause());
        }
    }
}
