package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.HeaderSimple;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(imports = {Constants.ContractConfigurationKeys.class, PluginUtils.class})
@SuppressWarnings("squid:S1214")
public interface HeaderSimpleDeliveryMapper {

    HeaderSimpleDeliveryMapper INSTANCE = Mappers.getMapper(HeaderSimpleDeliveryMapper.class);

    @Mapping(expression = "java(captureRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())", target = "merchantId")
    @Mapping(source = "captureRequest.transactionId", target = "messageId")
    @Mapping(expression = "java(PluginUtils.dateTimeNow())", target = "creationDateTime")
    @Mapping(source = "captureRequest.environment.notificationURL", target = "callbackUrl")
    HeaderSimple map(CaptureRequest captureRequest);

}
