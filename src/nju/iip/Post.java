package nju.iip;

import java.util.HashMap;

/**
 * 帖子类
 * @author wangqiang
 *
 */
public class Post {
	
	private HashMap<String,Integer>word_map;//帖子分词后的map
	
	private int  post_classify;//帖子类别
	
	public Post(HashMap<String,Integer>word_map,int post_classify){
		this.word_map=word_map;
		this.post_classify=post_classify;
	}
	
	
	public HashMap<String,Integer>getWordMap(){
		return this.word_map;
	}
	
	public int getPostClassify(){
		return this.post_classify;
	}

}
