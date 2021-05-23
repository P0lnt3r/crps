package zy.pointer.crps.commons.business.cryptotx.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zy.pointer.crps.commons.business.cryptotx.repository.mapper.AddressTxMapper;
import zy.pointer.crps.commons.business.cryptotx.repository.model.AddressTx;
import zy.pointer.crps.commons.business.cryptotx.service.IAddressTxService;
import zy.pointer.crps.commons.framework.business.AbsBusinessService;

@Primary
@Service
@Transactional
public class AddressTxServiceImpl extends AbsBusinessService<AddressTxMapper , AddressTx> implements IAddressTxService {



}
