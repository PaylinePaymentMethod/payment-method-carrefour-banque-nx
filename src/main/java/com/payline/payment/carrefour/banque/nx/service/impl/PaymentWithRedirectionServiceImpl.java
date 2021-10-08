package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.exception.InvalidDataException;
import com.payline.payment.carrefour.banque.nx.exception.PluginException;
import com.payline.payment.carrefour.banque.nx.service.business.impl.CirceoPaymentService;
import com.payline.payment.carrefour.banque.nx.service.business.impl.StatusToResponseConverter;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private CirceoPaymentService circeoPaymentService = CirceoPaymentService.getInstance();
    private StatusToResponseConverter statusToResponseConverter = StatusToResponseConverter.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(final RedirectionPaymentRequest redirectionPaymentRequest) {
        final String financingId = redirectionPaymentRequest.getRequestContext().getRequestData().get(Constants.RequestContextKeys.FINANCING_ID);
        return retrieveTransactionStatus(financingId, redirectionPaymentRequest.getPartnerConfiguration());
    }

    @Override
    public PaymentResponse handleSessionExpired(final TransactionStatusRequest transactionStatusRequest) {
        return retrieveTransactionStatus(transactionStatusRequest.getTransactionId(), transactionStatusRequest.getPartnerConfiguration());
    }

    protected PaymentResponse retrieveTransactionStatus(final String financingId, final PartnerConfiguration partnerConfiguration) {
        PaymentResponse paymentResponse;
        try {
            if (financingId == null) {
                throw new InvalidDataException("Missing financing ID from request context");
            }
            final FinancingRequestStatus financingRequestStatus = circeoPaymentService.getStatus(financingId, partnerConfiguration);
            paymentResponse = statusToResponseConverter.financingStatusToPaymentResponse(financingRequestStatus);
        } catch (final HttpErrorException e) {
            log.error(e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withErrorCode(e.getMessage())
                    .withPartnerTransactionId(financingId)
                    .withFailureCause(FailureCause.COMMUNICATION_ERROR)
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
