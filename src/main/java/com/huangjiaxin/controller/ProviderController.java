package com.huangjiaxin.controller;

import com.alibaba.fastjson.JSONArray;
import com.huangjiaxin.pojo.Provider;
import com.huangjiaxin.pojo.User;
import com.huangjiaxin.service.bill.BillServiceImpl;
import com.huangjiaxin.service.provider.ProviderServiceImpl;
import com.huangjiaxin.service.user.UserServiceImpl;
import com.huangjiaxin.utils.Constants;
import com.huangjiaxin.utils.PageSupport;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class ProviderController {
	@Autowired
	BillServiceImpl billService;
	@Autowired
	ProviderServiceImpl providerService;
	@Autowired
	UserServiceImpl userService;

	@RequestMapping(value="/provider.do",method = {RequestMethod.POST,RequestMethod.GET})
	public String doPost(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="method",required = false) String method)
			throws ServletException, IOException {
		if(method != null && method.equals("query")){
			return this.query(request,response);
		}else if(method != null && method.equals("add")){
			return this.add(request,response);
		}else if(method != null && method.equals("view")){
			return this.getProviderById(request,response,"providerview");
		}else if(method != null && method.equals("modify")){
			return this.getProviderById(request,response,"providermodify");
		}else if(method != null && method.equals("modifysave")){
			return this.modify(request,response);
		}else if(method != null && method.equals("delprovider")){
			 this.delProvider(request,response);
		}
		return "error";
	}

	private void delProvider(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("proid");
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(!StringUtils.isNullOrEmpty(id)){

			int flag = providerService.deleteProviderById(id);
			if(flag == 0){//????????????
				resultMap.put("delResult", "true");
			}else if(flag == -1){//????????????
				resultMap.put("delResult", "false");
			}else if(flag > 0){//?????????????????????????????????????????????????????????
				resultMap.put("delResult", String.valueOf(flag));
			}
		}else{
			resultMap.put("delResult", "notexit");
		}
		//???resultMap?????????json????????????
		response.setContentType("application/json");
		PrintWriter outPrintWriter = response.getWriter();
		outPrintWriter.write(JSONArray.toJSONString(resultMap));
		outPrintWriter.flush();
		outPrintWriter.close();
	}

	private String modify(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String proContact = request.getParameter("proContact");
		String proPhone = request.getParameter("proPhone");
		String proAddress = request.getParameter("proAddress");
		String proFax = request.getParameter("proFax");
		String proDesc = request.getParameter("proDesc");
		String id = request.getParameter("id");
		Provider provider = new Provider();
		provider.setId(Integer.valueOf(id));
		provider.setProContact(proContact);
		provider.setProPhone(proPhone);
		provider.setProFax(proFax);
		provider.setProAddress(proAddress);
		provider.setProDesc(proDesc);
		provider.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		provider.setModifyDate(new Date());
		boolean flag = false;

		flag = providerService.modify(provider);
		System.out.println("--------flag="+flag);
		if(flag){
			return "redirect:/provider.do?method=query";
//			response.sendRedirect(request.getContextPath()+"/jsp/provider.do?method=query");
		}else{
			return "providermodify";
//			request.getRequestDispatcher("providermodify.jsp").forward(request, response);
		}

	}

	private String getProviderById(HttpServletRequest request, HttpServletResponse response, String url)
			throws ServletException, IOException {

		String id = request.getParameter("proid");
		System.out.println("-------------"+id);
		if(!StringUtils.isNullOrEmpty(id)){

			Provider provider = null;
			provider = providerService.getProviderById(id);
			request.setAttribute("provider", provider);
			System.out.println("-------------"+id);
			return url;
//			request.getRequestDispatcher(url).forward(request, response);
		}
		return "error";
	}
	private String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String proCode = request.getParameter("proCode");
		String proName = request.getParameter("proName");
		String proContact = request.getParameter("proContact");
		String proPhone = request.getParameter("proPhone");
		String proAddress = request.getParameter("proAddress");
		String proFax = request.getParameter("proFax");
		String proDesc = request.getParameter("proDesc");

		Provider provider = new Provider();
		provider.setProCode(proCode);
		provider.setProName(proName);
		provider.setProContact(proContact);
		provider.setProPhone(proPhone);
		provider.setProFax(proFax);
		provider.setProAddress(proAddress);
		provider.setProDesc(proDesc);
		provider.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		provider.setCreationDate(new Date());
		boolean flag = false;

		flag = providerService.add(provider);
		if(flag){
			return "redirect:/provider.do?method=query";
//			response.sendRedirect(request.getContextPath()+"/jsp/provider.do?method=query");
		}else{
			return "provideradd";
//			request.getRequestDispatcher("provideradd.jsp").forward(request, response);
		}
	}

	private String query(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String queryProName = request.getParameter("queryProName");
		String queryProCode = request.getParameter("queryProCode");
		String pageIndex = request.getParameter("pageIndex");

		//??????????????????
		int pageSize = Constants.pageSize;
		//????????????
		int currentPageNo = 1;
//		System.out.println("queryUserName servlet--------"+queryUserName);
//		System.out.println("queryUserRole servlet--------"+queryUserRole);
//		System.out.println("query pageIndex--------- > " + pageIndex);
		if(queryProName == null){
			queryProName = "";
		}
		if(queryProCode == null){
			queryProCode = "";
		}
		if(pageIndex != null){
			try{
				currentPageNo = Integer.valueOf(pageIndex);
			}catch(NumberFormatException e){
				return "error";
			}
		}
		//??????????????????
		int totalCount	= providerService.getProviderCount(queryProName,queryProCode);

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

		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList(queryProName,queryProCode,currentPageNo,pageSize);
		request.setAttribute("providerList", providerList);
		request.setAttribute("queryProName", queryProName);
		request.setAttribute("queryProCode", queryProCode);
		request.setAttribute("totalPageCount", totalPageCount);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("currentPageNo", currentPageNo);
		return "providerlist";
//		request.getRequestDispatcher("providerlist.jsp").forward(request, response);
	}


}
