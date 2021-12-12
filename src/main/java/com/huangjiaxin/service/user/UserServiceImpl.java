package com.huangjiaxin.service.user;

import com.huangjiaxin.dao.UserDao;
import com.huangjiaxin.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserDao userDao;
    public boolean add(User user) {
//        userDao.add(user);
//        int a=1/0;
        return userDao.add(user)>=1?true:false;
    }

    @Override
    public User login(String userCode, String userPassword) {
        System.out.println(userCode+"==================="+userPassword);
        return userDao.getLoginUser(userCode,userPassword);
    }

    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        System.out.println("userlist :"+userDao.getUserList(queryUserName,queryUserRole,(currentPageNo-1)*pageSize,pageSize));
        return userDao.getUserList(queryUserName,queryUserRole,(currentPageNo-1)*pageSize,pageSize);
    }

    @Override
    public int getUserCount(String queryUserName, int queryUserRole) {
        return userDao.getUserCount(queryUserName,queryUserRole);
    }

    @Override
    public User selectUserCodeExist(String userCode) {
        return userDao.getLoginUser(userCode);
    }

    @Override
    public boolean deleteUserById(Integer delId) {
        return userDao.deleteUserById(delId)>=1?true:false;
    }

    @Override
    public User getUserById(String id) {
        System.out.println("00000000000 :"+userDao.getUserById(id));
        return userDao.getUserById(id);
    }

    @Override
    public boolean modify(User user) {
        return userDao.modify(user)>=1?true:false;
    }

    @Override
    public boolean updatePwd(int id, String pwd) {
        return userDao.updatePwd(id, pwd)>=1?true:false;
    }
}
