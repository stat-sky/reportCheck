package io.transwarp.autoCheck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.transwarp.information.CheckInfos;
import io.transwarp.information.ClusterInformation;
import io.transwarp.util.UtilTool;

public class DiskUsedOnNodeCheck extends ClusterInformation {
	
	
	public void checkDiskUsed() {
		for(Iterator<String> hostnames = ClusterInformation.nodeInfoByShells.keySet().iterator(); hostnames.hasNext(); ) {
			String hostname = hostnames.next();
			Map<String, String> nodeInfos = ClusterInformation.nodeInfoByShells.get(hostname);
			String inodes = nodeInfos.get("inode");
			List<String> diskOfLessInode = checkSpace(inodes);
			if(diskOfLessInode.size() > 0) {
				CheckInfos.diskOfLessInodes.put(hostname, diskOfLessInode);
			}
			String memory = nodeInfos.get("memory");
			List<String> diskOfLessMemory = checkSpace(memory);
			if(diskOfLessMemory.size() > 0) {
				CheckInfos.diskOfLessMemory.put(hostname, diskOfLessMemory);
			}
		}
	}
	
	private List<String> checkSpace(String infos) {
		List<String> disks = new ArrayList<String>();
		String[] lines = infos.split("\n");
		for(int i = 1; i < lines.length; i++) {  //first line is title
			String[] items = lines[i].split("\\s+");
			int len = items.length;
//			System.out.println(lines[i] + "||| " + items[len - 2]);
			double size = 0;
			try {
				size = Double.valueOf(UtilTool.getDouble(items[len - 2]));
			}catch(Exception e) {
				continue;
			}
			if(size >= 65) {
				disks.add(items[len - 1] + ":" + items[len - 2]);
			}
		}
		return disks;
	}
	
}
