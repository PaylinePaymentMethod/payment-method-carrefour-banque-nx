package com.payline.payment.carrefour.banque.nx.mapper;

import com.payline.pmapi.bean.common.Amount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
@SuppressWarnings("squid:S1214")
public interface AmountMapper {

    AmountMapper INSTANCE = Mappers.getMapper(AmountMapper.class);

    default String mapAmount(final Amount amount) {
        final int nbDigits = amount.getCurrency().getDefaultFractionDigits();

        final StringBuilder sb = new StringBuilder();
        sb.append(amount.getAmountInSmallestUnit());

        for (int i = sb.length(); i < nbDigits + 1; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - nbDigits, ".");
        return sb.toString();
    }
}
