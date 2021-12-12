package com.huangjiaxin.service.bill;

import com.huangjiaxin.dao.BillDao;
import com.huangjiaxin.pojo.Bill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BillService {
	/**
	 * 增加订单
	 * @param bill
	 * @return
	 */
	public boolean add(Bill bill);


	/**
	 * 通过条件获取订单列表-模糊查询-billList
	 * @param bill
	 * @return
	 */
	public List<Bill> getBillList(String productName, Integer providerId, Integer isPayment,int currentPageNo, int pageSize);
	
	/**
	 * 通过billId删除Bill
	 * @param delId
	 * @return
	 */
	public boolean deleteBillById(String delId);
	
	
	/**
	 * 通过billId获取Bill
	 * @param id
	 * @return
	 */
	public Bill getBillById(String id);
	
	/**
	 * 修改订单信息
	 * @param bill
	 * @return
	 */
	public boolean modify(Bill bill);


	public int getBillCount();
}
