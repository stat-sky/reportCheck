package io.transwarp.bean.restapiInfo;

import java.util.ArrayList;
import java.util.List;

public class MetricBean {

	private String metricName;
	private String unit;
	private List<String> metricValues;
	
	public MetricBean() {
		super();
		metricValues = new ArrayList<String>();
	}
	
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(Object metricName) {
		if(metricName == null)
			metricName = "";
		this.metricName = metricName.toString();
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(Object unit) {
		if(unit == null)
			unit = "";
		String unitString = unit.toString();
		if(unitString.equalsIgnoreCase("percent")) {
			this.unit = "%";
		}else if(unitString.equalsIgnoreCase("mb_per_second")) {
			this.unit = "MB/s";
		}else {
			this.unit = unitString;
		}
	}
	public List<String> getMetricValues() {
		return metricValues;
	}
	public void addMetricValue(String metricValue) {
		if(metricValues == null)
			metricValues = new ArrayList<String>();
		metricValues.add(metricValue);
	}
	
	@Override
	public String toString() {
		StringBuffer answer = new StringBuffer();
		answer.append("metricName : " + metricName).append("\n")
				.append("unit : " + unit).append("\n");
		for(String value : metricValues) {
			answer.append("value : " + value).append("\n");
		}
		return answer.toString();
	}
	
}
