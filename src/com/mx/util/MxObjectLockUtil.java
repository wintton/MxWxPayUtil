package com.mx.util;

import java.util.HashMap;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class MxObjectLockUtil {
	private HashMap<String, PlanLock> planLockHashMap = new HashMap<>(40);
	private volatile static MxObjectLockUtil instance;

	private MxObjectLockUtil() throws IllegalAccessException {
		if (instance != null) {
			throw new IllegalAccessException("该类为单例模式，不可生成额外的对象");
		}
	}

	public HashMap<String, PlanLock> getPlanLockHashMap() {
		return planLockHashMap;
	}

	public synchronized PlanLock getObjectLock(String key) {
		if (planLockHashMap.get(key) == null) {
			PlanLock planLock = new PlanLock();
			planLockHashMap.put(key, planLock);
			return planLock;
		}
		return planLockHashMap.get(key);
	}

	public PlanLock removeLock(String key) {
		PlanLock planLock = null;
		if (planLockHashMap.get(key) != null) {
			planLock = planLockHashMap.remove(key);
		}
		return planLock;
	}

	public static MxObjectLockUtil getInstance() {
		if (instance == null) {
			synchronized (MxObjectLockUtil.class) {
				if (instance == null) {
					try {
						instance = new MxObjectLockUtil();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}

}
