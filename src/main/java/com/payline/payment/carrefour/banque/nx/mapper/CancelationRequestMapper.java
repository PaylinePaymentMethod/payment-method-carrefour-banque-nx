package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.CancelationRequest;
import com.payline.payment.carrefour.banque.nx.bean.request.CancelationType;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigInteger;
import java.util.Currency;

@Mapper(uses = {OrderMapper.class, AmountMapper.class})
@SuppressWarnings("squid:S1214")
public interface CancelationRequestMapper {

    CancelationRequestMapper INSTANCE = Mappers.getMapper(CancelationRequestMapper.class);

    @Mapping(source = "resetRequest.order.reference", target = "orderId")
    @Mapping(expression = "java(cancelationType(resetRequest))", target = "cancelationType")
    @Mapping(expression = "java(calculatedNewFinancedAmount(resetRequest))", target = "newFinancedAmount")
    @Mapping(source = "resetRequest.amount", target = "canceledAmount")
    @Mapping(source = "resetRequest.order.amount.currency.currencyCode", target = "currency")
    CancelationRequest map(ResetRequest resetRequest);

    default CancelationType cancelationType(final ResetRequest resetRequest) {
        final float newFinancedAmount = calculatedNewFinancedAmount(resetRequest);
        if (newFinancedAmount > 0) {
            return CancelationType.PARTIAL;
        }
        return CancelationType.FULL;
    }

    default float calculatedNewFinancedAmount(final ResetRequest resetRequest) {
        final Currency currency = resetRequest.getOrder().getAmount().getCurrency();
        final BigInteger canceledAmount = resetRequest.getAmount().getAmountInSmallestUnit();
        final BigInteger orderAmount = resetRequest.getOrder().getAmount().getAmountInSmallestUnit();
        final Amount newFinancedAmount = new Amount(orderAmount.subtract(canceledAmount), currency);
        final String newFinancedAmountCalculated = AmountMapper.INSTANCE.mapAmount(newFinancedAmount);
        return Float.parseFloat(newFinancedAmountCalculated);
    }

}
