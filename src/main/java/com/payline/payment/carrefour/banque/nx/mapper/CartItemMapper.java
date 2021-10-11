package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.CartItem;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.Order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigInteger;

@Mapper(uses = AmountMapper.class)
@SuppressWarnings("squid:S1214")
public interface CartItemMapper {

    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(source = "amount", target = "unitPrice")
    @Mapping(source = "amount.currency.currencyCode", target = "currency")
    @Mapping(expression = "java(mapTotalPrice(item.getAmount(), item.getQuantity()))", target = "totalPrice")
    @Mapping(source = "comment", target = "description")
    CartItem map(OrderItem item);

    default String mapTotalPrice(final Amount amount, final Long quantity) {
        final BigInteger totalPrice = amount.getAmountInSmallestUnit().multiply(BigInteger.valueOf(quantity));
        return AmountMapper.INSTANCE.mapAmount(new Amount(totalPrice, amount.getCurrency()));
    }
}
