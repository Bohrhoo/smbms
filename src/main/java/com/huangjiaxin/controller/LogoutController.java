package com.huangjiaxin.controller;

import com.huangjiaxin.service.bill.BillServiceImpl;
import com.huangjiaxin.service.provider.ProviderServiceImpl;
import com.huangjiaxin.service.role.RoleServiceImpl;
import com.huangjiaxin.service.user.UserServiceImpl;
import com.huangjiaxin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Controller

public class LogoutController {
	@Autowired
	UserServiceImpl userService;
	@Autowired
	RoleServiceImpl roleService;
	@Autowired
	ProviderServiceImpl providerService;
	@Autowired
	BillServiceImpl billService;
	@RequestMapping(value="/logout.do",method = {RequestMethod.POST,RequestMethod.GET})
	public String  doPost(HttpServletRequest request, HttpServletResponse response)
			{
		//清除session
		request.getSession().removeAttribute(Constants.USER_SESSION);
		return "redirect:/login.jsp";
	}



}
