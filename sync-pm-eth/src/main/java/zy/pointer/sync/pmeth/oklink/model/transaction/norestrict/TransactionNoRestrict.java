package zy.pointer.sync.pmeth.oklink.model.transaction.norestrict;

import lombok.Data;
import zy.pointer.sync.pmeth.oklink.model.transaction.Tag;

import java.util.List;

/**
 *          "hash": "0x589c9fd49685bce958d5da5768cd97ea5974c4309c0ca92da55eb8b9a1ffd005",
 * 			"blocktime": 1623589539,
 * 			"index": 3,
 * 			"blockHash": "0xb178b511069703039b2c3129c8ae2efdd15d54b04eb5cda9058846c8559e5293",
 * 			"blockHeight": 12626301,
 * 			"totalIndex": 1170846913,
 * 			"hour": 21,
 * 			"from": "0x099a3c7d59c070ba3fea75337830c6dd7ab334f8",
 * 			"to": "0x0f5d2fb29fb7d3cfee444a200298f468908cc942",
 * 			"fee": 0.0021645351,
 * 			"value": 0.0,
 * 			"isContractCall": true,
 * 			"status": "0x1",
 * 			"isFromContract": false,
 * 			"isToContract": true,
 * 			"gasLimit": 200000,
 * 			"gasUsed": 54249,
 * 			"gasPrice": 39900000000,
 * 			"cumulativeGasUsed": 638023,
 * 			"nonce": 1010,
 * 			"inputHex": "0xa9059cbb00000000000000000000000006fb2b1d066cb71758b05067fe478f672060389b00000000000000000000000000000000000000000000002f6f326aa3117bd000",
 * 			"inputData": "\u0000�\u0005��\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0006�+\u001D\u0006l�\u0017X�Pg�G�g `8�\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000/o2j�\u0011{�\u0000",
 * 			"v": 38,
 * 			"r": "0x330f0ca1b720d7ae5a110e872e277247ea66845294c39c2cb00871aa89c5b214",
 * 			"s": "0x3766aa1291703886c4e576ae566e000bee78950a2ffaa1e401eae80e5641930",
 * 			"erc20TokenTransferCount": 1,
 * 			"erc721TokenTransferCount": 0,
 * 			"internalTranCount": 0,
 * 			"valueTotal": 0.0021645351,
 * 			"internalValueTotal": 0.0,
 * 			"confirm": 389,
 * 			"realValue": 0.0,
 * 			"fromTag": [],
 * 			"toTag": [{
 * 				"tag": "Decentraland Token",
 * 				"logo": "",
 * 				"project": "Game",
 * 				"item": "",
 * 				"type": ""
 *                        }],
 * 			"fromTokenUrl": "",
 * 			"toTokenUrl": "https://static.coinall.ltd/cdn/explorer/ethToken/ETH_MANA_0x0f5d2fb29fb7d3cfee444a200298f468908cc942.jpg",
 * 			"legalRate": 0.0
 */
@Data
public class TransactionNoRestrict {

    private String hash;

    private Long blocktime;

    private String from;

    private String to;

    private Boolean isContractCall;

    private String value;

    private List<Tag> fromTag;

    private List<Tag> toTag;

}
