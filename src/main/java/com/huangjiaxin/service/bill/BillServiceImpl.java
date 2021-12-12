package com.huangjiaxin.service.bill;

import com.huangjiaxin.dao.BillDao;
import com.huangjiaxin.pojo.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillDao billDao;
    public boolean add(Bill bill) {
        return billDao.add(bill)>=1?true:false;
    }
    public List<Bill> getBillList(String productName, Integer providerId, Integer isPayment,int currentPageNo, int pageSize){
        return billDao.getBillList(productName,providerId,isPayment,(currentPageNo-1)*pageSize,pageSize);
    }
    public boolean deleteBillById(String delId) {
        return billDao.deleteBillById(delId)>=1?true:false;
    }
    public Bill getBillById(String id) {
        return billDao.getBillById(id);
    }
    public boolean modify(Bill bill) {
        return billDao.modify(bill)>=1?true:false;
    }
    @Override
    public int getBillCount() {
        return billDao.getBillCount();
    }
}
