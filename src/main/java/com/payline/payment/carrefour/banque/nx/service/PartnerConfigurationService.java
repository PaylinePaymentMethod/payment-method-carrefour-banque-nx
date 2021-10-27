package com.payline.payment.carrefour.banque.nx.service;

import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerConfigurationService {
    private static class Holder {
        private static final PartnerConfigurationService INSTANCE = new PartnerConfigurationService();
    }

    public static PartnerConfigurationService getInstance() {
        return PartnerConfigurationService.Holder.INSTANCE;
    }

    PartnerConfigurationService() {
    }


    /**
     * Methode permettant d'extraire les options des partners configurations
     * @param key
     *      La clé des partners configurations contenant les options
     * @param partnerConfiguration
     *      Les partners configurations.
     * @return
     *      La liste des options possibles pour le paramètre du contrat.
     */
    public Map<String, String> fetchOptionsFromPartnerConf(final String key, final PartnerConfiguration partnerConfiguration) {
        final Map<String, String> optionsMap = new HashMap<>();
        final String options = partnerConfiguration.getProperty(key);
        if (!PluginUtils.isEmpty(options)) {
            final List<String> optionsList = Arrays.asList(options.split(","));
            optionsList.forEach(e -> {
                final String[] option = e.split(":");
                if (option.length == 2) {
                    optionsMap.put(option[0], option[1]);
                }
            });
        }
        return optionsMap;
    }
}
