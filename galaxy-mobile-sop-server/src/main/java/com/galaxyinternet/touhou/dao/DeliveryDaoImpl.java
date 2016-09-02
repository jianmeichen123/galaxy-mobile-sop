package com.galaxyinternet.touhou.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.galaxyinternet.bo.touhou.DeliveryBo;
import com.galaxyinternet.dao.touhou.DeliveryDao;
import com.galaxyinternet.framework.core.constants.SqlId;
import com.galaxyinternet.framework.core.dao.impl.BaseDaoImpl;
import com.galaxyinternet.framework.core.exception.DaoException;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.model.touhou.Delivery;


@Repository("deliveryDao")
public class DeliveryDaoImpl extends BaseDaoImpl<Delivery, Long> implements DeliveryDao {
	
	@Override
	public Page<DeliveryBo> selectDeliveryPageList(DeliveryBo query,Pageable pageable) {
		try {
			List<DeliveryBo> contentList = sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT),getParams(query, pageable));
			return new Page<DeliveryBo>(contentList, pageable,this.selectCount(query));
		} catch (Exception e) {
			throw new DaoException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}
	

}