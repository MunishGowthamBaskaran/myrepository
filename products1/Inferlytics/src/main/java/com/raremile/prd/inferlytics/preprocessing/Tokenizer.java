package com.raremile.prd.inferlytics.preprocessing;

import java.io.IOException;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;


/**
*
* @author Pratyusha
*/
public class Tokenizer {
	private static final Logger LOG = Logger.getLogger(Tokenizer.class);
	 public static LinkedList<String> getTokens (String text,ArrayList<String> newHashtags) throws IOException {

		String normalizedText = TextNormalizer.getTweetWithoutUrlsAnnotations(
				text, newHashtags);
	        return getStandardTokens(normalizedText);
	    }
	
	 public static LinkedList<String> getSentences (String text) throws IOException {
		 LinkedList<String> sentences = new LinkedList<String>();
		 BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		  iterator.setText(text);
		  int start = iterator.first();
		  for (int end = iterator.next();
		      end != BreakIterator.DONE;
		      start = end, end = iterator.next()) {
			  sentences.add(text.substring(start,end));
		   // System.out.println(source.substring(start,end));
		  }
		return sentences;
	 }

	/**
	 * @param text
	 * @return
	 * @throws IOException
	 * 
	 *             Using lucene standard-tokenizer finding tokens from a given
	 *             text. standard-tokenizer takes care of special characters
	 *             also by removing them. If the calling method needs some
	 *             special characters, the method itself needs to take care of
	 *             those.
	 */
	public static LinkedList<String> getStandardTokens(String text)
			throws IOException {
	        LinkedList<String> tokens   = new LinkedList<String>();
		final Version matchVersion = Version.LUCENE_30;
		TokenStream ts = new StandardTokenizer(matchVersion, new StringReader(
				text));
		TermAttribute termAtt = ts.getAttribute(TermAttribute.class);
		while (ts.incrementToken()) {
	            tokens.add(TextNormalizer.removeDuplicates(termAtt.term()));
		}
		return tokens;
	    }
	  public static void main(String[] args) throws IOException {
		  BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = "This is a 3.45D test. This is a T.L.A. test. Now with a Dr. AHAHin it.";
		/*
		 * iterator.setText(source); int start = iterator.first(); for (int end
		 * = iterator.next(); end != BreakIterator.DONE; start = end, end =
		 * iterator.next()) { LOG.info(source.substring(start, end));
		 * getStandardTokens(source.substring(start, end)); }
		 */
	LinkedList<String> sentences = getSentences(source);
		for (String string : sentences) {
			getStandardTokens(string);
		}
	}
	  
	  
}
