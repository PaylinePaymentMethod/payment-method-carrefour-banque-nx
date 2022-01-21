package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.DeliveryUpdateRequest;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {OrderMapper.class, AmountMapper.class})
@SuppressWarnings("squid:S1214")
public interface DeliveryUpdateRequestMapper {

    DeliveryUpdateRequestMapper INSTANCE = Mappers.getMapper(DeliveryUpdateRequestMapper.class);

    @Mapping(source = "captureRequest.amount", target = "deliveryAmount")
    @Mapping(expression = "java(com.payline.payment.carrefour.banque.nx.bean.request.DeliveryState.SHIPPED)", target = "deliveryState")
    DeliveryUpdateRequest map(CaptureRequest captureRequest);

}
