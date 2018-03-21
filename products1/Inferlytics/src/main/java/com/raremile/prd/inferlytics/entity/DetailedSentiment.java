package com.raremile.prd.inferlytics.entity;

public enum DetailedSentiment {
	WEAK_POSITIVE(0), POSITIVE(1), STRONG_POSITIVE(2), WEAK_NEGATIVE(3), NEGATIVE(
			4), STRONG_NEGATIVE(5), NEUTRAL(6);

	private int value;

	DetailedSentiment(int value) {
		this.value = value;
	}

	public static DetailedSentiment getSentimentFromDouble(Double score) {

		if (score >= 0.75) {
			return DetailedSentiment.STRONG_POSITIVE;
		} else if (score > 0.35 && score <= 0.75) {
			return DetailedSentiment.POSITIVE;
		} else if (score > 0 && score <= 0.35) {
			return DetailedSentiment.WEAK_POSITIVE;
		} else if (score < 0 && score >= -0.35) {
			return DetailedSentiment.WEAK_NEGATIVE;
		} else if (score < -0.35 && score >= -0.75) {
			return DetailedSentiment.NEGATIVE;
		} else if (score <= -0.75) {
			return DetailedSentiment.STRONG_NEGATIVE;
		} else if (score == 0) {
			return DetailedSentiment.NEUTRAL;
		} else {
			System.out.println("for this score " + score
					+ " sentiment is  not there");
			return null;
		}

	}

	public int getValue() {
		return this.value;
	}

	public static DetailedSentiment getSentimentFromInteger(int score) {

		switch (score) {
		case 0:
			return DetailedSentiment.WEAK_POSITIVE;
		case 1:
			return DetailedSentiment.POSITIVE;
		case 2:
			return DetailedSentiment.STRONG_POSITIVE;
		case 3:
			return DetailedSentiment.WEAK_NEGATIVE;
		case 4:
			return DetailedSentiment.NEGATIVE;
		case 5:
			return DetailedSentiment.STRONG_NEGATIVE;
		case 6:
			return DetailedSentiment.NEUTRAL;
		}
		return null;
	}

	public static String getSentimentStringFromInt(int score) {

		String sentiment = null;
		switch (score) {
		case 0:
			sentiment = "Weak Positive";
			break;
		case 1:
			sentiment = "Positive";
			break;
		case 2:
			sentiment = "Strong Positive";
			break;
		case 3:
			sentiment = "Weak Negative";
			break;
		case 4:
			sentiment = "Negative";
			break;
		case 5:
			sentiment = "Strong Negative";
			break;
		case 6:
			sentiment = "Neutral";
			break;
		default:
			sentiment = null;
		}
		return sentiment;

	}

	public static String getSentimentColorFromInt(int score) {

		String colorCode = null;
		switch (score) {
		case 0:
			colorCode = "#BCEE68";
			break;
		case 1:
			colorCode = "#A2CD5A";
			break;
		case 2:
			colorCode = "#6E8B3D";
			break;
		case 3:
			colorCode = "#FFC1C1";
			break;
		case 4:
			colorCode = "#FF6666";
			break;
		case 5:
			colorCode = "#FF3030";
			break;
		case 6:
			colorCode = "#FFFFF0";
			break;
		default:
			colorCode = null;
		}
		return colorCode;

	}

}
