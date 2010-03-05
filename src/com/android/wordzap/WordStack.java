package com.android.wordzap;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class WordStack extends Stack<Character> {
	private boolean wordComplete;
	private int wordLimit;
	public WordStack(int wordLimit){
		this.wordComplete=false;
		this.wordLimit=wordLimit;
	}
	
	public boolean isWordComplete(){
		return this.wordComplete;
	}
	
	public boolean lockWord(){
		if(this.size() == 0)
			return false;
		this.wordComplete=true;
		return true;
	}
	
	public void unlockWord(){
		this.wordComplete=false;
	}
	
	public boolean pushLetter(char letter) throws WordStackOverflowException {
		if(isWordComplete()){
			return false;
		}
        if (this.size() < wordLimit) {
            this.push(letter);
            return true;
        } else {
            throw new WordStackOverflowException(letter, wordLimit);
        }
    }
	
	public char popLetter() throws EmptyStackException{
        Character c=this.pop();
        return c.charValue();
    }
	
	public char peekLetter() throws EmptyStackException{
        Character c=this.peek();
        return c.charValue();
    }
	
	
	public String toString(){
		String word="";
		Iterator<Character> stackIter=this.iterator();
		while(stackIter.hasNext()){
			word+=stackIter.next().charValue();
		}
		return word;
	}
}
