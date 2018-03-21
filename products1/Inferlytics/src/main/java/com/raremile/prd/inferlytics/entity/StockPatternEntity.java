/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.entity;

import java.util.List;

/**
 * @author Praty
 * @created May 24, 2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class StockPatternEntity {


	private List<String> patterns;
	private List<String> values;
	private List<String> valuesFor;

	public List<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public List<String> getValuesFor() {
		return valuesFor;
	}

	public void setValuesFor(List<String> valuesFor) {
		this.valuesFor = valuesFor;
	}

}
