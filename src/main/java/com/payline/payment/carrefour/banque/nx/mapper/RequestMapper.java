package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.CaptureTime;
import com.payline.payment.carrefour.banque.nx.bean.request.Request;
import com.payline.payment.carrefour.banque.nx.service.PartnerConfigurationService;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.Constants.ContractConfigurationKeys;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(uses = OrderMapper.class)
@SuppressWarnings("squid:S1214")
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);
    PartnerConfigurationService PARTNER_CONFIGURATION_SERVICE = PartnerConfigurationService.getInstance();

    @Mapping(expression = "java(mapOfferId(paymentRequest))", target = "offerId")
    @Mapping(expression = "java(mapDuration(paymentRequest))", target = "duration")
    @Mapping(source = "paymentRequest.captureNow", target = "captureTime")
    @Mapping(source = "paymentRequest", target = "order")
    Request map(PaymentRequest paymentRequest);

    default int mapDuration(final PaymentRequest paymentRequest) {
        final String key = paymentRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.DURATION).getValue();
        final Map<String, String> durationOptions = PARTNER_CONFIGURATION_SERVICE.fetchOptionsFromPartnerConf(
                Constants.PartnerConfigurationKeys.DURATION_OPTIONS_AVAILABLE, paymentRequest.getPartnerConfiguration());
        return Integer.parseInt(durationOptions.get(key));
    }

    default String mapOfferId(final PaymentRequest paymentRequest) {
        final String key = paymentRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.OFFER_ID).getValue();
        final Map<String, String> offerOptions = PARTNER_CONFIGURATION_SERVICE.fetchOptionsFromPartnerConf(
                Constants.PartnerConfigurationKeys.OFFER_OPTIONS_AVAILABLE, paymentRequest.getPartnerConfiguration());
        return offerOptions.get(key);
    }

    default CaptureTime mapCaptureTime(final boolean isAutorAndCapture) {
        return isAutorAndCapture ? CaptureTime.PAYMENT : CaptureTime.SHIPMENT;
    }

}
