package zy.pointer.crps.commons.business.cryptotx.repository.model.extras;

import lombok.Data;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthAddressTx;

@Data
public class EthAddressTx_Statistics extends EthAddressTx {

    private Integer txCount;

    private String sumAmount;

}
