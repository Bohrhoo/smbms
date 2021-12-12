package com.huangjiaxin.dao;

import com.huangjiaxin.pojo.Bill;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BillDao {
	public int add(Bill bill);
	public List<Bill> getBillList(@Param("productName") String productName,
								  @Param("providerId") Integer providerId,
								  @Param("isPayment") Integer isPayment,
								  @Param("currentPageNo") int currentPageNo,
								  @Param("pageSize") int pageSize);

	public int deleteBillById(String delId);
	public Bill getBillById(String id);
	public int modify(Bill bill);
	public int getBillCountByProviderId(String providerId);
	int getBillCount();

}
