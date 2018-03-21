package com.raremile.prd.inferlytics.entity;

/**
 * 
 * @author Pratyusha
 */
public enum SENTIMENTENUM {
	GOOD, BAD, NEUTRAL;

	public static SENTIMENTENUM fromInteger(int x) {
		switch (x) {
		case 0:
			return GOOD;
		case 1:
			return BAD;
		case 2:
			return NEUTRAL;

		}
		return null;
	}


}
