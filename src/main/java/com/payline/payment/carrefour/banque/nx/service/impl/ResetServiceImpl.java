package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequestToCancel;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.mapper.FinancingRequestCancelationMapper;
import com.payline.payment.carrefour.banque.nx.service.business.impl.CirceoPaymentService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import com.payline.pmapi.service.ResetService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ResetServiceImpl implements ResetService {

    private FinancingRequestCancelationMapper financingRequestCancelationMapper = FinancingRequestCancelationMapper.INSTANCE;
    private CirceoPaymentService circeoPaymentService = CirceoPaymentService.getInstance();


    @Override
    public ResetResponse resetRequest(final ResetRequest resetRequest) {
        ResetResponse resetResponse;
        final FinancingRequestToCancel financingRequest = financingRequestCancelationMapper.map(resetRequest);
        final String financingId = resetRequest.getPartnerTransactionId();
        try {
            final CancelationResponse cancelationResponse = circeoPaymentService.doCancel(financingRequest, financingId,
                    resetRequest.getPartnerConfiguration());

            final CancelationRequestState cancelResponseStatus = cancelationResponse.getCancelationRequestState();
            if (CancelationRequestState.DONE.equals(cancelResponseStatus)) {
                resetResponse = ResetResponseSuccess.ResetResponseSuccessBuilder
                            .aResetResponseSuccess()
                            .withPartnerTransactionId(financingId)
                            .withStatusCode(cancelResponseStatus.name())
                            .build();

            } else if (CancelationRequestState.RECEIVED.equals(cancelResponseStatus)) {
                resetResponse = ResetResponseFailure.ResetResponseFailureBuilder
                        .aResetResponseFailure()
                        .withPartnerTransactionId(financingId)
                        .withErrorCode(cancelResponseStatus.name())
                        .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                        .build();
            } else {
                resetResponse = ResetResponseFailure.ResetResponseFailureBuilder
                        .aResetResponseFailure()
                        .withPartnerTransactionId(financingId)
                        .withErrorCode(cancelResponseStatus.name())
                        .withFailureCause(FailureCause.PAYMENT_PARTNER_ERROR)
                        .build();
            }

        } catch (final HttpErrorException e) {
            log.error(e);
            resetResponse = ResetResponseFailure.ResetResponseFailureBuilder.aResetResponseFailure()
                        .withErrorCode(e.getMessage())
                        .withFailureCause(FailureCause.INVALID_DATA)
                        .build();
            }
        return resetResponse;
    }

    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return true;
    }



}
