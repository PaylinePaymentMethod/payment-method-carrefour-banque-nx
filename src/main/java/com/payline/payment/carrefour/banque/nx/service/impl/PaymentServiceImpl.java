package com.payline.payment.carrefour.banque.nx.service.impl;


import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.mapper.FinancingRequestMapper;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.payment.carrefour.banque.nx.service.business.impl.RequiredDataValidator;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.service.PaymentService;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private RequiredDataValidator requiredDataValidator = RequiredDataValidator.getInstance();
    private CirceoProxy circeoProxy = CirceoProxy.getInstance();
    private FinancingRequestMapper financingRequestMapper = FinancingRequestMapper.INSTANCE;

    @Override
    public PaymentResponse paymentRequest(final PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse;
        try {
            requiredDataValidator.validateRequiredDataForFinancingRequest(paymentRequest.getBuyer(), paymentRequest.getOrder());
            final FinancingRequest financingRequest = financingRequestMapper.map(paymentRequest);
            final FinancingRequestResponse financingRequestResponse = circeoProxy.doPayment(financingRequest,
                    paymentRequest.getPartnerConfiguration());
            final String partnerTransactionId = financingRequestResponse.getFinancingId();

            final Map<String, String> requestData = new HashMap<>();
            requestData.put(Constants.RequestContextKeys.FINANCING_ID, partnerTransactionId);

            paymentResponse = PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withRequestContext(RequestContext.RequestContextBuilder.aRequestContext()
                            .withRequestData(requestData)
                            .build())
                    .withRedirectionRequest(PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder.aRedirectionRequest()
                            .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                            .withUrl(new URL(financingRequestResponse.getRedirectionUrl()))
                            .build())
                    .build();
        } catch (final HttpErrorException e) {
            log.error(e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withErrorCode(e.getMessage())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        } catch (final MalformedURLException e) {
            log.error(e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withErrorCode("Received invalid URL from partner")
                    .withFailureCause(FailureCause.INVALID_FIELD_FORMAT)
                    .build();
        } catch (final PluginException e) {
            log.error(e.getErrorCode(), e);
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        } catch (final RuntimeException e) {
            log.error("Unexpected plugin error", e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
        return paymentResponse;
    }
}
