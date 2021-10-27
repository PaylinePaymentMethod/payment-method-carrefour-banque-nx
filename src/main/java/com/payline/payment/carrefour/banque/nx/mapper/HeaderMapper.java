package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.Header;
import com.payline.payment.carrefour.banque.nx.utils.Constants.ContractConfigurationKeys;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {ContractConfigurationKeys.class, PluginUtils.class})
@SuppressWarnings("squid:S1214")
public interface HeaderMapper {

    HeaderMapper INSTANCE = Mappers.getMapper(HeaderMapper.class);

    @Mapping(expression = "java(paymentRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())", target = "merchantId")
    @Mapping(source = "paymentRequest.transactionId", target = "messageId")
    @Mapping(expression = "java(PluginUtils.dateTimeNow())", target = "creationDateTime")
    @Mapping(source = "paymentRequest.environment.notificationURL", target = "callbackUrl")
    @Mapping(source = "paymentRequest.environment.redirectionReturnURL", target = "redirectPaymentSuccessUrl")
    @Mapping(source = "paymentRequest.environment.redirectionReturnURL", target = "redirectPaymentFailureUrl")
    @Mapping(source = "paymentRequest.environment.redirectionCancelURL", target = "redirectPaymentCancelUrl")
    Header map(PaymentRequest paymentRequest);
}
