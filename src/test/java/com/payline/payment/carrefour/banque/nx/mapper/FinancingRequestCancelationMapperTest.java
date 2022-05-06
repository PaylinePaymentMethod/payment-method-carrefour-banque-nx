package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.bean.request.CancelationType;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequestToCancel;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class FinancingRequestCancelationMapperTest {

    private static final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);


    @Nested
    class withResetRequest {
        @Test
        void mapFullFinancingRequestCanceletion() throws ParseException {
            final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(1000);
            final FinancingRequestToCancel request = FinancingRequestCancelationMapper.INSTANCE.map(resetRequest);

            assertHeader(request);
            assertRequest(request);
        }

        @Test
        void mapPartialFinancingRequestCanceletion() throws ParseException {
            final ResetRequest resetRequest = MockUtils.aPaylineResetRequest(500);
            final FinancingRequestToCancel request = FinancingRequestCancelationMapper.INSTANCE.map(resetRequest);

            assertHeader(request);
            assertPartialRequest(request, 5.0f);
        }

    }

    @Nested
    class withRefundRequest {
        @Test
        void mapFullFinancingRequestCanceletion() throws ParseException {
            final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(1000, 1000, 0);
            final FinancingRequestToCancel request = FinancingRequestCancelationMapper.INSTANCE.map(refundRequest);

            assertHeader(request);
            assertRequest(request);
        }

        @Test
        void mapPartialFinancingRequestCanceletion() throws ParseException {
            final RefundRequest refundRequest = MockUtils.aPaylineRefundRequest(500, 1000, 200);
            final FinancingRequestToCancel request = FinancingRequestCancelationMapper.INSTANCE.map(refundRequest);

            assertHeader(request);
            assertPartialRequest(request, 3.0f);
        }

    }

    private void assertHeader(final FinancingRequestToCancel request) throws ParseException {
        assertEquals("MERCHANT_ID", request.getHeader().getMerchantId());
        assertEquals("PAYLINE20200901181833", request.getHeader().getMessageId());
        assertNotNull(sdf.parse(request.getHeader().getCreationDateTime()));
        assertEquals("http://notificationURL.com", request.getHeader().getCallbackUrl());
    }

    private void assertRequest(final FinancingRequestToCancel request) {
        //orderId
        assertEquals("REF20200901181833", request.getRequest().getOrderId());
        //cancelationType
        assertEquals(CancelationType.FULL, request.getRequest().getCancelationType());
        //currency
        assertEquals("EUR", request.getRequest().getCurrency());
        //newFinancedAmount
        assertEquals(0, request.getRequest().getNewFinancedAmount());
        //canceledAmount
        assertEquals(10, request.getRequest().getCanceledAmount());
    }

    private void assertPartialRequest(final FinancingRequestToCancel request, final float expectedFinancedAmount) {
        //orderId
        assertEquals("REF20200901181833", request.getRequest().getOrderId());
        //cancelationType
        assertEquals(CancelationType.PARTIAL, request.getRequest().getCancelationType());
        //currency
        assertEquals("EUR", request.getRequest().getCurrency());
        //newFinancedAmount
        assertEquals(expectedFinancedAmount, request.getRequest().getNewFinancedAmount());
        //canceledAmount
        assertEquals(5, request.getRequest().getCanceledAmount());
    }
}
