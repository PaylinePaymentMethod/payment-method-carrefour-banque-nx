package com.payline.payment.carrefour.banque.nx.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public class Constants {

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static class ContractConfigurationKeys {

        public static final String OFFER_ID = "offerId";

        public static final String DURATION = "duration";

        public static final String MERCHANT_ID = "merchantId";


        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private ContractConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in PartnerConfiguration maps.
     */
    public static class PartnerConfigurationKeys {


        public static final String CIRCEO_SCRIPT_URL = "CIRCEO_SCRIPT_URL";

        public static final String CLIENT_ID = "CLIENT_ID";

        public static final String PRIVATE_KEY = "PRIVATE_KEY";

        public static final String CLIENT_SECRET = "CLIENT_SECRET";

        public static final String OFFER_OPTIONS_AVAILABLE = "OFFER_OPTIONS_AVAILABLE";

        public static final String DURATION_OPTIONS_AVAILABLE = "DURATION_OPTIONS_AVAILABLE";


        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in RequestContext data.
     */
    public static class RequestContextKeys {

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys() {
        }
    }

    /**
     * Keys for form.
     */
    public static class FormKeys {


        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private FormKeys() {
        }
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants() {
    }

}
