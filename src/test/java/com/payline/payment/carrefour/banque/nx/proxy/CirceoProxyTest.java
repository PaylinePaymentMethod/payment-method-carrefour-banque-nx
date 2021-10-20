package com.payline.payment.carrefour.banque.nx.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequestToCancel;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.mapper.FinancingRequestCancelationMapper;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoHttpClient;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.apache.http.client.methods.HttpGet;
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
class CirceoProxyTest {

    @Captor
    private ArgumentCaptor<HttpPost> httpPostArgumentCaptor;

    @Captor
    private ArgumentCaptor<HttpGet> httpGetArgumentCaptor;

    @Mock
    private CirceoHttpClient circeoHttpClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FinancingRequestCancelationMapper financingRequestCancelationMapper ;

    @InjectMocks
    private CirceoProxy underTest;

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

    @Nested
    class doCancel {

        @Test
        void shouldReturnFinancingRequestResponse() throws HttpErrorException, IOException {
            final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
            final FinancingRequestToCancel financingRequest = MockUtils.aFinancingRequestToCancel();
            final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
            final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
            //Mock
            doReturn("{}").when(objectMapper).writeValueAsString(financingRequest);
            doReturn(financingRequest).when(financingRequestCancelationMapper).map(resetRequest);
            doReturn(cancelationResponse).when(circeoHttpClient).execute(httpPostArgumentCaptor.capture(), eq(CancelationResponse.class));
            //Test
            final CancelationResponse response = underTest.doCancel(resetRequest, partnerConfiguration);

            verify(circeoHttpClient).init(partnerConfiguration);
            assertEquals(cancelationResponse, response);

            final HttpPost httpPost = httpPostArgumentCaptor.getValue();
            assertNotNull(httpPost);
            assertEquals("https://recette.theloanfactory.carrefour-banque.fr/integration/service/financingRequests/C0000004/cancelations",
                    httpPost.getURI().toString());
            assertEquals(ContentType.APPLICATION_JSON.toString(), httpPost.getEntity().getContentType().getValue());
            assertEquals("{}", PluginUtils.inputStreamToString(httpPost.getEntity().getContent()));
        }

        @Test
        void shouldHandleJsonProcessingException() throws JsonProcessingException {
            final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
            final FinancingRequestToCancel financingRequest = MockUtils.aFinancingRequestToCancel();
            doReturn(financingRequest).when(financingRequestCancelationMapper).map(resetRequest);
            doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(financingRequest);

            final PluginException pluginException = assertThrows(PluginException.class,
                    () -> underTest.doCancel(resetRequest, MockUtils.aPartnerConfiguration()));

            assertEquals("Unable to convert FinancingRequest to json", pluginException.getMessage());
            assertEquals(FailureCause.INTERNAL_ERROR, pluginException.getFailureCause());
        }
    }

    @Test
    void shouldGetFinancingRequestStatus() throws HttpErrorException {
        final String financingId = "financingId";
        final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
        final FinancingRequestStatus financingRequestStatus = MockUtils.aFinancingRequestStatus();
        doReturn(financingRequestStatus).when(circeoHttpClient).execute(httpGetArgumentCaptor.capture(), eq(FinancingRequestStatus.class));

        final FinancingRequestStatus response = underTest.getStatus(financingId, partnerConfiguration);

        verify(circeoHttpClient).init(partnerConfiguration);
        assertEquals(financingRequestStatus, response);

        final HttpGet httpGet = httpGetArgumentCaptor.getValue();
        assertNotNull(httpGet);
        assertEquals("https://recette.theloanfactory.carrefour-banque.fr/integration/service/financingRequests/financingId",
                httpGet.getURI().toString());
    }

    @Test
    void shouldGetCancelationRequestState() throws HttpErrorException, JsonProcessingException {
        final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
        final FinancingRequestToCancel financingRequest = MockUtils.aFinancingRequestToCancel();
        final PartnerConfiguration partnerConfiguration = resetRequest.getPartnerConfiguration();
        final CancelationResponse cancelationResponse = MockUtils.aCancelationSucessResponse();
        //Mock
        doReturn(financingRequest).when(financingRequestCancelationMapper).map(resetRequest);
        doReturn("{}").when(objectMapper).writeValueAsString(financingRequest);
        doReturn(cancelationResponse).when(circeoHttpClient).execute(httpPostArgumentCaptor.capture(), eq(CancelationResponse.class));
        //Test
        final CancelationResponse response = underTest.doCancel(resetRequest, partnerConfiguration);

        assertEquals(CancelationRequestState.DONE, response.getCancelationRequestState());
    }
}