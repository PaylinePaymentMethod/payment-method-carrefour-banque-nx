package com.payline.payment.carrefour.banque.nx.business;

import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateResponse;
import com.payline.pmapi.bean.capture.response.CaptureResponse;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.reset.response.ResetResponse;

public interface CirceoPaymentBusiness {

     /**
      * Mapping des codes retours de remboursement circeo
      * @param cancelationResponse la réponse de la requête de remboursement
      * @return refundResponse
      */
     RefundResponse handleRefundResponse(CancelationResponse cancelationResponse);


     /**
      * Mapping des codes retours d'annulation circeo
      * @param cancelationResponse la réponse de la requête d'annulation
      * @return resetResponse
      */
     ResetResponse handleResetResponse(CancelationResponse cancelationResponse);


     /**
      * Permet de convertir la réponse d'une CaptureCirceo.
      * @param deliveryResponse la réponse de la requête de livraison des fonds.
      * @return captureResponse
      */
     CaptureResponse handleCaptureResponse(DeliveryUpdateResponse deliveryResponse);
}
