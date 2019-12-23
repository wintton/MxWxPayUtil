package com.px.util;

public class PlanLock {
	public synchronized void execute(Runnable runnable) {
		runnable.run();
	}
}
