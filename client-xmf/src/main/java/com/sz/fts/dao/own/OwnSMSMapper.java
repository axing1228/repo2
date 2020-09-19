package com.sz.fts.dao.own;

import com.sz.fts.bean.own.OwnSMS;

public interface OwnSMSMapper {
	int deleteByPrimaryKey(Integer smsNo);

	int insert(OwnSMS record);

	int insertSelective(OwnSMS record);

	OwnSMS selectByPrimaryKey(Integer smsNo);

	int updateByPrimaryKeySelective(OwnSMS record);

	int updateByPrimaryKey(OwnSMS record);

	/**
	 * 根据手机号更新数据
	 * 
	 * @param save
	 * @return
	 * @author 杨坚
	 * @Time 2017年2月28日
	 * @version 1.0v
	 */
	int updateSMSByTelephone(OwnSMS save);

	/**
	 * 添加验证信息
	 * 
	 * @param add
	 * @return
	 * @author 杨坚
	 * @Time 2017年2月28日
	 * @version 1.0v
	 */
	int insertSMSInfo(OwnSMS add);

	/**
	 * 根据手机查询信息
	 * 
	 * @param telephone
	 * @return
	 * @author 杨坚
	 * @Time 2017年2月28日
	 * @version 1.0v
	 */
	OwnSMS findSMSByTelephone(String telephone);

	/**
	 * 根据手机号删除信息
	 * 
	 * @param telephone
	 * @return
	 * @author 杨坚
	 * @Time 2017年2月28日
	 * @version 1.0v
	 */
	int deleteSMSByTelephone(String telephone);
}