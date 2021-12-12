package com.huangjiaxin.service.provider;


import com.huangjiaxin.pojo.Provider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProviderService {
	public boolean add(Provider provider);
	public List<Provider> getProviderList(String proName, String proCode,int currentPageNo, int pageSize);
	public int deleteProviderById(String delId);
	int getProviderCount(String proName,String proCode);
	public Provider getProviderById(String id);
	public boolean modify(Provider provider);
	
}
