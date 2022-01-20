package com.payline.payment.carrefour.banque.nx.service.impl;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateResponse;
import com.payline.payment.carrefour.banque.nx.business.CirceoPaymentBusiness;
import com.payline.payment.carrefour.banque.nx.business.impl.CirceoPaymentBusinessImpl;
import com.payline.payment.carrefour.banque.nx.exception.HttpErrorException;
import com.payline.payment.carrefour.banque.nx.proxy.CirceoProxy;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.capture.response.CaptureResponse;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseFailure;
import com.payline.pmapi.bean.common.FailureCause;
import lombok.extern.log4j.Log4j2;
import com.payline.pmapi.service.CaptureService;

@Log4j2
public class CaptureServiceImpl implements CaptureService {

    private CirceoProxy circeoProxy = CirceoProxy.getInstance();
    private CirceoPaymentBusiness circeoPaymentBusiness = new CirceoPaymentBusinessImpl();


    @Override
    public CaptureResponse captureRequest(final CaptureRequest captureRequest) {
        CaptureResponse captureResponse;
        try {
            final DeliveryUpdateResponse deliveryResponse = circeoProxy.doCapture(captureRequest);
            captureResponse = circeoPaymentBusiness.handleCaptureResponse(deliveryResponse);
        } catch (final HttpErrorException e) {
            log.error(e);
            captureResponse = CaptureResponseFailure.CaptureResponseFailureBuilder.aCaptureResponseFailure()
                    .withErrorCode(e.getMessage())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }
        return captureResponse;

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
