package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.PartnerAddress;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.common.Buyer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
@SuppressWarnings("squid:S1214")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(source = "address", target = "streetNumberAndNameLine1")
    @Mapping(source = "street2", target = "streetNameLine2")
    @Mapping(source = "city", target = "townName")
    @Mapping(source = "zipCode", target = "postalCode")
    PartnerAddress map(Buyer.Address address);

    default String mapStreetNumberAndNameLine1(final Buyer.Address address) {
        return PluginUtils.isEmpty(address.getStreetNumber()) ?
                address.getStreet1() : address.getStreetNumber() + " " + address.getStreet1();
    }
}
