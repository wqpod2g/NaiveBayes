package nju.iip;

import java.util.HashMap;
import java.util.Map;

/**
 * 词典类
 * @author wangqiang
 * @since 2014-12-8
 */
public class Dic{
	
	private int count;//对应词典单词总数
	
	private int v;//对应词典单词类别数（出现多次算一次）
	
	private Map<String,Integer>words_map;//单词统计
	
	public Dic(int count,int v,Map<String,Integer>words_map){
		this.count=count;
		this.v=v;
		this.words_map=words_map;
	}
	
	public Map<String,Integer> get_words_map(){
		return this.words_map;
	}
	
	public int get_count(){
		return this.count;
	}
	
	public int get_v(){
		return this.v;
	}
	

}
