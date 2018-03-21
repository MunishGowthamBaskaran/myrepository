package com.raremile.prd.inferlytics.utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

class TaggerDemo {

  private TaggerDemo() {}

  public static void main(String[] args) throws Exception {
    /*if (args.length != 2) {
      System.err.println("usage: java TaggerDemo modelFile fileToTag");
      return;
    }*/
    MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
    List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("taggers/sample-input.txt")));
    for (List<HasWord> sentence : sentences) {
    	   	
    //	System.out.println(Sentence.toUntaggedList(arg0));
    	
      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
      for (TaggedWord taggedWord : tSentence) {
		System.out.println(taggedWord.word() +"-->"+taggedWord.tag());
		//System.out.println(taggedWord.tag());
		
	}
      System.out.println(Sentence.listToString(tSentence, false));
      
    }
  }

}
