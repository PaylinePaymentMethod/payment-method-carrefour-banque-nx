package com.payline.payment.carrefour.banque.nx.service.impl;


import com.payline.payment.carrefour.banque.nx.MockUtils;
import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.ContractParametersRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    /* I18nService is not mocked here on purpose, to validate the existence of all
    the messages related to this class, at least in the default locale */
	
    @Mock
    private ReleaseProperties releaseProperties;

    @InjectMocks
    private ConfigurationServiceImpl underTest;

    @Test
    void getName() {
        assertEquals("Carrefour Banque NX", underTest.getName(Locale.getDefault()));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetParameters {

        private PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();

        @Test
        void shouldThrowExceptionWithLocale() {
            final UnsupportedOperationException unsupportedOperationException = assertThrows(UnsupportedOperationException.class,
                    () -> underTest.getParameters(Locale.FRENCH));
            assertEquals("Method not allowed", unsupportedOperationException.getMessage());
        }

        @ParameterizedTest
        @MethodSource("getContractParametersRequest")
        void nominal(ContractParametersRequest contractParametersRequest) {
            // when: retrieving the contract parameters
            List<AbstractParameter> parameters = underTest.getParameters(contractParametersRequest);

            assertEquals(3, parameters.size());

            assertTrue(parameters.get(0) instanceof ListBoxParameter);
            final ListBoxParameter offerIdListBoxParameter = (ListBoxParameter) parameters.get(0);
            assertEquals(Constants.ContractConfigurationKeys.OFFER_ID, offerIdListBoxParameter.getKey());
            assertTrue(offerIdListBoxParameter.isRequired());
            assertFalse(offerIdListBoxParameter.getLabel().contains("???"));
            assertFalse(offerIdListBoxParameter.getDescription().contains("???"));
            assertEquals(3, offerIdListBoxParameter.getList().size());
            assertEquals("tbc1", offerIdListBoxParameter.getList().get("0"));
            assertEquals("tbc2", offerIdListBoxParameter.getList().get("1"));
            assertEquals("tbc3", offerIdListBoxParameter.getList().get("2"));

            assertTrue(parameters.get(1) instanceof ListBoxParameter);
            final ListBoxParameter durationListBoxParameter = (ListBoxParameter) parameters.get(1);
            assertEquals(Constants.ContractConfigurationKeys.DURATION, durationListBoxParameter.getKey());
            assertTrue(durationListBoxParameter.isRequired());
            assertFalse(durationListBoxParameter.getLabel().contains("???"));
            assertFalse(durationListBoxParameter.getDescription().contains("???"));
            assertEquals(2, durationListBoxParameter.getList().size());
            assertEquals("3", durationListBoxParameter.getList().get("0"));
            assertEquals("4", durationListBoxParameter.getList().get("1"));

            assertTrue(parameters.get(2) instanceof InputParameter);
            final InputParameter merchantIdInputParameter = (InputParameter) parameters.get(2);
            assertEquals(Constants.ContractConfigurationKeys.MERCHANT_ID, merchantIdInputParameter.getKey());
            assertTrue(merchantIdInputParameter.isRequired());
            assertFalse(merchantIdInputParameter.getLabel().contains("???"));
            assertFalse(merchantIdInputParameter.getDescription().contains("???"));
        }

        /**
         * Set of ContractParametersRequest to test the getParameters() method. ZZ allows to search in the default messages.properties file.
         */
        Stream<ContractParametersRequest> getContractParametersRequest() {
            return Stream.of(
                    ContractParametersRequest.builder()
                            .partnerConfiguration(partnerConfiguration)
                            .locale(Locale.FRENCH)
                            .build(),
                    ContractParametersRequest.builder()
                            .partnerConfiguration(partnerConfiguration)
                            .locale(Locale.ENGLISH)
                            .build(),
                    ContractParametersRequest.builder()
                            .partnerConfiguration(partnerConfiguration)
                            .locale(new Locale("ZZ"))
                            .build()
            );
        }
    }

    @Test
    void getReleaseInformation() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String version = "M.m.p";

        // given: the release properties are OK
        doReturn(version).when(releaseProperties).get("release.version");
        Calendar cal = new GregorianCalendar();
        cal.set(2019, Calendar.AUGUST, 19);
        doReturn(formatter.format(cal.getTime())).when(releaseProperties).get("release.date");

        // when: calling the method getReleaseInformation
        ReleaseInformation releaseInformation = underTest.getReleaseInformation();

        // then: releaseInformation contains the right values
        assertEquals(version, releaseInformation.getVersion());
        assertEquals(2019, releaseInformation.getDate().getYear());
        assertEquals(Month.AUGUST, releaseInformation.getDate().getMonth());
        assertEquals(19, releaseInformation.getDate().getDayOfMonth());
    }

    @Nested
    class Check {

        private PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();

        @Test
        void nominal() {
            final Map<String, String> accountInfo = new HashMap<>();
            accountInfo.put(Constants.ContractConfigurationKeys.OFFER_ID, "123");
            accountInfo.put(Constants.ContractConfigurationKeys.DURATION, "1");
            accountInfo.put(Constants.ContractConfigurationKeys.MERCHANT_ID, "1");
            final ContractParametersCheckRequest request = ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                    .withPartnerConfiguration(partnerConfiguration)
                    .withLocale(Locale.FRENCH)
                    .withAccountInfo(accountInfo)
                    .withContractConfiguration(MockUtils.aContractConfiguration())
                    .withEnvironment(MockUtils.anEnvironment())
                    .build();

            final Map<String, String> errors = underTest.check(request);

            assertTrue(errors.isEmpty());
        }

        @Test
        void requiredFields() {
            final Map<String, String> accountInfo = new HashMap<>();
            accountInfo.put(Constants.ContractConfigurationKeys.MERCHANT_ID, "");
            final ContractParametersCheckRequest request = ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                    .withPartnerConfiguration(partnerConfiguration)
                    .withLocale(Locale.FRENCH)
                    .withAccountInfo(accountInfo)
                    .withContractConfiguration(MockUtils.aContractConfiguration())
                    .withEnvironment(MockUtils.anEnvironment())
                    .build();

            final Map<String, String> errors = underTest.check(request);

            assertEquals(3, errors.size());
            assertEquals("duration est obligatoire", errors.get(Constants.ContractConfigurationKeys.DURATION));
            assertEquals("merchantId est obligatoire", errors.get(Constants.ContractConfigurationKeys.MERCHANT_ID));
            assertEquals("offerId est obligatoire", errors.get(Constants.ContractConfigurationKeys.OFFER_ID));
        }
    }

}
