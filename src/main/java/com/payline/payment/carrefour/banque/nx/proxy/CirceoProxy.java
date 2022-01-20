package com.payline.payment.carrefour.banque.nx.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payline.payment.carrefour.banque.nx.bean.request.DeliveryUpdateRequestToCapture;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequestToCancel;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.mapper.FinancingRequestCancelationMapper;
import com.payline.payment.carrefour.banque.nx.mapper.FinancingRequestDeliveryMapper;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

@Log4j2
public class CirceoProxy {

    private static class Holder {
        private static final CirceoProxy INSTANCE = new CirceoProxy();
    }
    public static CirceoProxy getInstance() {
        return Holder.INSTANCE;
    }

    CirceoProxy() {
    }

    public static final String FINANCING_REQUESTS_URL_FRAGMENT = "/financingRequests";
    public static final String CANCELATION_URL = "/cancelations";
    public static final String CAPTURE_DELIVERIES_URL = "/deliveries";

    private FinancingRequestCancelationMapper financingRequestCancelationMapper = FinancingRequestCancelationMapper.INSTANCE;
    private FinancingRequestDeliveryMapper financingRequestDeliveryMapper = FinancingRequestDeliveryMapper.INSTANCE;


    private ObjectMapper objectMapper = new ObjectMapper();
    private CirceoHttpClient circeoHttpClient = CirceoHttpClient.getInstance();

    /**
     * Effectue une demande de financement auprès de circeo
     * @param financingRequest la requête de demande de financement
     * @param partnerConfiguration les partnerConf contenant les infos nécessaires à l'appel à l'API circeo
     * @return la réponse à la demande de financement
     * @throws HttpErrorException en cas d'erreur 4XX (mauvais paramètres de requête)
     */
    public FinancingRequestResponse doPayment(final FinancingRequest financingRequest,
                                              final PartnerConfiguration partnerConfiguration) throws HttpErrorException {
        final String financingRequestJson;
        try {
            financingRequestJson = objectMapper.writeValueAsString(financingRequest);
        } catch (final JsonProcessingException e) {
            log.error(e);
            throw new PluginException("Unable to convert FinancingRequest to json", FailureCause.INTERNAL_ERROR);
        }
        final HttpEntity httpEntity = new StringEntity(financingRequestJson, ContentType.APPLICATION_JSON);

        final String baseUrl = partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CIRCEO_API_URL);
        final HttpPost httpPost = new HttpPost(baseUrl + FINANCING_REQUESTS_URL_FRAGMENT);
        httpPost.setEntity(httpEntity);

        circeoHttpClient.init(partnerConfiguration);
        return circeoHttpClient.execute(httpPost, FinancingRequestResponse.class);
    }

    /**
     * Effectue une demande d'annulation ou de remboursement du financement auprès de circeo
     * @param partnerConfiguration les partnerConf contenant les infos nécessaires à l'appel à l'API circeo
     * @return la réponse à la demande d'annulation financement
     * @throws HttpErrorException en cas d'erreur 4XX (mauvais paramètres de requête)
     */
    public CancelationResponse doCancel(final ResetRequest resetRequest, final PartnerConfiguration partnerConfiguration) throws HttpErrorException {
        final String financingRequestJson;
        final FinancingRequestToCancel financingRequest = financingRequestCancelationMapper.map(resetRequest);
        final String financingId = resetRequest.getPartnerTransactionId();
        try {
            financingRequestJson = objectMapper.writeValueAsString(financingRequest);
        } catch (final JsonProcessingException e) {
            log.error(e);
            throw new PluginException("Unable to convert FinancingRequest to json", FailureCause.INTERNAL_ERROR);
        }
        final HttpEntity httpEntity = new StringEntity(financingRequestJson, ContentType.APPLICATION_JSON);

        final String baseUrl = partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CIRCEO_API_URL);
        final HttpPost httpPost = new HttpPost(baseUrl + FINANCING_REQUESTS_URL_FRAGMENT + "/" + financingId + CANCELATION_URL);
        httpPost.setEntity(httpEntity);

        circeoHttpClient.init(partnerConfiguration);
        return circeoHttpClient.execute(httpPost, CancelationResponse.class);
    }

    /**
     * Récupère le statut du financement auprès de circeo
     * @param financingId l'id du financement
     * @param partnerConfiguration les partnerConf contenant les infos nécessaires à l'appel à l'API circeo
     * @return le statut du financement
     * @throws HttpErrorException en cas d'erreur 4XX (mauvais paramètres de requête)
     */
    public FinancingRequestStatus getStatus(final String financingId,
                                            final PartnerConfiguration partnerConfiguration) throws HttpErrorException {
        final String baseUrl = partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CIRCEO_API_URL);
        final HttpGet httpGet = new HttpGet(baseUrl + FINANCING_REQUESTS_URL_FRAGMENT + "/" + financingId);

        circeoHttpClient.init(partnerConfiguration);
        return circeoHttpClient.execute(httpGet, FinancingRequestStatus.class);
    }

    /**
     * Methode permettant d'appeler le service de capture de circeo.
     * @param captureRequest
     *      La requête Payline de capture.
     * @param partnerConfiguration
     *      Les informations de configurations du partenaire.
     * @return
     *      La réponse de circeo pour la capture.
     * @throws HttpErrorException
     */
    public DeliveryUpdateResponse doCapture(final CaptureRequest captureRequest) throws HttpErrorException {

        final String financingRequestJson;
        final PartnerConfiguration partnerConfiguration = captureRequest.getPartnerConfiguration();
        final DeliveryUpdateRequestToCapture financingRequest = financingRequestDeliveryMapper.map(captureRequest);
        final String financingId = captureRequest.getPartnerTransactionId();

        try {
            financingRequestJson = objectMapper.writeValueAsString(financingRequest);
        } catch (final JsonProcessingException e) {
            log.error(e);
            throw new PluginException("Unable to convert DeliveryUpdateRequest to json", FailureCause.INTERNAL_ERROR);
        }

        final HttpEntity httpEntity = new StringEntity(financingRequestJson, ContentType.APPLICATION_JSON);
        final String baseUrl = partnerConfiguration.getProperty(Constants.PartnerConfigurationKeys.CIRCEO_API_URL);
        final HttpPost httpPost = new HttpPost(baseUrl + FINANCING_REQUESTS_URL_FRAGMENT + "/" + financingId + CAPTURE_DELIVERIES_URL);
        httpPost.setEntity(httpEntity);

        circeoHttpClient.init(partnerConfiguration);
        return circeoHttpClient.execute(httpPost, DeliveryUpdateResponse.class);

    }

}
