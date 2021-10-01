package com.payline.payment.carrefour.banque.nx.service.business.impl;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.exception.InvalidDataException;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.Order;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequiredDataValidatorTest {

    private final RequiredDataValidator underTest = RequiredDataValidator.getInstance();

    @Test
    void validateRequiredDataForFinancingRequestNominal() {
        underTest.validateRequiredDataForFinancingRequest(MockUtils.aBuyer(), MockUtils.anOrder());
    }

    @Nested
    class ShouldNotValidateIf {

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyOrderDeliveryMode(final String deliveryMode) {
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode(deliveryMode)
                    .build();

            assertInvalidDataException(null, order, "order.deliveryMode is required");
        }

        @Test
        void nullOrderItems() {
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode("deliveryMode")
                    .build();

            assertInvalidDataException(null, order, "order.details are required");
        }

        @Test
        void emptyOrderItems() {
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode("deliveryMode")
                    .withItems(new ArrayList<>())
                    .build();

            assertInvalidDataException(null, order, "order.details are required");
        }

        @Test
        void nullOrderItemAmount() {
            final List<Order.OrderItem> items = new ArrayList<>();
            items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                    .build());
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode("deliveryMode")
                    .withItems(items)
                    .build();

            assertInvalidDataException(null, order, "order.details.price is required");
        }

        @Test
        void nullOrderItemAmountInSmallestUnit() {
            final List<Order.OrderItem> items = new ArrayList<>();
            items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                    .withAmount(new Amount(null, Currency.getInstance("EUR")))
                    .build());
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode("deliveryMode")
                    .withItems(items)
                    .build();

            assertInvalidDataException(null, order, "order.details.price is required");
        }

        @Test
        void nullOrderItemQuantity() {
            final List<Order.OrderItem> items = new ArrayList<>();
            items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                    .withAmount(MockUtils.aPaylineAmount())
                    .build());
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode("deliveryMode")
                    .withItems(items)
                    .build();

            assertInvalidDataException(null, order, "order.details.quantity is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyOrderItemComment(final String comment) {
            final List<Order.OrderItem> items = new ArrayList<>();
            items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                    .withAmount(MockUtils.aPaylineAmount())
                    .withQuantity(1L)
                    .withComment(comment)
                    .build());
            final Order order = Order.OrderBuilder.anOrder()
                    .withDeliveryMode("deliveryMode")
                    .withItems(items)
                    .build();

            assertInvalidDataException(null, order, "order.details.comment is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerCustomerIdentifier(final String customerIdentifier) {
            final Order order = MockUtils.anOrder();
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier(customerIdentifier)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.customerId is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerLastName(final String lastName) {
            final Order order = MockUtils.anOrder();
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", lastName, null))
                    .build();

            assertInvalidDataException(buyer, order, "buyer.lastName is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerFirstName(final String firstName) {
            final Order order = MockUtils.anOrder();
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName(firstName, "lastName", null))
                    .build();

            assertInvalidDataException(buyer, order, "buyer.firstName is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerEmail(final String email) {
            final Order order = MockUtils.anOrder();
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail(email)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.email is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerMobilePhone(final String mobilePhone) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, mobilePhone);
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.mobilePhone is required");
        }

        @Test
        void nullBuyerAddresses() {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress and buyer.billingAddress are required");
        }

        @Test
        void emptyBuyerAddresses() {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(new HashMap<>())
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress and buyer.billingAddress are required");
        }

        @Test
        void nullBuyerDeliveryAddress() {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, null);
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerDeliveryAddressStreetNumber(final String streetNumber) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber(streetNumber)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress.streetNumber is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerDeliveryAddressStreet1(final String street1) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1(street1)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress.street1 is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerDeliveryAddressCityName(final String cityName) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1("street1")
                    .withCity(cityName)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress.cityName is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerDeliveryAddressZipCode(final String zipCode) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1("street1")
                    .withCity("cityName")
                    .withZipCode(zipCode)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress.zipCode is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerDeliveryAddressCountry(final String country) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1("street1")
                    .withCity("cityName")
                    .withZipCode("zipCode")
                    .withCountry(country)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.shippingAdress.country is required");
        }

        @Test
        void nullBuyerBillingAddress() {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, MockUtils.aPaylineAddress());
            addresses.put(Buyer.AddressType.BILLING, null);
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.billingAddress is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerBillingAddressStreetNumber(final String streetNumber) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, MockUtils.aPaylineAddress());
            addresses.put(Buyer.AddressType.BILLING, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber(streetNumber)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.billingAddress.streetNumber is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerBillingAddressStreet1(final String street1) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, MockUtils.aPaylineAddress());
            addresses.put(Buyer.AddressType.BILLING, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1(street1)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.billingAddress.street1 is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerBillingAddressCityName(final String cityName) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, MockUtils.aPaylineAddress());
            addresses.put(Buyer.AddressType.BILLING, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1("street1")
                    .withCity(cityName)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.billingAddress.cityName is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerBillingAddressZipCode(final String zipCode) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, MockUtils.aPaylineAddress());
            addresses.put(Buyer.AddressType.BILLING, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1("street1")
                    .withCity("cityName")
                    .withZipCode(zipCode)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.billingAddress.zipCode is required");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyBuyerBillingAddressCountry(final String country) {
            final Order order = MockUtils.anOrder();
            final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
            phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "mobilePhone");
            final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
            addresses.put(Buyer.AddressType.DELIVERY, MockUtils.aPaylineAddress());
            addresses.put(Buyer.AddressType.BILLING, Buyer.Address.AddressBuilder.anAddress()
                    .withStreetNumber("streetNumber")
                    .withStreet1("street1")
                    .withCity("cityName")
                    .withZipCode("zipCode")
                    .withCountry(country)
                    .build());
            final Buyer buyer = Buyer.BuyerBuilder.aBuyer()
                    .withCustomerIdentifier("customerIdentifier")
                    .withFullName(new Buyer.FullName("firstname", "lastName", null))
                    .withEmail("email")
                    .withPhoneNumbers(phoneNumbers)
                    .withAddresses(addresses)
                    .build();

            assertInvalidDataException(buyer, order, "buyer.billingAddress.country is required");
        }

        private void assertInvalidDataException(final Buyer buyer, final Order order, final String message) {
            final InvalidDataException invalidDataException = assertThrows(InvalidDataException.class,
                    () -> underTest.validateRequiredDataForFinancingRequest(buyer, order));

            assertEquals(FailureCause.INVALID_DATA, invalidDataException.getFailureCause());
            assertEquals(message, invalidDataException.getMessage());
        }
    }
}