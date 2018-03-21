package com.raremile.prd.inferlytics.entity;

import java.util.ArrayList;

/**
 * @author Pratyusha
 * @created Apr 4, 2013
 * 
 *          TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class ModifierMap {
	public static ArrayList<String> unigrams;
	public static ArrayList<ArrayList<ArrayList<Integer>>> ugramValuePointer;
	public static ArrayList<Double> score;
	public static ArrayList<String> modifierMultiWord;
	
	

	private ModifierMap() {
	}

	private static boolean doesUnigramListExist() {
		if (null != unigrams && unigrams.size() > 0) {
			return true;
		}
		return false;
	}
	
	private static boolean doesUnigramExist(String unigram) {
		if (doesUnigramListExist() && unigrams.contains(unigram)) {
				return true;
		}
		return false;
	}
	
	public static double getModifierUnigramScore(String unigram){
		if(doesUnigramExist(unigram)){
			return getScore(unigrams.indexOf(unigram));
		}
		
		return 0.0;
		
	}
	private static double getScore(int index) {
		if (index >= 0 && index < score.size()) {
			return score.get(index);
		}
		return 0.0;
	}

	
}
