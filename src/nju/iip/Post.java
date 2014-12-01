package nju.iip;

import java.util.HashMap;

/**
 * 帖子类
 * @author wangqiang
 *
 */
public class Post {
	
	private HashMap<String,Integer>post_map;//帖子分词后的map
	
	private String post_classify;//帖子类别
	
	public Post(HashMap<String,Integer>post_map,String post_classify){
		this.post_map=post_map;
		this.post_classify=post_classify;
	}
	
	
	public HashMap<String,Integer>getPostMap(){
		return this.post_map;
	}
	
	public String getPostClassify(){
		return this.post_classify;
	}

}
