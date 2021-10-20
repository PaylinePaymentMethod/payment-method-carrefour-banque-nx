package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.business.CirceoPaymentBusiness;
import com.payline.payment.carrefour.banque.nx.business.impl.CirceoPaymentBusinessImpl;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.service.ResetService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ResetServiceImpl implements ResetService {

    private CirceoProxy circeoProxy = CirceoProxy.getInstance();
    private CirceoPaymentBusiness circeoPaymentBusiness = new CirceoPaymentBusinessImpl();

    @Override
    public ResetResponse resetRequest(final ResetRequest resetRequest) {
        ResetResponse resetResponse;
        try {
            final CancelationResponse cancelationResponse = circeoProxy.doCancel(resetRequest, resetRequest.getPartnerConfiguration());
            resetResponse = circeoPaymentBusiness.handleResetResponse(cancelationResponse);
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
