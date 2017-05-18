package io.transwarp.report.test;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;
import io.transwarp.bean.ConfigBean;
import io.transwarp.bean.TableBean;
import io.transwarp.bean.restapiInfo.MetricBean;
import io.transwarp.bean.restapiInfo.NodeBean;
import io.transwarp.bean.restapiInfo.ServiceBean;
import io.transwarp.information.ClusterInformation;
import io.transwarp.util.UtilTool;

public class OutputByJson extends ClusterInformation {

	public static void output(String path) {
		try {
			UtilTool.checkAndBuildDir(path);
			
			FileWriter nodeInfoWrite = new FileWriter(path + "nodeInfo.txt");
			for(Iterator<String> hostnames = ClusterInformation.nodeInfoByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next();
				NodeBean node = nodeInfoByRestAPIs.get(hostname);
				nodeInfoWrite.write(node.toString());
				nodeInfoWrite.write("\n\n");
			}
			nodeInfoWrite.flush();
			nodeInfoWrite.close();
			
			FileWriter serviceInfoWrite = new FileWriter(path + "serviceInfo.txt");
			for(Iterator<String> servicenames = ClusterInformation.serviceInfoByRestAPIs.keySet().iterator(); servicenames.hasNext(); ) {
				String servicename = servicenames.next();
				ServiceBean service = ClusterInformation.serviceInfoByRestAPIs.get(servicename);
				serviceInfoWrite.write(service.toString());
				serviceInfoWrite.write("\n\n");
			}
			serviceInfoWrite.flush();
			serviceInfoWrite.close();
			
			FileWriter licenseInfoWrite = new FileWriter(path + "licenseInfo.txt");
			licenseInfoWrite.write(licenseInfo.toString());
			licenseInfoWrite.flush();
			licenseInfoWrite.close();
			
			FileWriter configWrite = new FileWriter(path + "config.txt");
			for(Iterator<String> hostnames = ClusterInformation.configInfos.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next();
				configWrite.write("hostname : " + hostname + "\n==================================================================\n\n");
				Map<String, ConfigBean> configs = ClusterInformation.configInfos.get(hostname);
				for(Iterator<String> servicenames = configs.keySet().iterator(); servicenames.hasNext() ;) {
					String servicename = servicenames.next();
					ConfigBean config = configs.get(servicename);
					configWrite.write("service : " + servicename + "\n");
					configWrite.write(config.toString());
					configWrite.write("\n\n");
				}
			}
			configWrite.flush();
			configWrite.close();
			
			FileWriter portWrite = new FileWriter(path + "portInfo.txt");
			JSONObject json = new JSONObject();
			json.putAll(ClusterInformation.portInfos);
			portWrite.write(json.toString());
			portWrite.write("\n\n");
			portWrite.flush();
			portWrite.close();
			
			FileWriter processInfoWrite = new FileWriter(path + "process.txt");
			JSONObject jsonOfProcess = new JSONObject();
			jsonOfProcess.putAll(ClusterInformation.serviceProcessInfos);
			processInfoWrite.write(jsonOfProcess.toString());
			processInfoWrite.flush();
			processInfoWrite.close();
			
			FileWriter metricWrite = new FileWriter(path + "metric.txt");
			for(Iterator<String> hostnames = ClusterInformation.metricByRestAPIs.keySet().iterator(); hostnames.hasNext(); ) {
				String hostname = hostnames.next();
				metricWrite.write("hostname : " + hostname + "\n=======================\n\n");
				Map<String, MetricBean> metrics = ClusterInformation.metricByRestAPIs.get(hostname);
				for(Iterator<String> metricNames = metrics.keySet().iterator(); metricNames.hasNext(); ) {
					String metricName = metricNames.next();
					MetricBean metric = metrics.get(metricName);
					metricWrite.write(metric.toString());
					metricWrite.write("\n\n");
				}
			}
			metricWrite.flush();
			metricWrite.close();
			
			FileWriter hdfsWrite = new FileWriter(path + "hdfs.txt");
			JSONObject jsonOfHDFS = new JSONObject();
			jsonOfHDFS.putAll(ClusterInformation.hdfsInfos);
			hdfsWrite.write(jsonOfHDFS.toString());
			hdfsWrite.write("\n\n");
			hdfsWrite.flush();
			hdfsWrite.close();
			
			FileWriter hdfsSpaceWrite = new FileWriter(path + "hdfsSpace.txt");
			for(String item : ClusterInformation.hdfsSpaceSizeInfos) {
				hdfsSpaceWrite.write(item);
				hdfsSpaceWrite.write("\n\n");
			}
			hdfsSpaceWrite.flush();
			hdfsSpaceWrite.close();
			
			FileWriter mysqlWrite = new FileWriter(path + "mysql.txt");
			JSONObject jsonOfMysql = new JSONObject();
			jsonOfMysql.putAll(ClusterInformation.mysqlHAInfo);
			mysqlWrite.write(jsonOfMysql.toString());
			mysqlWrite.flush();
			mysqlWrite.close();
			
			FileWriter editWrite = new FileWriter(path + "edit.txt");
			for(Iterator<String> keys = ClusterInformation.editLogContinuity.keySet().iterator(); keys.hasNext(); ) {
				String key = keys.next();
				editWrite.write(key + ":\n");
				List<String> files = ClusterInformation.editLogContinuity.get(key);
				for(String file : files) {
					editWrite.write("\t" + file + "\n");
				}
			}
			editWrite.flush();
			editWrite.close();
			
			FileWriter fsimageWrite = new FileWriter(path + "fsimage.txt");
			JSONObject jsonOfFsimage = new JSONObject();
			jsonOfFsimage.putAll(ClusterInformation.fsimageIDAndTimestamp);
			fsimageWrite.write(jsonOfFsimage.toString());
			fsimageWrite.flush();
			fsimageWrite.close();
			
			FileWriter tableWrite = new FileWriter(path + "table.txt");
			for(Iterator<String> servicenames = ClusterInformation.tableInfos.keySet().iterator(); servicenames.hasNext() ;) {
				String servicename = servicenames.next();
				tableWrite.write("service : " + servicename + "\n");
				Vector<TableBean> tables = ClusterInformation.tableInfos.get(servicename);
				for(TableBean table : tables) {
					tableWrite.write(table.toString());
					tableWrite.write("\n\n");
				}
			}
			
			tableWrite.flush();
			tableWrite.close();
			
			FileWriter executeWrite = new FileWriter(path + "execute.txt");
			JSONObject jsonOfExecute = new JSONObject();
			jsonOfExecute.putAll(ClusterInformation.executorInfos);
			executeWrite.write(jsonOfExecute.toString());
			executeWrite.flush();
			executeWrite.close();
			
			FileWriter resourceWrite = new FileWriter(path + "resource.txt");
			JSONObject jsonOfResource = new JSONObject();
			jsonOfResource.putAll(ClusterInformation.roleResources);
			resourceWrite.write(jsonOfResource.toString());
			resourceWrite.flush();
			resourceWrite.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
