package zy.pointer.crps.commons.business.cryptotx.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.EthBlockTransactionMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.EthBlockTransaction;
import zy.pointer.crps.commons.business.cryptotx.service.IEthBlockTransactionService;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;

@Primary
@Transactional
@Service
public class EthBlockTransactionServiceImpl extends AbsBusinessService<EthBlockTransactionMapper , EthBlockTransaction> implements IEthBlockTransactionService {



}
