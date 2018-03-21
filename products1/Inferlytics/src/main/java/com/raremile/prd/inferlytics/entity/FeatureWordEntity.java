package com.raremile.prd.inferlytics.entity;

import java.util.List;

public class FeatureWordEntity {
private String nounWord;
private String synonymWord;
private List<String> subDimension;
/**
 * @return the nounWord
 */
public String getNounWord() {
	return nounWord;
}
/**
 * @param nounWord the nounWord to set
 */
public void setNounWord(String nounWord) {
	this.nounWord = nounWord;
}
/**
 * @return the synonymWord
 */
public String getSynonymWord() {
	return synonymWord;
}
/**
 * @param synonymWord the synonymWord to set
 */
public void setSynonymWord(String synonymWord) {
	this.synonymWord = synonymWord;
}
/**
 * @return the subDimension
 */
public List<String> getSubDimension() {
	return subDimension;
}
/**
 * @param subDimension the subDimension to set
 */
public void setSubDimension(List<String> subDimension) {
	this.subDimension = subDimension;
}

}
