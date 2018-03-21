package com.raremile.prd.inferlytics.entity;

import java.util.Map;

public class IdMap {

	private static Map<String, Integer> entityIdMap;
	private static Map<String, Integer> subproductIdMap;

	public static int getEntityId(String entity) {
		int id = entityIdMap.get(entity);
		return id;
	}

	public static int getSubproductId(String subProduct) {
		int id = subproductIdMap.get(subProduct);
		return id;

	}

	public static void setEntityIdMap(Map<String, Integer> idMap) {
		IdMap.entityIdMap = idMap;
	}

	public static void setsubproductIdMap(Map<String, Integer> idMap) {
		IdMap.subproductIdMap = idMap;
	}

}
