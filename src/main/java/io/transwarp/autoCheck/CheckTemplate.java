package io.transwarp.autoCheck;

import io.transwarp.information.ClusterInformation;

public abstract class CheckTemplate extends ClusterInformation {

	public abstract void check() throws Exception;
}
