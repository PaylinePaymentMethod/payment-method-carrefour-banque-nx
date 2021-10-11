package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.CaptureTime;
import com.payline.payment.carrefour.banque.nx.bean.request.Channel;
import com.payline.payment.carrefour.banque.nx.bean.request.DeliveryMode;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class FinancingRequestMapperTest {

    private static final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

    @Test
    void mapFinancingRequest() throws ParseException {
        final PaymentRequest paymentRequest = MockUtils.aPaylinePaymentRequest();

        final FinancingRequest request = FinancingRequestMapper.INSTANCE.map(paymentRequest);

        assertHeader(request);
        assertRequest(request);
        assertCustomer(request);
    }

    private void assertHeader(final FinancingRequest request) throws ParseException {
        assertEquals("MERCHANT_ID", request.getHeader().getMerchantId());
        assertEquals("PAYLINE20200901181833", request.getHeader().getMessageId());
        assertNotNull(sdf.parse(request.getHeader().getCreationDateTime()));
        assertEquals("http://redirectionURL.com", request.getHeader().getRedirectPaymentSuccessUrl());
        assertEquals("http://redirectionURL.com", request.getHeader().getRedirectPaymentFailureUrl());
        assertEquals("http://redirectionCancelURL.com", request.getHeader().getRedirectPaymentCancelUrl());
        assertEquals("http://notificationURL.com", request.getHeader().getCallbackUrl());
    }

    private void assertRequest(final FinancingRequest request) throws ParseException {
        assertEquals("tbc1", request.getRequest().getOfferId());
        assertEquals(4, request.getRequest().getDuration());
        assertEquals(CaptureTime.PAYMENT, request.getRequest().getCaptureTime());
        assertEquals("REF20200901181833", request.getRequest().getOrder().getOrderId());
        assertEquals(Channel.DESKTOP, request.getRequest().getOrder().getChannel());
        assertNotNull(sdf.parse(request.getRequest().getOrder().getOrderDateTime()));

        // cart
        assertEquals("10.00", request.getRequest().getOrder().getCart().getTotalFinancedAmount());
        assertEquals("EUR", request.getRequest().getOrder().getCart().getCurrency());
        assertEquals(DeliveryMode.PICKUP_AT_MERCHANT, request.getRequest().getOrder().getCart().getDeliveryMode());
        assertEquals("192.168.0.1", request.getRequest().getOrder().getCart().getIpV4());
        assertNull(request.getRequest().getOrder().getCart().getIpV6());

        // cart items
        assertEquals(2, request.getRequest().getOrder().getCart().getCartItems().size());

        // first cart item
        assertEquals("20.00", request.getRequest().getOrder().getCart().getCartItems().get(0).getTotalPrice());
        assertEquals("10.00", request.getRequest().getOrder().getCart().getCartItems().get(0).getUnitPrice());
        assertEquals("EUR", request.getRequest().getOrder().getCart().getCartItems().get(0).getCurrency());
        assertEquals("2", request.getRequest().getOrder().getCart().getCartItems().get(0).getQuantity());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(0).getUniverse());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(0).getCategory());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(0).getBrand());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(0).getModel());
        assertEquals("ref", request.getRequest().getOrder().getCart().getCartItems().get(0).getReference());
        assertEquals("comment?", request.getRequest().getOrder().getCart().getCartItems().get(0).getDescription());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(0).getPictureUri());

        // second cart item
        assertEquals("30.00", request.getRequest().getOrder().getCart().getCartItems().get(1).getTotalPrice());
        assertEquals("10.00", request.getRequest().getOrder().getCart().getCartItems().get(1).getUnitPrice());
        assertEquals("EUR", request.getRequest().getOrder().getCart().getCartItems().get(1).getCurrency());
        assertEquals("3", request.getRequest().getOrder().getCart().getCartItems().get(1).getQuantity());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(1).getUniverse());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(1).getCategory());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(1).getBrand());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(1).getModel());
        assertEquals("ref2", request.getRequest().getOrder().getCart().getCartItems().get(1).getReference());
        assertEquals("comment2", request.getRequest().getOrder().getCart().getCartItems().get(1).getDescription());
        assertNull(request.getRequest().getOrder().getCart().getCartItems().get(1).getPictureUri());

        // misc data
        assertNull(request.getRequest().getMiscData());
    }

    private void assertCustomer(final FinancingRequest request) {
        assertEquals("id1", request.getCustomer().getCustomerId());

        // identification
        assertNull(request.getCustomer().getIdentification().getTitle());
        assertEquals("Durand", request.getCustomer().getIdentification().getSurname());
        assertEquals("Marie", request.getCustomer().getIdentification().getFirstname());
        assertNull(request.getCustomer().getIdentification().getBirthName());
        assertNull(request.getCustomer().getIdentification().getDateOfBirth());
        assertNull(request.getCustomer().getIdentification().getPlaceOfBirth());
        assertNull(request.getCustomer().getIdentification().getPlaceOfBirthCode());
        assertNull(request.getCustomer().getIdentification().getPlaceOfBirthCountry());

        // account data
        assertNull(request.getCustomer().getAccountData());

        // contract data
        assertEquals("0033601020304", request.getCustomer().getContactData().getMobile());
        assertEquals("test@mail.com", request.getCustomer().getContactData().getEmail());

        // billing address
        assertEquals("1 billing1" , request.getCustomer().getBillingAddress().getStreetNumberAndNameLine1());
        assertEquals("billing2" , request.getCustomer().getBillingAddress().getStreetNameLine2());
        assertEquals("billingCity" , request.getCustomer().getBillingAddress().getTownName());
        assertEquals("billingZipCode" , request.getCustomer().getBillingAddress().getPostalCode());
        assertEquals("FR" , request.getCustomer().getBillingAddress().getCountry());

        // shipping address
        assertEquals("2 delivery1" , request.getCustomer().getShippingAddress().getStreetNumberAndNameLine1());
        assertNull(request.getCustomer().getShippingAddress().getStreetNameLine2());
        assertEquals("deliveryCity" , request.getCustomer().getShippingAddress().getTownName());
        assertEquals("deliveryZipCode" , request.getCustomer().getShippingAddress().getPostalCode());
        assertEquals("FR" , request.getCustomer().getShippingAddress().getCountry());
    }
}