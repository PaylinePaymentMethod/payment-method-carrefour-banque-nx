package com.payline.payment.carrefour.banque.nx.service.business.impl;

import com.payline.payment.carrefour.banque.nx.exception.InvalidDataException;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.payment.Order;

public class RequiredDataValidator {

    private static class Holder {
        private static final RequiredDataValidator INSTANCE = new RequiredDataValidator();
    }

    public static RequiredDataValidator getInstance() {
        return RequiredDataValidator.Holder.INSTANCE;
    }

    private RequiredDataValidator() {
    }

    public void validateRequiredDataForFinancingRequest(final Buyer buyer, final Order order) {
        checkNotEmpty(order.getDeliveryMode(), "order.deliveryMode is required");
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new InvalidDataException("order.details are required");
        }
        order.getItems().forEach(item -> {
            if (item.getAmount() == null || item.getAmount().getAmountInSmallestUnit() == null) {
                throw new InvalidDataException("order.details.price is required");
            }
            if (item.getQuantity() == null) {
                throw new InvalidDataException("order.details.quantity is required");
            }
            checkNotEmpty(item.getComment(), "order.details.comment is required");
        });
        checkNotEmpty(buyer.getCustomerIdentifier(), "buyer.customerId is required");
        checkNotEmpty(buyer.getFullName().getLastName(), "buyer.lastName is required");
        checkNotEmpty(buyer.getFullName().getFirstName(), "buyer.firstName is required");
        checkNotEmpty(buyer.getEmail(), "buyer.email is required");
        checkNotEmpty(buyer.getPhoneNumberForType(Buyer.PhoneNumberType.CELLULAR), "buyer.mobilePhone is required");
        if (buyer.getAddresses() == null || buyer.getAddresses().isEmpty()) {
            throw new InvalidDataException("buyer.shippingAdress and buyer.billingAddress are required");
        }
        final Buyer.Address shippingAddress = buyer.getAddressForType(Buyer.AddressType.DELIVERY);
        if (shippingAddress == null) {
            throw new InvalidDataException("buyer.shippingAdress is required");
        }
        checkNotEmpty(shippingAddress.getStreetNumber(), "buyer.shippingAdress.streetNumber is required");
        checkNotEmpty(shippingAddress.getStreet1(), "buyer.shippingAdress.street1 is required");
        checkNotEmpty(shippingAddress.getCity(), "buyer.shippingAdress.cityName is required");
        checkNotEmpty(shippingAddress.getZipCode(), "buyer.shippingAdress.zipCode is required");
        checkNotEmpty(shippingAddress.getCountry(), "buyer.shippingAdress.country is required");
        final Buyer.Address billingAddress = buyer.getAddressForType(Buyer.AddressType.BILLING);
        if (billingAddress == null) {
            throw new InvalidDataException("buyer.billingAddress is required");
        }
        checkNotEmpty(billingAddress.getStreetNumber(), "buyer.billingAddress.streetNumber is required");
        checkNotEmpty(billingAddress.getStreet1(), "buyer.billingAddress.street1 is required");
        checkNotEmpty(billingAddress.getCity(), "buyer.billingAddress.cityName is required");
        checkNotEmpty(billingAddress.getZipCode(), "buyer.billingAddress.zipCode is required");
        checkNotEmpty(billingAddress.getCountry(), "buyer.billingAddress.country is required");
    }

    private void checkNotEmpty(final String stringToCheck, final String errorMessage) {
        if (stringToCheck == null || stringToCheck.equals("")) {
            throw new InvalidDataException(errorMessage);
        }
    }
}
