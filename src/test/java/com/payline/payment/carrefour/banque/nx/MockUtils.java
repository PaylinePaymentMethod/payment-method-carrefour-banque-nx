package com.payline.payment.carrefour.banque.nx;


import com.payline.payment.carrefour.banque.nx.bean.request.DeliveryUpdateRequestToCapture;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequest;
import com.payline.payment.carrefour.banque.nx.bean.request.FinancingRequestToCancel;
import com.payline.payment.carrefour.banque.nx.bean.request.State;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.CancelationResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateRequestState;
import com.payline.payment.carrefour.banque.nx.bean.response.DeliveryUpdateResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestResponse;
import com.payline.payment.carrefour.banque.nx.bean.response.FinancingRequestStatus;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.TestUtils;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.RetrievePluginConfigurationRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class that generates mocks of frequently used objects.
 */
public class MockUtils {

    private static final String timestamp = "20200901181833";

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public static Map<String, String> anAccountInfo(ContractConfiguration contractConfiguration) {
        Map<String, String> accountInfo = new HashMap<>();
        for (Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet()) {
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }
	
	
	/**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration() {
        final Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.MERCHANT_ID, new ContractProperty("MERCHANT_ID"));
        contractProperties.put(Constants.ContractConfigurationKeys.OFFER_ID, new ContractProperty("0"));
        contractProperties.put(Constants.ContractConfigurationKeys.DURATION, new ContractProperty("1"));
        return new ContractConfiguration("carrefour-banque-nx", contractProperties);
	}
	
	 /**
     * Generate a valid plugin configuration, as a <code>String</code>.
     */
    public static String aPluginConfiguration() {
        //TODO a completer avec les plugins configurations si existantes.
		return null;
    }

    /**
     * Generate a valid {@link Browser}.
     */
    public static Browser aBrowser() {
        return Browser.BrowserBuilder.aBrowser()
                .withLocale(Locale.getDefault())
                .withIp("192.168.0.1")
                .withUserAgent(aUserAgent())
                .build();
    }

    /**
     * Generate a valid {@link Buyer}.
     */
    public static Buyer aBuyer() {
        final Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.BILLING, Buyer.Address.AddressBuilder.anAddress()
                .withStreetNumber("")
                .withStreet1("billing1")
                .withStreet2("billing2")
                .withCity("billingCity")
                .withZipCode("billingZipCode")
                .withCountry("FR")
                .build());
        addresses.put(Buyer.AddressType.DELIVERY, Buyer.Address.AddressBuilder.anAddress()
                .withStreetNumber("2")
                .withStreet1("delivery1")
                .withCity("deliveryCity")
                .withZipCode("deliveryZipCode")
                .withCountry("FR")
                .build());
        final Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "0033601020304");
        return Buyer.BuyerBuilder.aBuyer()
                .withCustomerIdentifier("id1")
                .withFullName(new Buyer.FullName("Marie", "Durand", "1"))
                .withPhoneNumbers(phoneNumbers)
                .withAddresses(addresses)
                .withEmail("test@mail.com")
                .build();
    }

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }

    /**
     * Generate a valid, but not complete, {@link Order}
     */
    public static Order anOrder() {
        final List<Order.OrderItem> items = new ArrayList<>();
        items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(aPaylineAmount())
                .withQuantity(2L)
                .withReference("ref")
                .withComment("comment?")
                .build());
        items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(aPaylineAmount())
                .withQuantity(3L)
                .withReference("ref2")
                .withComment("comment2")
                .build());
        return Order.OrderBuilder.anOrder()
                .withDate(new Date())
                .withAmount(aPaylineAmount())
                .withReference("REF" + timestamp)
                .withDeliveryMode("1")
                .withItems(items)
                .build();
    }


    /**
     * Generate a valid Payline <code>Address</code>.
     */
    public static Buyer.Address aPaylineAddress() {
        return Buyer.Address.AddressBuilder.anAddress()
                .withCity("Aix-en-Provence")
                .withCountry("FR")
                .withEmail("john.doe@mythalesgroup.io")
                .withFullName(new Buyer.FullName("Jon", "Doe", "M."))
                .withStreetNumber("1")
                .withStreet1("150 rue dont le nom est le plus long que j'ai jamais vu. Y'a pas idée d'habiter un endroit pareil !")
                .withStreet2("Le grand bâtiment orange, avec les fenêtres un peu hautes mais un peu larges aussi, et un toit bleu")
                .withZipCode("13100")
                .build();
    }

    /**
     * Generate a valid Payline Amount.
     */
    public static com.payline.pmapi.bean.common.Amount aPaylineAmount() {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(1000), Currency.getInstance("EUR"));
    }

    /**
     * Generate a valid {@link PaymentRequest}.
     */
    public static PaymentRequest aPaylinePaymentRequest() {
        return aPaylinePaymentRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentRequest.Builder aPaylinePaymentRequestBuilder() {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withDifferedActionDate(TestUtils.addTime(new Date(), Calendar.DATE, 5))
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(anOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPaymentFormContext(aPaymentFormContext())
                .withPluginConfiguration(aPluginConfiguration())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId("PAYLINE" + timestamp)
                .withMiscData("{\"cle1\": {\"sousCle\": \"sousVal\"}, \"cle2\": \"valeur\"}");
    }

    /**
     * Generate a valid {@link PaymentFormContext}.
     */
    public static PaymentFormContext aPaymentFormContext(){
        Map<String, String> paymentFormParameter = new HashMap<>();

        Map<String,String> sensitivePaymentFormParameter = new HashMap<>();


        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(paymentFormParameter)
                .withSensitivePaymentFormParameter(sensitivePaymentFormParameter)
                .build();
    }

    /**
     * Generate a valid {@link PaymentFormConfigurationRequest}.
     */
    public static PaymentFormConfigurationRequest aPaymentFormConfigurationRequest() {
        return aPaymentFormConfigurationRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentFormConfigurationRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder aPaymentFormConfigurationRequestBuilder() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(anOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPluginConfiguration(aPluginConfiguration());
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }

    /**
     * Generate a valid payment ID, similar to the ones the partner API would return.
     */
    public static String aPaymentId() {
        return "123456";
    }


    /**
     * Generate a valid {@link RedirectionPaymentRequest}.
     */
    public static RedirectionPaymentRequest aRedirectionPaymentRequest() {
        return RedirectionPaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(anOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withTransactionId(aTransactionId())
                .withRequestContext(aRequestContext())
                .build();
    }

    private static RequestContext aRequestContext() {
        final Map<String, String> requestData = new HashMap<>();
        requestData.put(Constants.RequestContextKeys.FINANCING_ID, "financingId");
        return RequestContext.RequestContextBuilder.aRequestContext()
                .withRequestData(requestData)
                .build();
    }

    public static PaymentResponseSuccess aPaymentResponseSuccess() {
        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withTransactionDetails(new EmptyTransactionDetails())
                .withPartnerTransactionId("financingId")
                .withStatusCode("OK")
                .build();
    }

    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.CIRCEO_SCRIPT_URL,
                "https://iam.ibmeu02.circeo.today/auth/realms/car-fr-uat/protocol/openid-connect/token");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.CIRCEO_API_URL,
                "https://recette.theloanfactory.carrefour-banque.fr/integration/service");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.CLIENT_ID, "FILL_ME_FOR_TEST_IT");  // ne pas commiter !!!!
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.OFFER_OPTIONS_AVAILABLE, "0:tbc1,1:tbc2,2:tbc3");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.DURATION_OPTIONS_AVAILABLE, "0:3,1:4");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.HTTP_CONNECTION_REQUEST_TIMEOUT, "1");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.HTTP_CONNECT_TIMEOUT, "2");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.HTTP_SOCKET_TIMEOUT, "3");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.HTTP_RETRIES, "4");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        sensitiveConfigurationMap.put(Constants.PartnerConfigurationKeys.PRIVATE_KEY, "privateKey");
        sensitiveConfigurationMap.put(Constants.PartnerConfigurationKeys.CLIENT_SECRET, "FILL_ME_FOR_TEST_IT");  // ne pas commiter !!!!

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }


    /**
     * Generate a builder for a valid {@link RetrievePluginConfigurationRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static RetrievePluginConfigurationRequest.RetrieveConfigurationRequestBuilder aRetrievePluginConfigurationRequestBuilder() {
        return RetrievePluginConfigurationRequest.RetrieveConfigurationRequestBuilder.aRetrieveConfigurationRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPluginConfiguration(aPluginConfiguration() );
    }

    /**
     * @return a valid transaction ID.
     */
    public static String aTransactionId() {
        return "financingId";
    }

    public static NotificationRequest aPaylineNotificationRequest(final String body) {
        return NotificationRequest.NotificationRequestBuilder
                .aNotificationRequest()
                .withContent(new ByteArrayInputStream(body.getBytes()))
                .withContractConfiguration(aContractConfiguration())
                .withHttpMethod("GET")
                .withPathInfo("foo")
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withHeaderInfos(new HashMap<>())
                .build();
    }

    /**
     * Generate a valid {@link TransactionStatusRequest}.
     */
    public static TransactionStatusRequest aTransactionStatusRequest() {
        return TransactionStatusRequest.TransactionStatusRequestBuilder.aNotificationRequest()
                .withAmount(aPaylineAmount() )
                .withBuyer(aBuyer() )
                .withContractConfiguration(aContractConfiguration() )
                .withEnvironment(anEnvironment() )
                .withOrder(anOrder() )
                .withPartnerConfiguration(aPartnerConfiguration() )
                .withTransactionId(aTransactionId() )
                .withNeedFinalStatus(false)
                .build();
    }

    public static FinancingRequest aFinancingRequest() {
        return FinancingRequest.builder()
                .build();
    }

    public static FinancingRequestToCancel aFinancingRequestToCancel() {
        return FinancingRequestToCancel.builder()
                .build();
    }

    public static DeliveryUpdateRequestToCapture aDeliveryRequestToCapture() {
        return DeliveryUpdateRequestToCapture.builder()
                .build();
    }

    public static FinancingRequestResponse aFinancingRequestResponse() {
        return FinancingRequestResponse.builder()
                .financingId("financingId")
                .redirectionUrl("http://url.com")
                .build();
    }

    public static FinancingRequestStatus aFinancingRequestStatus() {
        return FinancingRequestStatus.builder()
                .financingId("financingId")
                .state(State.FINANCED)
                .build();
    }

    /**
     * Generate a unique identifier that matches the API expectations.
     */
    public static String aUniqueIdentifier() {
        return "MONEXT" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * @return a valid user agent.
     */
    public static String aUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0";
    }

    public static CancelationResponse aCancelationSucessResponse() {
        return CancelationResponse.builder()
                .financingId("financingId")
                .cancelationRequestState(CancelationRequestState.DONE)
                .orderId("orderId")
                .reason(null)
                .build();
    }

    public static CancelationResponse aCancelationFailureResponse() {
        return CancelationResponse.builder()
                .financingId("financingId")
                .cancelationRequestState(CancelationRequestState.FAILED)
                .orderId("orderId")
                .reason(null)
                .build();
    }

    public static CancelationResponse aCancelationReceivedFailureResponse() {
        return CancelationResponse.builder()
                .financingId("financingId")
                .cancelationRequestState(CancelationRequestState.RECEIVED)
                .orderId("orderId")
                .reason(null)
                .build();
    }

    public static DeliveryUpdateResponse aDeliveryUpdateResponse() {
        return DeliveryUpdateResponse.builder()
                .financingId("C0000005")
                .deliveryRequestState(DeliveryUpdateRequestState.DONE)
                .orderId("orderId")
                .reason(null)
                .build();
    }

    public static DeliveryUpdateResponse aDeliveryUpdateFailureResponse() {
        return DeliveryUpdateResponse.builder()
                .financingId("financingId")
                .deliveryRequestState(DeliveryUpdateRequestState.FAILED)
                .orderId("orderId")
                .reason(null)
                .build();
    }

    public static DeliveryUpdateResponse aDeliveryUpdateReceivedFailureResponse() {
        return DeliveryUpdateResponse.builder()
                .financingId("financingId")
                .deliveryRequestState(DeliveryUpdateRequestState.RECEIVED)
                .orderId("orderId")
                .reason(null)
                .build();
    }

    public static ResetRequest aPaylineResetRequest(int amountToReset) {

        return ResetRequest.ResetRequestBuilder.aResetRequest()
                .withPartnerTransactionId("C0000004")
                .withAmount(aPaylineAmount(amountToReset))
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(anOrder())
                .withTotalResetedAmount(aPaylineAmount(0))
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPluginConfiguration(aPluginConfiguration())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId("PAYLINE" + timestamp).build();
    }

    public static CaptureRequest aPaylineCaptureRequest(int amountToReset) {

        return CaptureRequest.CaptureRequestBuilder.aCaptureRequest()
                .withPartnerTransactionId("C0000005")
                .withAmount(aPaylineAmount(amountToReset))
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(anOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPluginConfiguration(aPluginConfiguration())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId("PAYLINE" + timestamp).build();
    }


    public static RefundRequest aPaylineRefundRequest(int amountToReset) {

        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withPartnerTransactionId("financingId")
                .withAmount(aPaylineAmount(amountToReset))
                .withTotalRefundedAmount(aPaylineAmount(0))
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(anOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPluginConfiguration(aPluginConfiguration())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId("PAYLINE" + timestamp).build();
    }

    public static com.payline.pmapi.bean.common.Amount aPaylineAmount(int amount) {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(amount), Currency.getInstance("EUR"));
    }
}
