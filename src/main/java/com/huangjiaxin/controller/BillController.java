package com.huangjiaxin.controller;

import com.alibaba.fastjson.JSONArray;
import com.huangjiaxin.pojo.Bill;
import com.huangjiaxin.pojo.Provider;
import com.huangjiaxin.pojo.Role;
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
import sun.security.jca.ProviderList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class BillController {
	@Autowired
	BillServiceImpl billService;
	@Autowired
	ProviderServiceImpl providerService;
	@Autowired
	UserServiceImpl userService;

	@RequestMapping(value="/bill.do",method = {RequestMethod.POST,RequestMethod.GET})
	public String doPost(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="method",required = false) String method)
			throws ServletException, IOException {
		System.out.println("=================== "+method);
		if(method != null && method.equals("query")){
			return this.query(request,response);
		}else if(method != null && method.equals("add")){
			return  this.add(request,response);
		}else if(method != null && method.equals("view")){
			return  this.getBillById(request,response,"billview");
		}else if(method != null && method.equals("modify")){
			return this.getBillById(request,response,"billmodify");
		}else if(method != null && method.equals("modifysave")){
			return this.modify(request,response);
		}else if(method != null && method.equals("delbill")){
			this.delBill(request,response);
		}else if(method != null && method.equals("getproviderlist")){
			this.getProviderlist(request,response);
		}
		return "error";
	}

	private void getProviderlist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("getproviderlist ========================= ");
		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList("","",0,0);
		System.out.println("getProName--------------"+providerList.get(0).getProName());
		//把providerList转换成json对象输出
		response.setContentType("application/json");
		PrintWriter outPrintWriter = response.getWriter();
		outPrintWriter.write(JSONArray.toJSONString(providerList));
		outPrintWriter.flush();
		outPrintWriter.close();
	}
	private String getBillById(HttpServletRequest request, HttpServletResponse response,String url)
			throws ServletException, IOException {
		String id = request.getParameter("billid");
		if(!StringUtils.isNullOrEmpty(id)){

			Bill bill = null;
			bill = billService.getBillById(id);
			System.out.println("bill================>  "+bill);
			request.setAttribute("bill", bill);
			return url;
//			request.getRequestDispatcher(url).forward(request, response);
		}
		return "error";
	}

	private String modify(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("modify===============");
		String id = request.getParameter("id");
		String productName = request.getParameter("productName");
		String productDesc = request.getParameter("productDesc");
		String productUnit = request.getParameter("productUnit");
		String productCount = request.getParameter("productCount");
		String totalPrice = request.getParameter("totalPrice");
		String providerId = request.getParameter("providerId");
		String isPayment = request.getParameter("isPayment");

		Bill bill = new Bill();
		bill.setId(Integer.valueOf(id));
		bill.setProductName(productName);
		bill.setProductDesc(productDesc);
		bill.setProductUnit(productUnit);
		bill.setProductCount(new BigDecimal(productCount).setScale(2,BigDecimal.ROUND_DOWN));
		bill.setIsPayment(Integer.parseInt(isPayment));
		bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2,BigDecimal.ROUND_DOWN));
		bill.setProviderId(Integer.parseInt(providerId));

		bill.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		bill.setModifyDate(new Date());
		boolean flag = false;

		flag = billService.modify(bill);
		if(flag){
			return "redirect:/bill.do?method=query";
//			response.sendRedirect(request.getContextPath()+"/jsp/bill.do?method=query");
		}else{
			return "billmodify";
//			request.getRequestDispatcher("billmodify.jsp").forward(request, response);
		}
	}
	private void delBill(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("billid");
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(!StringUtils.isNullOrEmpty(id)){

			boolean flag = billService.deleteBillById(id);
			if(flag){//删除成功
				resultMap.put("delResult", "true");
			}else{//删除失败
				resultMap.put("delResult", "false");
			}
		}else{
			resultMap.put("delResult", "notexit");
		}
		//把resultMap转换成json对象输出
		response.setContentType("application/json");
		PrintWriter outPrintWriter = response.getWriter();
		outPrintWriter.write(JSONArray.toJSONString(resultMap));
		outPrintWriter.flush();
		outPrintWriter.close();
	}
	private String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String billCode = request.getParameter("billCode");
		String productName = request.getParameter("productName");
		String productDesc = request.getParameter("productDesc");
		String productUnit = request.getParameter("productUnit");

		String productCount = request.getParameter("productCount");
		String totalPrice = request.getParameter("totalPrice");
		String providerId = request.getParameter("providerId");
		String isPayment = request.getParameter("isPayment");

		Bill bill = new Bill();
		bill.setBillCode(billCode);
		bill.setProductName(productName);
		bill.setProductDesc(productDesc);
		bill.setProductUnit(productUnit);
		bill.setProductCount(new BigDecimal(productCount).setScale(2,BigDecimal.ROUND_DOWN));
		bill.setIsPayment(Integer.parseInt(isPayment));
		bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2,BigDecimal.ROUND_DOWN));
		bill.setProviderId(Integer.parseInt(providerId));
		bill.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		bill.setCreationDate(new Date());
		boolean flag = false;

		flag = billService.add(bill);
		System.out.println("add flag -- > " + flag);
		if(flag){
			return "redirect:/bill.do?method=query";
//			response.sendRedirect(request.getContextPath()+"/jsp/bill.do?method=query");
		}else{
			return "billadd";
//			request.getRequestDispatcher("billadd.jsp").forward(request, response);
		}

	}
	private String query(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("=======================");
		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList("","",0,0);
		request.setAttribute("providerList", providerList);
		String queryProductName = request.getParameter("queryProductName");
		String temp = request.getParameter("queryProviderId");
		String temp2 = request.getParameter("queryIsPayment");
		String pageIndex = request.getParameter("pageIndex");
		int queryProviderId = 0;
		int queryIsPayment = 0;
		if(StringUtils.isNullOrEmpty(queryProductName)){
			queryProductName = "";
		}

		System.out.println("111111111111111111111111111");
		//设置页面容量
		int pageSize = Constants.pageSize;
		//当前页码
		int currentPageNo = 1;
		System.out.println(queryProductName);
		System.out.println(temp);
		System.out.println(temp2);
		List<Bill> billList = new ArrayList<Bill>();
/*		Bill bill = new Bill();
		if(StringUtils.isNullOrEmpty(queryIsPayment)){
			bill.setIsPayment(0);
		}else{
			bill.setIsPayment(Integer.parseInt(queryIsPayment));
		}

		if(StringUtils.isNullOrEmpty(queryProviderId)){
			bill.setProviderId(0);
		}else{
			bill.setProviderId(Integer.parseInt(queryProviderId));
		}*/

		if(queryProductName == null){
			queryProductName = "";
		}

		if(temp != null && !temp.equals("")){
			queryProviderId = Integer.parseInt(temp);
		}
		if(temp2 != null && !temp2.equals("")){
			queryIsPayment = Integer.parseInt(temp2);
		}

		if(pageIndex != null){
			try{
				currentPageNo = Integer.valueOf(pageIndex);
			}catch(NumberFormatException e){
				return "error";
			}
		}


		//总数量（表）
		int totalCount	= billService.getBillCount();
		//总页数
		PageSupport pages=new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);

		int totalPageCount = pages.getTotalPageCount();

		//控制首页和尾页
		if(currentPageNo < 1){
			currentPageNo = 1;
		}else if(currentPageNo > totalPageCount){
			currentPageNo = totalPageCount;
		}

/*		bill.setProductName(queryProductName);*/
		billList = billService.getBillList(queryProductName,Integer.valueOf(queryProviderId),Integer.valueOf(queryIsPayment),currentPageNo, pageSize);
		request.setAttribute("billList", billList);
		request.setAttribute("queryProductName", queryProductName);
		request.setAttribute("queryProviderId", queryProviderId);
		request.setAttribute("queryIsPayment", queryIsPayment);
		request.setAttribute("totalPageCount", totalPageCount);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("currentPageNo", currentPageNo);
		return "billlist";
//		request.getRequestDispatcher("billlist.jsp").forward(request, response);

	}










	public static void main(String[] args) {
		System.out.println(new BigDecimal("23.235").setScale(2,BigDecimal.ROUND_HALF_DOWN));
	}
}
