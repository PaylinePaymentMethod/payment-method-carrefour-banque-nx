package com.payline.payment.carrefour.banque.nx.service.impl;

import com.payline.payment.carrefour.banque.nx.utils.Constants;
import com.payline.payment.carrefour.banque.nx.utils.PluginUtils;
import com.payline.payment.carrefour.banque.nx.utils.i18n.I18nService;
import com.payline.payment.carrefour.banque.nx.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.request.ContractParametersRequest;
import com.payline.pmapi.service.ConfigurationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {

    private static final String I18N_CONTRACT_PREFIX = "contract.";

    private I18nService i18n = I18nService.getInstance();
    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();


    @Override
    public List<AbstractParameter> getParameters(final Locale locale) {
        throw new UnsupportedOperationException("Method not allowed");
    }

    @Override
    public List<AbstractParameter> getParameters(ContractParametersRequest request) {
        final Map<String, String> offerOptions = fetchOptionsFromPartnerConf(Constants.PartnerConfigurationKeys.OFFER_OPTIONS_AVAILABLE, request.getPartnerConfiguration());
        final Map<String, String> durationOptions = fetchOptionsFromPartnerConf(Constants.PartnerConfigurationKeys.DURATION_OPTIONS_AVAILABLE, request.getPartnerConfiguration());

        final Locale locale = request.getLocale();
        final List<AbstractParameter> parameters = new ArrayList<>();
        parameters.add(newListBoxParameter(Constants.ContractConfigurationKeys.OFFER_ID, offerOptions, true, locale));
        parameters.add(newListBoxParameter(Constants.ContractConfigurationKeys.DURATION, durationOptions, true, locale));
        parameters.add(newInputParameter(Constants.ContractConfigurationKeys.MERCHANT_ID, true, locale));
        return parameters;
    }

    @Override
    public Map<String, String> check(final ContractParametersCheckRequest contractParametersCheckRequest) {
        final Map<String, String> errors = new HashMap<>();
        final Map<String, String> accountInfo = contractParametersCheckRequest.getAccountInfo();
        final Locale locale = contractParametersCheckRequest.getLocale();
        final ContractParametersRequest contractParametersRequest = ContractParametersRequest.builder()
                .partnerConfiguration(contractParametersCheckRequest.getPartnerConfiguration())
                .locale(contractParametersCheckRequest.getLocale())
                .build();
        // check required fields
        for (final AbstractParameter param : this.getParameters(contractParametersRequest)) {
            if (param.isRequired() && accountInfo.get(param.getKey()) == null) {
                final String message = i18n.getMessage(I18N_CONTRACT_PREFIX + param.getKey() + ".requiredError", locale);
                errors.put(param.getKey(), message);
            }
        }
        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                                                .withDate(LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                                .withVersion(releaseProperties.get("release.version"))
                                                .build();
    }

    @Override
    public String getName(final Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }

    /**
     * Build and return a new <code>InputParameter</code> for the contract configuration.
     *
     * @param key      The parameter key
     * @param required Is this parameter required ?
     * @param locale   The current locale
     * @return The new input parameter
     */
    private InputParameter newInputParameter(final String key, final boolean required, final Locale locale) {
        final InputParameter inputParameter = new InputParameter();
        inputParameter.setKey(key);
        inputParameter.setLabel(i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".label", locale));
        inputParameter.setDescription(i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".description", locale));
        inputParameter.setRequired(required);
        return inputParameter;
    }

    /**
     * Build and return a new <code>ListBoxParameter</code> for the contract configuration.
     *
     * @param key          The parameter key
     * @param values       All the possible values for the list box
     * @param required     Is this parameter required ?
     * @param locale       The current locale
     * @return The new list box parameter
     */
    private ListBoxParameter newListBoxParameter(final String key, final Map<String, String> values, final boolean required, final Locale locale) {
        final ListBoxParameter listBoxParameter = new ListBoxParameter();
        listBoxParameter.setKey(key);
        listBoxParameter.setLabel(i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".label", locale));
        listBoxParameter.setDescription(i18n.getMessage(I18N_CONTRACT_PREFIX + key + ".description", locale));
        listBoxParameter.setList(values);
        listBoxParameter.setRequired(required);
        return listBoxParameter;
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
    protected Map<String, String> fetchOptionsFromPartnerConf(final String key, final PartnerConfiguration partnerConfiguration) {
        final Map<String, String> optionsMap = new HashMap<>();
        final String options = partnerConfiguration.getProperty(key);
        if (!PluginUtils.isEmpty(options)) {
            final List <String> optionsList = Arrays.asList(options.split(","));
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


