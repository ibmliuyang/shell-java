package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbChkWriteOff;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbChkWriteOffOutVO;
import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import java.util.List;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.common.base.BaseQuery;

/**
 * 对公贷款核销表服务接口
 */
public interface NcbsCcllnbChkWriteOffService extends BaseService<NcbsCcllnbChkWriteOff, Long>{
    Page<NcbsCcllnbChkWriteOffOutVO> listPage(BaseQuery<NcbsCcllnbChkWriteOff> vo, UserVO userVO) throws Exception;
    @Override
    void deleteById(Long id);
    NcbsCcllnbChkWriteOff saveOrUpdate(NcbsCcllnbChkWriteOff entity, UserVO userVO) throws Exception;
    NcbsCcllnbChkWriteOffOutVO queryInfoById(Long id) throws Exception;
    void deleteAllByIds(List<Long> ids) throws Exception;
    DataSyncResult syncData(DataSyncRequest request) throws Exception;
}