package io.transwarp.report;

import io.transwarp.information.ClusterInformation;

public abstract class ClusterReportTemplate extends ClusterInformation {

	abstract public void outputToFile(String path) throws Exception;
}
