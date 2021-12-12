package com.huangjiaxin.dao;

import com.huangjiaxin.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
	int countUser();
	List<User> getAllUser();
	User getUserById(String id);
	int add(User user);
	User getLoginUser(String userCode);
	User getLoginUser(@Param("userCode") String userCode, @Param("userPassword") String userPassword);
	List<User> getUserList(@Param("userName") String userName,
						   @Param("userRole") int userRole,
						   @Param("currentPageNo") int currentPageNo,
						   @Param("pageSize") int pageSize);
	int getUserCount(@Param("userName") String userName, @Param("userRole") int userRole);
	int deleteUserById(Integer delId);
	int modify(User user);
	public int updatePwd(@Param("id") int id, @Param("pwd") String pwd);
}
