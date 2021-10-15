package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequestToCancel;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {HeaderSimpleMapper.class, CancelationRequestMapper.class})
@SuppressWarnings("squid:S1214")
public interface FinancingRequestCancelationMapper {
    FinancingRequestCancelationMapper INSTANCE = Mappers.getMapper(FinancingRequestCancelationMapper.class);

    @Mapping(source = "resetRequest", target = "header")
    @Mapping(source = "resetRequest", target = "request")
    FinancingRequestToCancel map(ResetRequest resetRequest);
}
