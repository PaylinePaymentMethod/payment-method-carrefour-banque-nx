package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.DeliveryUpdateRequestToCapture;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {HeaderSimpleDeliveryMapper.class, DeliveryUpdateRequestMapper.class})
@SuppressWarnings("squid:S1214")
public interface FinancingRequestDeliveryMapper {

    FinancingRequestDeliveryMapper INSTANCE = Mappers.getMapper(FinancingRequestDeliveryMapper.class);

    @Mapping(source = "captureRequest", target = "header")
    @Mapping(source = "captureRequest", target = "request")
    DeliveryUpdateRequestToCapture map(CaptureRequest captureRequest);
}
