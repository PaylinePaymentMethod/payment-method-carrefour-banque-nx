package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.Header;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.Constants.ContractConfigurationKeys;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(imports = ContractConfigurationKeys.class)
@SuppressWarnings("squid:S1214")
public interface HeaderMapper {

    HeaderMapper INSTANCE = Mappers.getMapper(HeaderMapper.class);

    @Mapping(expression = "java(paymentRequest.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())", target = "merchantId")
    @Mapping(source = "paymentRequest.transactionId", target = "messageId")
    @Mapping(expression = "java(dateTimeNow())", target = "creationDateTime")
    @Mapping(source = "paymentRequest.environment.notificationURL", target = "callbackUrl")
    @Mapping(source = "paymentRequest.environment.redirectionReturnURL", target = "redirectPaymentSuccessUrl")
    @Mapping(source = "paymentRequest.environment.redirectionReturnURL", target = "redirectPaymentFailureUrl")
    @Mapping(source = "paymentRequest.environment.redirectionCancelURL", target = "redirectPaymentCancelUrl")
    Header map(PaymentRequest paymentRequest);

    default String dateTimeNow() {
        return new SimpleDateFormat(Constants.DATE_TIME_FORMAT).format(new Date());
    }
}
