package com.payline.payment.carrefour.banque.nx.it;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthenticationTestIT {

    @BeforeEach
    void beforeAll() {
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "5000");
    }

    @Test
    void financingRequest() throws IOException, HttpErrorException, URISyntaxException {
        final URL url = this.getClass().getClassLoader().getResource("requests/financingRequest.json");
        final Path resPath = Paths.get(url.toURI());
        final String json = new String(Files.readAllBytes(resPath), StandardCharsets.UTF_8);

        final HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);

        final HttpPost httpPost = new HttpPost("https://recette.theloanfactory.carrefour-banque.fr/integration/service/financingRequests");
        httpPost.setEntity(httpEntity);

        CirceoHttpClient.getInstance().init(MockUtils.aPartnerConfiguration());
        final FinancingRequestResponse financingRequestResponse = CirceoHttpClient.getInstance().execute(httpPost, FinancingRequestResponse.class);
        assertNotNull(financingRequestResponse);
    }

    @Test
    void deliveries() throws IOException, HttpErrorException, URISyntaxException {
        final URL url = this.getClass().getClassLoader().getResource("requests/deliveries.json");
        final Path resPath = Paths.get(url.toURI());
        final String json = new String(Files.readAllBytes(resPath), StandardCharsets.UTF_8);

        final HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);

        final HttpPost httpPost = new HttpPost("https://recette.theloanfactory.carrefour-banque.fr/integration/service/financingRequests/123/deliveries");
        httpPost.setEntity(httpEntity);

        CirceoHttpClient.getInstance().init(null);
        final String test = CirceoHttpClient.getInstance().execute(httpPost, String.class);
        assertNotNull(test);
    }
}
