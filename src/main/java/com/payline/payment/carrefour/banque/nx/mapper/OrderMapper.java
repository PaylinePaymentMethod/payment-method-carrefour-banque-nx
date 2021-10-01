package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.Channel;
import com.payline.payment.carrefour.banque.nx.bean.request.PartnerOrder;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CartMapper.class)
@SuppressWarnings("squid:S1214")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "paymentRequest.browser.userAgent", target = "channel")
    @Mapping(source = "paymentRequest.order.reference", target = "orderId")
    @Mapping(source = "paymentRequest.order.date", target = "orderDateTime", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Mapping(source = "paymentRequest", target = "cart")
    PartnerOrder map(PaymentRequest paymentRequest);

    default Channel mapChannel(final String paymentRequest) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(paymentRequest);
        final DeviceType deviceType = userAgent.getOperatingSystem().getDeviceType();

        final Channel channel;
        switch (deviceType) {
            case MOBILE:
                channel = Channel.PHONE;
                break;
            case TABLET:
                channel = Channel.TABLET;
                break;
            default:
                channel = Channel.DESKTOP;
        }
        return channel;
    }
}
