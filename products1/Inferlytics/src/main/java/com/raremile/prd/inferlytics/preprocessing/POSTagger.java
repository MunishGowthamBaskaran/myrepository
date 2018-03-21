package com.raremile.prd.inferlytics.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.raremile.prd.inferlytics.commons.POSConstants;
import com.raremile.prd.inferlytics.utils.Util;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * 
 * @author Pratyusha
 */
public class POSTagger {
	private static final Logger LOG = Logger.getLogger(POSTagger.class);

	// Declare the tagger
	private static  MaxentTagger tagger = null;

	/**
	 * @param tokens
	 * @param nounAdj
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * 
	 *             This Method tags each token with corresponding Parts Of
	 *             Speech tags and also identifies nouns and the corresponding
	 *             adjectives for the given tokens
	 */
	public static synchronized LinkedHashMap<String, List<String>> doPOSTagging(
			LinkedList<String> tokens, Map<String, String> nounAdj,Map<String,String> lemmaWordMap)
			throws IOException, ClassNotFoundException {
		// Initialize the tagger
		
		
		if (null == tagger) {
			// initialize the tagger only once. Because initializing itself
			// takes 2 seconds.
			initializeTagger();
		}
	
		if (null == nounAdj) {
			nounAdj = new HashMap<String, String>();
		}if(null == lemmaWordMap){
			lemmaWordMap = new HashMap<>();
		}

		LOG.debug("Before POS tagging " + tokens);

		/**
		 * The value to this key is a list of String because if the same word
		 * comes again in the sentence, the different pattern will be added to
		 * the list of patterns against the word. The Analysis class using these
		 * patterns should take care of it.
		 */
		LinkedHashMap<String, List<String>> postaggedList = new LinkedHashMap<String, List<String>>();
		if (null != tokens && !tokens.isEmpty()) {

			List<HasWord> sentence = Sentence.toWordList(tokens);
			ArrayList<TaggedWord> tSentence = null;
			synchronized (tagger) {
				 tSentence = tagger.tagSentence(sentence);	
			}
			

			/**
			 * Do the chunking part here and group all noun words with
			 * underscore. Get nearest adjective of every noun and fill nounAdj
			 * Map .
			 */

			TaggedWord[] taggedWords = tSentence
					.toArray(new TaggedWord[tSentence.size()]);
			int localcounter = -1;
			int adjPos = -1;
			String newToken = "";
			for (int i = 0; i < taggedWords.length; i++) {

				/**
				 * Get lemmatized word here.
				 */
				String lemmaWord = "";
				try{
					
					LOG.debug("Going to find lemma of this word "+taggedWords[i].word());
				 lemmaWord = Morphology.lemmaStatic(
						taggedWords[i].word(), taggedWords[i].tag(), false);
				}
				catch(Throwable t){
					LOG.error("Error occured while finding lemma for the word "+taggedWords[i].word(),t);
					lemmaWord = taggedWords[i].word();
				}
				if(lemmaWord == ""){
					lemmaWord = taggedWords[i].word();
				}
				/**
				 * wordPOSPattern stores POS pattern for each word. This is two
				 * patterns before the word and two after the word.
				 */
				StringBuffer wordPOSPattern = new StringBuffer();
				int wordPosition = 0;
				int arraySize = taggedWords.length;

				if ((i - 2) >= 0) {
					wordPOSPattern.append(" ").append(
							Util.getCommonPOS(taggedWords[i - 2].tag()));
					wordPosition++;
				}
				if ((i - 1) >= 0) {
					wordPOSPattern.append(" ").append(
							Util.getCommonPOS(taggedWords[i - 1].tag()));
					wordPosition++;
				}
				String thisTag = Util.getCommonPOS(taggedWords[i].tag());
				wordPOSPattern.append(" ").append(thisTag);
				wordPosition++;
				if ((i + 1 < arraySize)) {
					wordPOSPattern.append(" ").append(
							Util.getCommonPOS(taggedWords[i + 1].tag()));
				}
				if ((i + 2 < arraySize)) {
					wordPOSPattern.append(" ").append(
							Util.getCommonPOS(taggedWords[i + 2].tag()));
				}


				if (!postaggedList.containsKey(lemmaWord
						.toLowerCase(Locale.ENGLISH))) {
					ArrayList<String> patternList = new ArrayList<String>();
					patternList.add(wordPOSPattern.toString());
					patternList.trimToSize();
					postaggedList.put(lemmaWord.toLowerCase(Locale.ENGLISH),
							patternList);
				} else {
					List<String> existingPatternList = postaggedList
							.get(lemmaWord.toLowerCase(Locale.ENGLISH));
					existingPatternList.add(wordPOSPattern.toString());
					postaggedList.put(lemmaWord.toLowerCase(Locale.ENGLISH),
							existingPatternList);
				}

				LOG.debug("Lemma of " + taggedWords[i].word() + " is "
						+ lemmaWord + " with POS Tag " + taggedWords[i].tag());

				// Find Noun Adj here
				if (localcounter == -1) {
					localcounter = i;
				} else if (localcounter < i) {
					localcounter = i;
				}
				if (i == localcounter - 1 && adjPos == -1) {
					adjPos = getNearestAdjPosition(wordPOSPattern.toString(),
 wordPosition);
					if (adjPos != -1) {
						String adj = taggedWords[i - (wordPosition - adjPos)]
								.word();
						nounAdj.put(newToken.toLowerCase(Locale.ENGLISH), adj);
						
					}
				}
				if (thisTag.equals(POSConstants.NounSingular)
						&& localcounter == i) {
					//lemmaWordMap.put(lemmaWord.toLowerCase(Locale.ENGLISH), taggedWords[i].word().toLowerCase());
					/**
					 * Put all Noun Words in the postaggedList Map With NN as
					 * Key. So that later this can be used while Analysis.
					 */
					if (!postaggedList.containsKey(POSConstants.NounSingular)) {
						ArrayList<String> nounWordList = new ArrayList<String>();
						nounWordList.add(lemmaWord.toLowerCase());
						nounWordList.trimToSize();
						postaggedList.put(POSConstants.NounSingular,
						nounWordList);
					} else {
						List<String> existingnounWordList = postaggedList
								.get(POSConstants.NounSingular);
						existingnounWordList.add(lemmaWord.toLowerCase());
						postaggedList.put(POSConstants.NounSingular,
						existingnounWordList);
					}

					if ((i + 1) < tokens.size()) {
						newToken = "";
						while ((localcounter + 1) < tokens.size()
								&& taggedWords[localcounter].tag().equals(
										taggedWords[localcounter + 1].tag())) {
							if (!newToken.isEmpty()) {
								newToken += " ";
							}
							newToken += tokens.get(localcounter) + " "
									+ tokens.get(localcounter + 1);

							localcounter += 2;
						}
						if (localcounter < tokens.size()) {
							if (newToken != "") {

								if (taggedWords[localcounter]
											.tag()
											.equals(taggedWords[localcounter - 1]
													.tag())) {
								newToken += " " + tokens.get(localcounter);
								localcounter++;
								}
							} else {
								newToken += lemmaWord
										.toLowerCase(Locale.ENGLISH);
							}
						}
						// Get nearest Adjective here
						adjPos = getNearestAdjPosition(
								wordPOSPattern.toString(),
								wordPosition);

						String adj = null;
						if (adjPos != -1) {
							adj = taggedWords[i - (wordPosition - adjPos)]
									.word();
							nounAdj.put(newToken.toLowerCase(Locale.ENGLISH),
									adj);
						}
					} else {
						// Get nearest Adjective here
						adjPos = getNearestAdjPosition(
								wordPOSPattern.toString(),
								wordPosition);
						String adj = null;
						if (adjPos != -1) {
							adj = taggedWords[i - (wordPosition - adjPos)]
									.word();
							nounAdj.put(lemmaWord.toLowerCase(Locale.ENGLISH),
									adj);
							
						}
					}
				}
				
				if(thisTag.equals(POSConstants.Adjective)){
				//	lemmaWordMap.put(lemmaWord.toLowerCase(),  taggedWords[i].word().toLowerCase());
					
					if (!postaggedList.containsKey(POSConstants.Adjective)) {
						ArrayList<String> adjWordList = new ArrayList<String>();
						adjWordList.add(lemmaWord.toLowerCase());
						adjWordList.trimToSize();
						postaggedList.put(POSConstants.Adjective,
								adjWordList);
					} else {
						List<String> existingAdjWordList = postaggedList
								.get(POSConstants.Adjective);
						existingAdjWordList.add(lemmaWord.toLowerCase());
						postaggedList.put(POSConstants.Adjective,
								existingAdjWordList);
					}
					
				}
				lemmaWordMap.put(lemmaWord.toLowerCase(),  taggedWords[i].word().toLowerCase());
			}

		}

		return postaggedList;

	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void initializeTagger() throws IOException,
			ClassNotFoundException {
		tagger = new MaxentTagger(
				"taggers/english-left3words-distsim.tagger");
	}

	private static int getNearestAdjPosition(String wordPOSPattern, int wordPos) {
		int position = -1;
		if (wordPOSPattern.contains(POSConstants.Adjective)) {
			int adjPos = wordPOSPattern.indexOf(POSConstants.Adjective);

			position = (adjPos / 3) + 1;
			if (position < wordPos) {
				adjPos = wordPOSPattern.indexOf(POSConstants.Adjective,
						adjPos + 1);
				if (adjPos != -1 && ((adjPos / 3) + 1) < wordPos) {
					position = (adjPos / 3) + 1;
				}
			}
		} else if (wordPOSPattern.contains(POSConstants.Adverb)) {
			int advPos = wordPOSPattern.indexOf(POSConstants.Adverb);

			position = (advPos / 3) + 1;
			if (position < wordPos) {
				advPos = wordPOSPattern
						.indexOf(POSConstants.Adverb, advPos + 1);
				if (advPos != -1 && ((advPos / 3) + 1) < wordPos) {
					position = (advPos / 3) + 1;
				}
			}
		}
		return position;
	}

	public static String getPOSPattern(LinkedList<String> tokens, String word)
			throws IOException, ClassNotFoundException {
		if (null == tagger) {
		initializeTagger();
		}

		//LOG.debug("Before POS tagging " + tokens);
		String posPattern = "";
		if (null != tokens && !tokens.isEmpty()) {
			List<HasWord> sentence = Sentence.toWordList(tokens);
			ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);

			TaggedWord[] taggedWords = tSentence
					.toArray(new TaggedWord[tSentence.size()]);

			for (int i = 0; i < taggedWords.length; i++) {
				if (taggedWords[i].word().contains(word)
						|| word.contains(taggedWords[i].word())) {

					int arraySize = taggedWords.length;

					if ((i - 2) >= 0) {
						posPattern = posPattern + " "
								+ Util.getCommonPOS(taggedWords[i - 2].tag());
					}
					if ((i - 1) >= 0) {
						posPattern = posPattern + " "
								+ Util.getCommonPOS(taggedWords[i - 1].tag());

					}
					posPattern = posPattern + " "
							+ Util.getCommonPOS(taggedWords[i].tag());
					if ((i + 1 < arraySize)) {
						posPattern = posPattern + " "
								+ Util.getCommonPOS(taggedWords[i + 1].tag());
					}
					if ((i + 2 < arraySize)) {
						posPattern = posPattern + " "
								+ Util.getCommonPOS(taggedWords[i + 2].tag());
					}

					LOG.info(taggedWords[i].word() + "-->"
							+ taggedWords[i].tag());
					LOG.info(tokens.toString() + " -- > " + posPattern);
				}

			}

			/*
			 * ListIterator<TaggedWord> itr = tSentence.listIterator(); while
			 * (itr.hasNext()) { TaggedWord taggedWord = itr.next();
			 *//**
			 * If we find the word we are looking for, go back till three
			 * words and add the whole pattern i.e., back 3 + front 3 total 6.
			 */
			/*
			 * if (taggedWord.word().contains(word)) { int prevcount = 0; int
			 * nextCount = 0; while (itr.hasPrevious() && prevcount <= 2) {
			 * itr.previous();
			 *//** DO Nothing .. Just go back. */
			/*
			 * prevcount++; } for (int i = 0; i < prevcount; i++) { if
			 * (itr.hasNext()) {
			 * posPattern.add(Util.getCommonPOS(itr.next().tag())); } }
			 * 
			 * while (itr.hasNext() && nextCount <= 2) {
			 * posPattern.add(Util.getCommonPOS(itr.next().tag())); nextCount++;
			 * } } }
			 */

		}

		return posPattern;

	}

	public static ArrayList<TaggedWord> getTaggedWords(LinkedList<String> tokens)
			throws IOException, ClassNotFoundException {
		if (null == tagger) {
			initializeTagger();
		}

		LOG.info("Before POS tagging " + tokens);
		ArrayList<TaggedWord> tSentence = null;
		if (null != tokens && !tokens.isEmpty()) {
			List<HasWord> sentence = Sentence.toWordList(tokens);
			tSentence = tagger.tagSentence(sentence);
		}
		return tSentence;
	}

	public static HashMap<String, String> getChunkedTokens(List<String> tokens,
			TaggedWord[] taggedWords) {

		HashMap<String, String> nounAdj = new HashMap<String, String>();
		/*
		 * for (int i = 0; i < taggedWords.length; i++) {
		 * 
		 * if
		 * (Util.getCommonPOS(taggedWords[i].tag()).equals(POSConstants.NounSingular
		 * )) { if ((i + 1) >= tokens.size()) { if (taggedWords[i].tag() ==
		 * taggedWords[i].tag()) { String newToken = tokens.get(i) + "_" +
		 * tokens.get(i + 1); //Get nearest Adjective here
		 * nounAdj.put(newToken,getNearestAdj(String )); i++; } } else{
		 * nounAdj.put(tokens.get(i),getNearestAdj()); } } else {
		 * newTokens.add(tokens.get(i)); } }
		 */

		return nounAdj;

	}

}
