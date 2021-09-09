package zy.pointer.crps.commons.business.cryptotx.syncpm;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleTxModel {

    // 交易哈希
    private String txHash;

    private String from;

    private String to;

    private String token;

    private String amount;

    private String fromTag;

    private String toTag;

    private Long txTime;

    public SimpleTxModel(){}

    public SimpleTxModel(String txHash, String from, String to, String token, String amount) {
        this.txHash = txHash;
        this.from = from;
        this.to = to;
        this.token = token;
        this.amount = amount;
    }

}
