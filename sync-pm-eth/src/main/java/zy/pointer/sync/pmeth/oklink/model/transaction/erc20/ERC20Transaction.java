package zy.pointer.sync.pmeth.oklink.model.transaction.erc20;

import lombok.Data;
import zy.pointer.sync.pmeth.oklink.model.transaction.Tag;

import java.util.List;

/**
 *          "blockHash": "0xb178b511069703039b2c3129c8ae2efdd15d54b04eb5cda9058846c8559e5293",
 * 			"blockHeight": 12626301,
 * 			"blocktime": 1623589539,
 * 			"tokenContractAddress": "0xdac17f958d2ee523a2206206994597c13d831ec7",
 * 			"logIndex": 48,
 * 			"txhash": "0xf592f4ae88457f4cb539edf2838858a7838f1df7c74e5ed1c3887ec239632a53",
 * 			"from": "0x49c08dfc07b68d517d89a5287a9e503f25769a8a",
 * 			"to": "0x75e89d5979e4f6fba9f97c104c2f0afb3f1dcb88",
 * 			"value": 1894.644859,
 * 			"legalRate": 2353.0,
 * 			"txindex": 11,
 * 			"tokenType": "ERC20",
 * 			"isFromContract": false,
 * 			"isToContract": false,
 * 			"hour": 21,
 * 			"symbol": "USDT",
 * 			"logoUrl": "http://static.coinall.ltd/cdn/explorer/ethToken/ETH_USDT_0xdac17f958d2ee523a2206206994597c13d831ec7.jpg",
 * 			"confirm": 154,
 * 			"realValue": 0.0,
 * 			"fromTag": [{
 * 				"tag": "MXC",
 * 				"logo": "",
 * 				"project": "Exchange",
 * 				"item": "",
 * 				"type": "User"
 *                        }],
 * 			"toTag": [{
 * 				"tag": "MXC",
 * 				"logo": "",
 * 				"project": "Exchange",
 * 				"item": "",
 * 				"type": "Deposit"
 *            }],
 * 			"fromTokenUrl": "",
 * 			"toTokenUrl": ""
 */
@Data
public class ERC20Transaction {

    private Long blockHeight;

    private Long blocktime;

    private String txhash;

    private String from;

    private String to;

    private String tokenContractAddress;

    private String value;

    private List<Tag> fromTag;

    private List<Tag> toTag;

}
