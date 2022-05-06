package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.business.CirceoPaymentBusiness;
import com.payline.payment.carrefour.banque.nx.business.impl.CirceoPaymentBusinessImpl;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.service.RefundService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RefundServiceImpl implements RefundService {

    private CirceoProxy circeoProxy = CirceoProxy.getInstance();
    private CirceoPaymentBusiness circeoPaymentBusiness = new CirceoPaymentBusinessImpl();

    /**
     * Execute a refund request
     * @param refundRequest refundRequest
     * @return an implementation of {@link RefundResponse}
     */
    @Override
    public RefundResponse refundRequest(final RefundRequest refundRequest) {
        RefundResponse refundResponse;

        try {
            final CancelationResponse cancelationResponse = circeoProxy.doCancel(refundRequest, refundRequest.getPartnerConfiguration());
            refundResponse = circeoPaymentBusiness.handleRefundResponse(cancelationResponse);
        } catch (final HttpErrorException e) {
            log.error(e);
            refundResponse = RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withErrorCode(e.getMessage())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }
        return refundResponse;
    }

    /**
     * @return true if implementation authorize multiple payment else false
     */
    @Override
    public boolean canMultiple() {
        return true;
    }

    /**
     * @return true if implementation authorize partial payment else false
     */
    @Override
    public boolean canPartial() {
        return true;
    }
}
