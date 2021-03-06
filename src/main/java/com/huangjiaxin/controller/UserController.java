package com.huangjiaxin.controller;

import com.alibaba.fastjson.JSONArray;
import com.huangjiaxin.pojo.Role;
import com.huangjiaxin.pojo.User;
import com.huangjiaxin.service.bill.BillServiceImpl;
import com.huangjiaxin.service.provider.ProviderServiceImpl;
import com.huangjiaxin.service.role.RoleServiceImpl;
import com.huangjiaxin.service.user.UserServiceImpl;
import com.huangjiaxin.utils.Constants;
import com.huangjiaxin.utils.PageSupport;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
	@Autowired
	BillServiceImpl billService;
	@Autowired
	ProviderServiceImpl providerService;
	@Autowired
	UserServiceImpl userService;
	@Autowired
	RoleServiceImpl roleService;
	@RequestMapping(value="/user.do",method = {RequestMethod.POST,RequestMethod.GET})
	public String doPost(HttpSession session,User user, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, @RequestParam(value="method",required = false) String method, Model model)
			throws ServletException, IOException {
		System.out.println("--------------->"+method);
		if(method != null && method.equals("add")){
			return this.add(request, response,user,bindingResult,session);
		}else if(method != null && method.equals("query")){
			return this.query(request, response);
		}else if(method != null && method.equals("getrolelist")){
			this.getRoleList(request, response);
		}else if(method != null && method.equals("ucexist")){
			this.userCodeExist(request, response);
		}else if(method != null && method.equals("deluser")){
			this.delUser(request, response);
		}else if(method != null && method.equals("view")){
			return this.getUserById(request, response,"userview",model);
		}else if(method != null && method.equals("modify")){
			return this.getUserById(request, response,"usermodify",model);
		}else if(method != null && method.equals("modifyexe")){
			return this.modify(request, response);
		}else if(method != null && method.equals("pwdmodify")){
			this.getPwdByUserId(request, response);
		}else if(method != null && method.equals("savepwd")){
			return this.updatePwd(request, response);
		}
		return "error";

	}
	private String updatePwd(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Object o = request.getSession().getAttribute(Constants.USER_SESSION);
		String newpassword = request.getParameter("newpassword");
		boolean flag = false;
		if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
			flag = userService.updatePwd(((User)o).getId(),newpassword);
			if(flag){
				request.setAttribute(Constants.SYS_MESSAGE, "??????????????????,??????????????????????????????????????????");
				request.getSession().removeAttribute(Constants.USER_SESSION);//session??????
			}else{
				request.setAttribute(Constants.SYS_MESSAGE, "?????????????????????");
			}
		}else{

			request.setAttribute(Constants.SYS_MESSAGE, "?????????????????????");
		}
		return "pwdmodify";

	}

	private void getPwdByUserId(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Object o = request.getSession().getAttribute(Constants.USER_SESSION);
		String oldpassword = request.getParameter("oldpassword");
		Map<String, String> resultMap = new HashMap<String, String>();

		if(null == o ){//session??????
			resultMap.put("result", "sessionerror");
		}else if(StringUtils.isNullOrEmpty(oldpassword)){//?????????????????????
			resultMap.put("result", "error");
		}else{
			String sessionPwd = ((User)o).getUserPassword();
			if(oldpassword.equals(sessionPwd)){
				resultMap.put("result", "true");
			}else{//????????????????????????
				resultMap.put("result", "false");
			}
		}

		response.setContentType("application/json");
		PrintWriter outPrintWriter = response.getWriter();
		outPrintWriter.write(JSONArray.toJSONString(resultMap));
		outPrintWriter.flush();
		outPrintWriter.close();
	}


	private String modify(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("uid");
		String userName = request.getParameter("userName");
		String gender = request.getParameter("gender");
		String birthday = request.getParameter("birthday");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");
		String userRole = request.getParameter("userRole");

		User user = new User();
		user.setId(Integer.valueOf(id));
		user.setUserName(userName);
		user.setGender(Integer.valueOf(gender));
		try {
			user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setPhone(phone);
		user.setAddress(address);
		user.setUserRole(Integer.valueOf(userRole));
		user.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		user.setModifyDate(new Date());
		if(userService.modify(user)){
			return "redirect:/user.do?method=query";
//			response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
		}else{
			return "usermodify";
//			request.getRequestDispatcher("usermodify.jsp").forward(request, response);
		}

	}

	private String getUserById(HttpServletRequest request, HttpServletResponse response,String url,Model model)
			throws ServletException, IOException {
		String id = request.getParameter("uid");

		if(!StringUtils.isNullOrEmpty(id)){
			//????????????????????????user??????

			User user = userService.getUserById(id);
			System.out.println("------------> "+user);
			model.addAttribute("user", user);
			return url;
//			request.getRequestDispatcher(url).forward(request, response);
		}
		return "error";
	}
	private void delUser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("uid");
		Integer delId = 0;
		try{
			delId = Integer.parseInt(id);
		}catch (Exception e) {
			// TODO: handle exception
			delId = 0;
		}
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(delId <= 0){
			resultMap.put("delResult", "notexist");
		}else{

			if(userService.deleteUserById(delId)){
				resultMap.put("delResult", "true");
			}else{
				resultMap.put("delResult", "false");
			}
		}

		//???resultMap?????????json????????????
		response.setContentType("application/json");
		PrintWriter outPrintWriter = response.getWriter();
		outPrintWriter.write(JSONArray.toJSONString(resultMap));
		outPrintWriter.flush();
		outPrintWriter.close();
	}

	private void userCodeExist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//??????????????????????????????
		String userCode = request.getParameter("userCode");

		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(StringUtils.isNullOrEmpty(userCode)){
			//userCode == null || userCode.equals("")
			resultMap.put("userCode", "exist");
		}else{
			User user = userService.selectUserCodeExist(userCode);
			if(null != user){
				resultMap.put("userCode","exist");
			}else{
				resultMap.put("userCode", "notexist");
			}
		}

		//???resultMap??????json????????????json???????????????
		//??????????????????????????????
		response.setContentType("application/json");
		//???response??????????????????????????????writer??????
		PrintWriter outPrintWriter = response.getWriter();
		//???resultMap??????json????????? ??????
		outPrintWriter.write(JSONArray.toJSONString(resultMap));
		outPrintWriter.flush();//??????
		outPrintWriter.close();//?????????
	}
	private void getRoleList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		//???roleList?????????json????????????
		response.setContentType("application/json");
		PrintWriter outPrintWriter = response.getWriter();
		outPrintWriter.write(JSONArray.toJSONString(roleList));
		outPrintWriter.flush();
		outPrintWriter.close();
	}

	private String query(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//??????????????????
		String queryUserName = request.getParameter("queryname");
		String temp = request.getParameter("queryUserRole");
		String pageIndex = request.getParameter("pageIndex");
		int queryUserRole = 0;
		List<User> userList = null;
		//??????????????????
		int pageSize = Constants.pageSize;
		//????????????
		int currentPageNo = 1;
//		System.out.println("queryUserName servlet--------"+queryUserName);
//		System.out.println("queryUserRole servlet--------"+queryUserRole);
//		System.out.println("query pageIndex--------- > " + pageIndex);
		if(queryUserName == null){
			queryUserName = "";
		}
		if(temp != null && !temp.equals("")){
			queryUserRole = Integer.parseInt(temp);
		}

		if(pageIndex != null){
			try{
				currentPageNo = Integer.valueOf(pageIndex);
			}catch(NumberFormatException e){
				return "error";
			}
		}
		//??????????????????
		int totalCount	= userService.getUserCount(queryUserName,queryUserRole);

		//?????????
		PageSupport pages=new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);

		int totalPageCount = pages.getTotalPageCount();

		//?????????????????????
		if(currentPageNo < 1){
			currentPageNo = 1;
		}else if(currentPageNo > totalPageCount){
			currentPageNo = totalPageCount;
		}


		userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo, pageSize);
		request.setAttribute("userList", userList);
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		request.setAttribute("roleList", roleList);
		request.setAttribute("queryUserName", queryUserName);
		request.setAttribute("queryUserRole", queryUserRole);
		request.setAttribute("totalPageCount", totalPageCount);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("currentPageNo", currentPageNo);
		return "userlist";
	}

	private String add( HttpServletRequest request, HttpServletResponse response,User user,BindingResult bindingResult,HttpSession session)
			throws ServletException, IOException {
		if(bindingResult.hasErrors()){
			System.out.println("????????????");
			return "useradd";
		}System.out.println("111");
		user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());System.out.println("222");
		user.setCreationDate(new Date());System.out.println("333");
		if(userService.add(user)){return "redirect:/user.do?method=query";}System.out.println("444");
		return "useradd";
	}
}
