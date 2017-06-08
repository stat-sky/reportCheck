package io.transwarp.autoCheck;

import io.transwarp.bean.restapiInfo.ComponentBean;
import io.transwarp.information.CheckInfos;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;

import java.text.SimpleDateFormat;
import java.util.List;


public class LicenseDateCheck extends CheckTemplate {

	public void check() throws Exception {
		List<ComponentBean> components = ClusterInformation.licenseInfo.getComponents();
		for(ComponentBean component : components) {
			String expiredDate = component.getExpiredDate();
			if(expiredDate == null || expiredDate.equals("")) {
				continue;
			}
			String[] items = expiredDate.replaceAll(",", "").split("\\s+");
			if(items.length != 3) {
				throw new RuntimeException("expiredDate of license is error, expiredDate is : " + expiredDate);
			}
			String endDate = items[2] + "-" + getMonthFromEN(items[0]) + "-" + items[1];
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			long endDateOfLong = dateFormat.parse(endDate).getTime();
			long remainingDate = (endDateOfLong - System.currentTimeMillis())/Constant.ONEDAYTIME;
			if(remainingDate < 45 ) { 
				CheckInfos.licenseDateofRemaining.put(component.getCompType(), remainingDate);
			}
		}
	}
	
	
	
	private String getMonthFromEN(String en_month) {
		if(en_month.equals("January") || en_month.equals("Jan")) {
			return "1";
		}else if(en_month.equals("February") || en_month.equals("Feb")) {
			return "2";
		}else if(en_month.equals("March") || en_month.equals("Mar")) {
			return "3";
		}else if(en_month.equals("April") || en_month.equals("Apr")) {
			return "4";
		}else if(en_month.equals("May") || en_month.equals("May")) {
			return "5";
		}else if(en_month.equals("June") || en_month.equals("Jun")) {
			return "6";
		}else if(en_month.equals("July") || en_month.equals("Jul")) {
			return "7";
		}else if(en_month.equals("August") || en_month.equals("Aug")) {
			return "8";
		}else if(en_month.equals("September") || en_month.equals("Sept")) {
			return "9";
		}else if(en_month.equals("October") || en_month.equals("Oct")) {
			return "10";
		}else if(en_month.equals("November") || en_month.equals("Nov")) {
			return "11";
		}else if(en_month.equals("December") || en_month.equals("Dec")) {
			return "12";
		}else {
			throw new RuntimeException("no this month");
		}
	}
	
}
