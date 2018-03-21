
package com.raremile.prd.inferlytics.entity;

import java.util.List;

public class Relations {

	private List<Integer> relationId;
	private List<String> relationName;
	private List<Integer> possibleMaleRelation;
	private List<Integer> possibleFemaleRelation;

	public List<Integer> getRelationId() {
		return relationId;
	}

	public void setRelationId(List<Integer> relationId) {
		this.relationId = relationId;
	}

	public List<String> getRelationName() {
		return relationName;
	}

	public void setRelationName(List<String> relationName) {
		this.relationName = relationName;
	}

	public List<Integer> getPossibleMaleRelation() {
		return possibleMaleRelation;
	}

	public void setPossibleMaleRelation(List<Integer> possibleMaleRelation) {
		this.possibleMaleRelation = possibleMaleRelation;
	}

	public List<Integer> getPossibleFemaleRelation() {
		return possibleFemaleRelation;
	}

	public void setPossibleFemaleRelation(List<Integer> possibleFemaleRelation) {
		this.possibleFemaleRelation = possibleFemaleRelation;
	}

}
