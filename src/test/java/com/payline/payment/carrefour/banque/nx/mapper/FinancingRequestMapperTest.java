package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.*;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class FinancingRequestMapperTest {

    private static final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

    @Test
    void mapFinancingRequest() throws ParseException {
        final PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();

        final FinancingRequest request = FinancingRequestMapper.INSTANCE.map(paymentRequest);

        assertHeader(request.getHeader());
        assertRequest(request.getRequest());
        assertCustomer(request.getCustomer());
    }

    private void assertHeader(final Header header) throws ParseException {
        assertEquals("MERCHANT_ID", header.getMerchantId());
        assertEquals("PAYLINE20200901181833", header.getMessageId());
        assertNotNull(sdf.parse(header.getCreationDateTime()));
        assertEquals("http://redirectionURL.com", header.getRedirectPaymentSuccessUrl());
        assertEquals("http://redirectionURL.com", header.getRedirectPaymentFailureUrl());
        assertEquals("http://redirectionCancelURL.com", header.getRedirectPaymentCancelUrl());
        assertEquals("http://notificationURL.com", header.getCallbackUrl());
    }

    private void assertRequest(final Request request) throws ParseException {
        assertEquals("tbc1", request.getOfferId());
        assertEquals(4, request.getDuration());
        assertEquals(CaptureTime.PAYMENT, request.getCaptureTime());
        assertEquals("REF20200901181833", request.getOrder().getOrderId());
        assertEquals(Channel.DESKTOP, request.getOrder().getChannel());
        assertNotNull(sdf.parse(request.getOrder().getOrderDateTime()));

        // cart
        assertEquals("10.00", request.getOrder().getCart().getTotalFinancedAmount());
        assertEquals("EUR", request.getOrder().getCart().getCurrency());
        assertEquals(DeliveryMode.PICKUP_AT_MERCHANT, request.getOrder().getCart().getDeliveryMode());
        assertEquals("192.168.0.1", request.getOrder().getCart().getIpV4());
        assertNull(request.getOrder().getCart().getIpV6());

        // cart items
        assertEquals(2, request.getOrder().getCart().getCartItems().size());

        // first cart item
        assertEquals("20.00", request.getOrder().getCart().getCartItems().get(0).getTotalPrice());
        assertEquals("10.00", request.getOrder().getCart().getCartItems().get(0).getUnitPrice());
        assertEquals("EUR", request.getOrder().getCart().getCartItems().get(0).getCurrency());
        assertEquals("2", request.getOrder().getCart().getCartItems().get(0).getQuantity());
        assertNull(request.getOrder().getCart().getCartItems().get(0).getUniverse());
        assertNull(request.getOrder().getCart().getCartItems().get(0).getCategory());
        assertNull(request.getOrder().getCart().getCartItems().get(0).getBrand());
        assertNull(request.getOrder().getCart().getCartItems().get(0).getModel());
        assertEquals("ref", request.getOrder().getCart().getCartItems().get(0).getReference());
        assertEquals("comment?", request.getOrder().getCart().getCartItems().get(0).getDescription());
        assertNull(request.getOrder().getCart().getCartItems().get(0).getPictureUri());

        // second cart item
        assertEquals("30.00", request.getOrder().getCart().getCartItems().get(1).getTotalPrice());
        assertEquals("10.00", request.getOrder().getCart().getCartItems().get(1).getUnitPrice());
        assertEquals("EUR", request.getOrder().getCart().getCartItems().get(1).getCurrency());
        assertEquals("3", request.getOrder().getCart().getCartItems().get(1).getQuantity());
        assertNull(request.getOrder().getCart().getCartItems().get(1).getUniverse());
        assertNull(request.getOrder().getCart().getCartItems().get(1).getCategory());
        assertNull(request.getOrder().getCart().getCartItems().get(1).getBrand());
        assertNull(request.getOrder().getCart().getCartItems().get(1).getModel());
        assertEquals("ref2", request.getOrder().getCart().getCartItems().get(1).getReference());
        assertEquals("comment2", request.getOrder().getCart().getCartItems().get(1).getDescription());
        assertNull(request.getOrder().getCart().getCartItems().get(1).getPictureUri());

        // misc data
        final Map<String, Object> miscData = request.getMiscData();
        assertNotNull(miscData);
        assertEquals(2, miscData.size());
        final Object object1 = miscData.get("cle1");
        assertTrue(object1 instanceof Map);
        final Map<String, Object> map1 = (Map<String, Object>) object1;
        assertEquals(1, map1.size());
        assertEquals("sousVal", map1.get("sousCle"));
        final Object object2 = miscData.get("cle2");
        assertEquals("valeur", object2);
    }

    private void assertCustomer(final Customer customer) {
        assertEquals("id1", customer.getCustomerId());

        // identification
        assertNull(customer.getIdentification().getTitle());
        assertEquals("Durand", customer.getIdentification().getSurname());
        assertEquals("Marie", customer.getIdentification().getFirstname());
        assertNull(customer.getIdentification().getBirthName());
        assertNull(customer.getIdentification().getDateOfBirth());
        assertNull(customer.getIdentification().getPlaceOfBirth());
        assertNull(customer.getIdentification().getPlaceOfBirthCode());
        assertNull(customer.getIdentification().getPlaceOfBirthCountry());

        // account data
        assertNull(customer.getAccountData());

        // contract data
        assertEquals("0033601020304", customer.getContactData().getMobile());
        assertEquals("test@mail.com", customer.getContactData().getEmail());

        // billing address
        assertEquals("1 billing1" , customer.getBillingAddress().getStreetNumberAndNameLine1());
        assertEquals("billing2" , customer.getBillingAddress().getStreetNameLine2());
        assertEquals("billingCity" , customer.getBillingAddress().getTownName());
        assertEquals("billingZipCode" , customer.getBillingAddress().getPostalCode());
        assertEquals("FR" , customer.getBillingAddress().getCountry());

        // shipping address
        assertEquals("2 delivery1" , customer.getShippingAddress().getStreetNumberAndNameLine1());
        assertNull(customer.getShippingAddress().getStreetNameLine2());
        assertEquals("deliveryCity" , customer.getShippingAddress().getTownName());
        assertEquals("deliveryZipCode" , customer.getShippingAddress().getPostalCode());
        assertEquals("FR" , customer.getShippingAddress().getCountry());
    }
}