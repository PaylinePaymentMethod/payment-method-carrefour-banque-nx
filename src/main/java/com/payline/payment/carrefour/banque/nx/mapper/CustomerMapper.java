package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.bean.request.Customer;
import com.payline.payment.carrefour.banque.nx.bean.request.PartnerAddress;
import com.payline.pmapi.bean.common.Buyer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
@SuppressWarnings("squid:S1214")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "customerIdentifier", target = "customerId")
    @Mapping(source = "fullName.lastName", target = "identification.surname")
    @Mapping(source = "fullName.firstName", target = "identification.firstname")
    @Mapping(expression = "java(buyer.getPhoneNumberForType(Buyer.PhoneNumberType.CELLULAR))", target = "contactData.mobile")
    @Mapping(source = "email", target = "contactData.email")
    @Mapping(expression = "java(mapAddress(buyer.getAddressForType(Buyer.AddressType.BILLING)))", target = "billingAddress")
    @Mapping(expression = "java(mapAddress(buyer.getAddressForType(Buyer.AddressType.DELIVERY)))", target = "shippingAddress")
    Customer map(Buyer buyer);

    default PartnerAddress mapAddress(final Buyer.Address address) {
        return AddressMapper.INSTANCE.map(address);
    }
}
