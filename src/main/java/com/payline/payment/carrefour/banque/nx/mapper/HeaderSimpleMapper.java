package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.HeaderSimple;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(imports = {Constants.ContractConfigurationKeys.class, PluginUtils.class})
@SuppressWarnings("squid:S1214")
public interface HeaderSimpleMapper {

    HeaderSimpleMapper INSTANCE = Mappers.getMapper(HeaderSimpleMapper.class);

    @Mapping(expression = "java(resetRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())", target = "merchantId")
    @Mapping(source = "resetRequest.transactionId", target = "messageId")
    @Mapping(expression = "java(PluginUtils.dateTimeNow())", target = "creationDateTime")
    @Mapping(source = "resetRequest.environment.notificationURL", target = "callbackUrl")
    HeaderSimple map(ResetRequest resetRequest);

    @Mapping(expression = "java(refundRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())", target = "merchantId")
    @Mapping(source = "refundRequest.transactionId", target = "messageId")
    @Mapping(expression = "java(PluginUtils.dateTimeNow())", target = "creationDateTime")
    @Mapping(source = "refundRequest.environment.notificationURL", target = "callbackUrl")
    HeaderSimple map(RefundRequest refundRequest);

}
