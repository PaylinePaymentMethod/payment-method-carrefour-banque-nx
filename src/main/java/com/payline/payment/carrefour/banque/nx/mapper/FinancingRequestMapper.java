package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {HeaderMapper.class, RequestMapper.class, CustomerMapper.class})
@SuppressWarnings("squid:S1214")
public interface FinancingRequestMapper {

    FinancingRequestMapper INSTANCE = Mappers.getMapper(FinancingRequestMapper.class);

    @Mapping(source = "paymentRequest", target = "header")
    @Mapping(source = "paymentRequest", target = "request")
    @Mapping(source = "paymentRequest.buyer", target = "customer")
    FinancingRequest map(PaymentRequest paymentRequest);
}
