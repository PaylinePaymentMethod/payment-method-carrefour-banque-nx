package com.payline.payment.carrefour.banque.nx.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public final class Constants {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static final class ContractConfigurationKeys {

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
    public static final class PartnerConfigurationKeys {

        public static final String CIRCEO_SCRIPT_URL = "CIRCEO_SCRIPT_URL";

        public static final String CIRCEO_API_URL = "CIRCEO_API_URL";

        public static final String CLIENT_ID = "CLIENT_ID";

        public static final String PRIVATE_KEY = "PRIVATE_KEY";

        public static final String CLIENT_SECRET = "CLIENT_SECRET";

        public static final String OFFER_OPTIONS_AVAILABLE = "OFFER_OPTIONS_AVAILABLE";

        public static final String DURATION_OPTIONS_AVAILABLE = "DURATION_OPTIONS_AVAILABLE";

        public static final String HTTP_CONNECTION_REQUEST_TIMEOUT = "HTTP_CONNECTION_REQUEST_TIMEOUT";

        public static final String HTTP_CONNECT_TIMEOUT = "HTTP_CONNECT_TIMEOUT";

        public static final String HTTP_SOCKET_TIMEOUT = "HTTP_SOCKET_TIMEOUT";

        public static final String HTTP_RETRIES = "HTTP_RETRIES";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in RequestContext data.
     */
    public static final class RequestContextKeys {

        public static final String FINANCING_ID = "FINANCING_ID";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys() {
        }
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants() {
    }

}
