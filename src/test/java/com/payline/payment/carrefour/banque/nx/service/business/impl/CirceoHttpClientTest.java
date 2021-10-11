package com.payline.payment.carrefour.banque.nx.service.business.impl;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CirceoHttpClientTest {

    @Mock
    private CloseableHttpClient client;

    @Mock
    private OAuth20Service oAuth20Service;

    @InjectMocks
    @Spy
    private CirceoHttpClient underTest;

    @Test
    void shouldInit() {
        final PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();

        underTest.init(partnerConfiguration);

        assertEquals(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CLIENT_ID), underTest.oAuth20Service.getApiKey());
        assertEquals(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CLIENT_SECRET), underTest.oAuth20Service.getApiSecret());
        assertEquals("openid", underTest.oAuth20Service.getDefaultScope());
        assertEquals(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CIRCEO_SCRIPT_URL),
                underTest.oAuth20Service.getApi().getAccessTokenEndpoint());
    }

    @Nested
    class RetrieveOAuthToken {

        @Test
        void shouldReturnOAuth2AccessToken() throws InterruptedException, ExecutionException, IOException {
            final OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken("tok");
            doReturn(oAuth2AccessToken).when(oAuth20Service).getAccessTokenClientCredentialsGrant();

            assertEquals(oAuth2AccessToken, underTest.retrieveOAuthToken());
        }

        @Test
        void shouldHandleInterruptedException() throws InterruptedException, ExecutionException, IOException {
            doThrow(new InterruptedException()).when(oAuth20Service).getAccessTokenClientCredentialsGrant();

            final PluginException pluginException = assertThrows(PluginException.class, () -> underTest.retrieveOAuthToken());

            assertEquals("unable to retrieve oauth token from circeo", pluginException.getMessage());
        }

        @Test
        void shouldHandleExecutionException() throws InterruptedException, ExecutionException, IOException {
            doThrow(new ExecutionException(new Throwable())).when(oAuth20Service).getAccessTokenClientCredentialsGrant();

            final PluginException pluginException = assertThrows(PluginException.class, () -> underTest.retrieveOAuthToken());

            assertEquals("unable to retrieve oauth token from circeo", pluginException.getMessage());
        }

        @Test
        void shouldHandleIOException() throws InterruptedException, ExecutionException, IOException {
            doThrow(new IOException()).when(oAuth20Service).getAccessTokenClientCredentialsGrant();

            final PluginException pluginException = assertThrows(PluginException.class, () -> underTest.retrieveOAuthToken());

            assertEquals("unable to retrieve oauth token from circeo", pluginException.getMessage());
        }
    }

    @Nested
    class Execute {

        @Mock(answer = Answers.RETURNS_DEEP_STUBS)
        private CloseableHttpResponse httpResponse;

        private final HttpPost httpPost = new HttpPost("http://test.com");

        @BeforeEach
        void initCommonMocks() {
            doReturn(new OAuth2AccessToken("tok")).when(underTest).retrieveOAuthToken();
            underTest.initRetries(MockUtils.aPartnerConfiguration());
        }

        @Test
        void shouldReturnResponseIfHttp200() throws HttpErrorException, IOException {
            // obligé d'utiliser when pour le deep stub
            when(httpResponse.getStatusLine().getStatusCode()).thenReturn(200);
            when(httpResponse.getEntity()).thenReturn(new StringEntity("{ \"financingId\": \"id\" }"));
            doReturn(httpResponse).when(client).execute(httpPost);

            final FinancingRequestResponse response = underTest.execute(httpPost, FinancingRequestResponse.class);

            assertNotNull(httpPost.getHeaders(OAuthConstants.HEADER));
            assertEquals(1, httpPost.getHeaders(OAuthConstants.HEADER).length);
            assertEquals("Bearer tok", httpPost.getHeaders(OAuthConstants.HEADER)[0].getValue());
            assertEquals("id", response.getFinancingId());
        }

        @Test
        void shouldThrowHttpErrorExceptionIfHttp400() throws IOException {
            // obligé d'utiliser when pour le deep stub
            when(httpResponse.getStatusLine().getStatusCode()).thenReturn(400);
            when(httpResponse.getEntity()).thenReturn(new StringEntity("{ \"code\": \"kod\", \"message\": \"msg\" }"));
            doReturn(httpResponse).when(client).execute(httpPost);

            final HttpErrorException exception = assertThrows(HttpErrorException.class,
                    () -> underTest.execute(httpPost, FinancingRequestResponse.class));

            assertEquals("kod", exception.getCode());
            assertEquals("msg", exception.getMessage());
        }

        @Test
        void shouldThrowPluginExceptionIfHttp500() throws IOException {
            // obligé d'utiliser when pour le deep stub
            when(httpResponse.getStatusLine().getStatusCode()).thenReturn(500);
            when(httpResponse.getEntity()).thenReturn(new StringEntity("{ \"error\": \"unknown\" }"));
            doReturn(httpResponse).when(client).execute(httpPost);

            final PluginException exception = assertThrows(PluginException.class,
                    () -> underTest.execute(httpPost, FinancingRequestResponse.class));

            assertEquals("An error occurred during the HTTP call", exception.getMessage());
        }

        @Test
        void shouldThrowPluginExceptionIfIoError() throws IOException {
            doThrow(new IOException()).when(client).execute(httpPost);

            final PluginException exception = assertThrows(PluginException.class,
                    () -> underTest.execute(httpPost, FinancingRequestResponse.class));

            // 2 retry
            verify(client, times(4)).execute(httpPost);

            assertEquals("Failed to contact the partner API", exception.getMessage());
            assertEquals(FailureCause.COMMUNICATION_ERROR, exception.getFailureCause());
        }
    }
}