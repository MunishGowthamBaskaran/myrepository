package com.raremile.prd.inferlytics.entity;

import java.io.Serializable;
import java.util.List;

/**
*
* @author Pratyusha
*/
public class Opinion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String object;
	private List<NounAdjScore> features;
	private List<FeatureAdjectiveEntity> featureDimensions;
	private Sentiment opinionOrientation;
	private String opinionHolder;
	private long time;
	/**
	 * @param object the object to set
	 * This indicates the object for which we are extracting Sentiment.
	 * It can be a brand in our case.
	 */
	public void setObject(String object) {
		this.object = object;
	}
	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}


	/**
	 * @param opinionHolder the opinionHolder to set
	 * The holder of an opinion is the person or organization that expresses the
		opinion.In the case of product reviews and blogs, opinion
		holders are usually the authors of the posts. Opinion holders are more important in news articles because
		they often explicitly state the person or organization that holds a particular opinion
	 */
	public void setOpinionHolder(String opinionHolder) {
		this.opinionHolder = opinionHolder;
	}
	/**
	 * @return the opinionHolder
	 */
	public String getOpinionHolder() {
		return opinionHolder;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @param opinion the opinion to set
	 * The orientation of an opinion on a feature f indicates whether the opinion is positive, negative or neutral.
		Opinion orientation is also known as sentiment orientation, polarity of opinion, or semantic orientation
	 */
	public void setOpinionOrientation(Sentiment opinionOrientation) {
		this.opinionOrientation = opinionOrientation;
	}
	/**
	 * @return the opinionOrientation
	 */
	public Sentiment getOpinionOrientation() {
		return opinionOrientation;
	}
	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<NounAdjScore> features) {
		this.features = features;
	}
	/**
	 * @return the features
	 */
	public List<NounAdjScore> getFeatures() {
		return features;
	}

	/**
	 * @param featureDimensions
	 *            the featureDimensions to set
	 */
	public void setFeatureDimensions(
			List<FeatureAdjectiveEntity> featureDimensions) {
		this.featureDimensions = featureDimensions;
	}

	/**
	 * @return the featureDimensions
	 */
	public List<FeatureAdjectiveEntity> getFeatureDimensions() {
		return featureDimensions;
	}
	
}
