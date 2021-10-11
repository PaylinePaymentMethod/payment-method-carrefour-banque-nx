package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.Cart;
import com.payline.payment.carrefour.banque.nx.bean.request.DeliveryMode;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CartItemMapper.class, AmountMapper.class})
@SuppressWarnings("squid:S1214")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(source = "paymentRequest.order.amount", target = "totalFinancedAmount")
    @Mapping(source = "paymentRequest.order.amount.currency.currencyCode", target = "currency")
    @Mapping(source = "paymentRequest.order.deliveryMode", target = "deliveryMode")
    @Mapping(source = "paymentRequest.browser.ip", target = "ipV4")
    @Mapping(source = "paymentRequest.order.items", target = "cartItems")
    Cart map(PaymentRequest paymentRequest);

    default DeliveryMode mapDeliveryMode(final String deliveryMode) {
        final DeliveryMode result;
        switch (deliveryMode) {
            case "1":
            case "999":
                result = DeliveryMode.PICKUP_AT_MERCHANT;
                break;
            case "2":
            case "3":
                result = DeliveryMode.RELAY;
                break;
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "10":
                result = DeliveryMode.POSTAL;
                break;
            case "9":
                result = DeliveryMode.DIGITAL;
                break;
            default:
                result = null;
        }
        return result;
    }
}
