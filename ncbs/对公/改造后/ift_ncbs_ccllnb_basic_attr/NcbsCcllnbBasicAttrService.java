package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbBasicAttrOutVO;
import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import java.util.List;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.common.base.BaseQuery;

/**
 * 对公贷款属性表服务接口
 */
public interface NcbsCcllnbBasicAttrService extends BaseService<NcbsCcllnbBasicAttr, Long>{
    Page<NcbsCcllnbBasicAttrOutVO> listPage(BaseQuery<NcbsCcllnbBasicAttr> vo, UserVO userVO) throws Exception;
    @Override
    void deleteById(Long id);
    NcbsCcllnbBasicAttr saveOrUpdate(NcbsCcllnbBasicAttr entity, UserVO userVO) throws Exception;
    NcbsCcllnbBasicAttrOutVO queryInfoById(Long id) throws Exception;
    void deleteAllByIds(List<Long> ids) throws Exception;
    DataSyncResult syncData(DataSyncRequest request) throws Exception;
}