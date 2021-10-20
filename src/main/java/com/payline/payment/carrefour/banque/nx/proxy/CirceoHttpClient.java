package com.payline.payment.carrefour.banque.nx.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class CirceoHttpClient {

    private static class Holder {
        private static final CirceoHttpClient INSTANCE = new CirceoHttpClient();
    }

    public static CirceoHttpClient getInstance() {
        return Holder.INSTANCE;
    }

    CirceoHttpClient() {
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CloseableHttpClient client;
    protected OAuth20Service oAuth20Service;

    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    /**
     * Has this class been initialized with partner configuration ?
     */
    protected AtomicBoolean initialized = new AtomicBoolean();

    /**
     * Configuration du client HTTP
     * @param partnerConfiguration la configuration
     */
    public void init(final PartnerConfiguration partnerConfiguration) {
        if (initialized.compareAndSet(false, true)) {
            // Retrieve config properties
            final int connectionRequestTimeout;
            final int connectTimeout;
            final int socketTimeout;

            try {
                // request config timeouts (in seconds)
                connectionRequestTimeout = Integer.parseInt(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.HTTP_CONNECTION_REQUEST_TIMEOUT));
                connectTimeout = Integer.parseInt(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.HTTP_CONNECT_TIMEOUT));
                socketTimeout = Integer.parseInt(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.HTTP_SOCKET_TIMEOUT));
                initRetries(partnerConfiguration);
            } catch (final NumberFormatException e) {
                throw new PluginException("plugin error: http.* properties must be integers", e);
            }

            // Create RequestConfig
            final RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                    .setConnectTimeout(connectTimeout * 1000)
                    .setSocketTimeout(socketTimeout * 1000)
                    .build();

            // Instantiate Apache HTTP client
            client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(requestConfig)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(),
                            SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                    .build();

            oAuth20Service = new ServiceBuilder(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CLIENT_ID))
                    .apiSecret(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CLIENT_SECRET))
                    .defaultScope("openid")
                    .build(new DefaultApi20() {
                        @Override
                        public String getAccessTokenEndpoint() {
                            return partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CIRCEO_SCRIPT_URL);
                        }

                        @Override
                        protected String getAuthorizationBaseUrl() {
                            return null;
                        }
                    });
        }
    }

    protected void initRetries(final PartnerConfiguration partnerConfiguration) {
        // number of retry attempts
        retries = Integer.parseInt(partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.HTTP_RETRIES));
    }

    /**
     * Récupère un token oauth valide auprès de circeo
     * @return le token
     */
    protected OAuth2AccessToken retrieveOAuthToken() {
        try {
            return oAuth20Service.getAccessTokenClientCredentialsGrant();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PluginException("unable to retrieve oauth token from circeo", e);
        } catch (final ExecutionException | IOException e) {
            throw new PluginException("unable to retrieve oauth token from circeo", e);
        }
    }

    /**
     * Effectue une requête HTTP avec authentification oauth
     * @param httpRequest la requête http
     * @param responseType le type de réponse attendu
     * @param <T> le type de réponse attendu
     * @return une réponse de type responseType si tout s'est bien passé
     * @throws HttpErrorException en cas d'erreur 4XX
     */
    public  <T> T execute(final HttpRequestBase httpRequest, final Class<T> responseType) throws HttpErrorException {
        T response = null;

        final OAuth2AccessToken token = retrieveOAuthToken();
        httpRequest.addHeader(OAuthConstants.HEADER, "Bearer " + token.getAccessToken());

        for (int attempts = 1; attempts < retries + 1; attempts++) {
            log.info("Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts);
            try (CloseableHttpResponse httpResponse = client.execute(httpRequest)) {
                log.info("Response obtained from partner API [{} {}]", httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                final String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
                if (httpResponse.getStatusLine().getStatusCode() < 400) {
                    // cas nominal, réponse en dessous de 400
                    response = objectMapper.readValue(jsonResponse, responseType);
                    break;
                } else if (httpResponse.getStatusLine().getStatusCode() < 500) {
                    // erreur sur la requête (en dessous de 500)
                    final HttpErrorException httpErrorException = objectMapper.readValue(jsonResponse, HttpErrorException.class);
                    log.error("Error: code = {}, message = {}", httpErrorException.getCode(), httpErrorException.getMessage());
                    throw httpErrorException;
                } else {
                    // erreur 500
                    log.error("Error: code = {}, response = {}", httpResponse.getStatusLine().getStatusCode(), jsonResponse);
                    throw new PluginException("An error occurred during the HTTP call");
                }
            } catch (final IOException e) {
                log.error("An error occurred during the HTTP call :", e);
            }
        }
        if (response == null) {
            throw new PluginException("Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR);
        }
        return response;
    }
}
