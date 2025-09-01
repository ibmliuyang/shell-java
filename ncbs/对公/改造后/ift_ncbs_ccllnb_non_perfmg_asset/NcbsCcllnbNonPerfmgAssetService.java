package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbNonPerfmgAsset;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbNonPerfmgAssetOutVO;
import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import java.util.List;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.common.base.BaseQuery;

/**
 * 对公贷款不良资产表服务接口
 */
public interface NcbsCcllnbNonPerfmgAssetService extends BaseService<NcbsCcllnbNonPerfmgAsset, Long>{
    Page<NcbsCcllnbNonPerfmgAssetOutVO> listPage(BaseQuery<NcbsCcllnbNonPerfmgAsset> vo, UserVO userVO) throws Exception;
    @Override
    void deleteById(Long id);
    NcbsCcllnbNonPerfmgAsset saveOrUpdate(NcbsCcllnbNonPerfmgAsset entity, UserVO userVO) throws Exception;
    NcbsCcllnbNonPerfmgAssetOutVO queryInfoById(Long id) throws Exception;
    void deleteAllByIds(List<Long> ids) throws Exception;
    DataSyncResult syncData(DataSyncRequest request) throws Exception;
}