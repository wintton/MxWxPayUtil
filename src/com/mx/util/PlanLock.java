package com.mx.util;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class PlanLock {
	public synchronized void execute(Runnable runnable) {
		runnable.run();
	}
}
