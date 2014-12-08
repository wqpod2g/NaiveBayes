package nju.iip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


public class NaiveBayesMultinomial {
	
	
	private static String file_path="lily";//测试文本路径
	
	/**
	 * 所有帖子集合
	 */
	private static Map<String,ArrayList<Post>>all_post_list=new HashMap<String,ArrayList<Post>>();
	
	
	/**
	 * 
	 * @Description: 返回某类帖子的分词结果
	 * @param @param content
	 * @return Map<String,Integer>（单词，个数）
	 */
     public  static HashMap<String, Integer> segStr(String content){
    	 // 分词
	    	StringReader input = new StringReader(content);
	        IKSegmenter iks = new IKSegmenter(input, true);
	        Lexeme lexeme = null;
	        HashMap<String, Integer> words = new HashMap<String, Integer>();
	        try {
	            while ((lexeme = iks.next()) != null) {
	            	if(lexeme.getLength()>1){
	            		if (words.containsKey(lexeme.getLexemeText())) {
	                        words.put(lexeme.getLexemeText(), words.get(lexeme.getLexemeText()) + 1);
	                    } else {
	                        words.put(lexeme.getLexemeText(), 1);
	                    }
	            	}
	            }       
	        }catch(IOException e) {
	            e.printStackTrace();
	        }
	        return words;
	 }
     
     
     /**
 	 * 求出某一类帖子集合
 	 * @return String content
 	 * @throws IOException 
 	 */
 	public static ArrayList<Post> getPostList(String txt_name){
 		ArrayList<Post>post_list=new ArrayList<Post>();
 		String txt_path=file_path+File.separator+txt_name;
 		try{
 			FileInputStream fs=new FileInputStream(txt_path);
 			InputStreamReader is=new InputStreamReader(fs,"UTF-8");
 			BufferedReader br=new BufferedReader(is);
 			String line=br.readLine();
 			while(line!=null){
 				Post post=new Post(segStr(line),txt_name);
 				post_list.add(post);
 				line=br.readLine();
 			}
 			br.close();
 		}catch(Exception e){
 			e.printStackTrace();
 		}
 		return post_list;
 	}
 	
 	
 	/**
	 * @description 获得所有帖子集合
	 * @return
	 * @throws IOException 
	 */
	public static Map<String,ArrayList<Post>>getAllpostList(){
		File f=new File(file_path);
		String[] txt_list=f.list();
		for(int i=0;i<txt_list.length;i++){
			all_post_list.put(txt_list[i], getPostList(txt_list[i]));
		}
		return all_post_list;
		
	}
	
	/**
	 * 得到某一类对应的单词词典
	 * @param txt_name
	 * @return
	 * @throws IOException 
	 */
	public static Dic getOneDictionary(ArrayList<Post>sample){
		int count=0;
		int v=0;
		HashMap<String,Integer>words_map=new HashMap<String,Integer>();
		for(Post post:sample){
			HashMap<String, Integer>word_map=post.getWordMap();
			Set<String>words=word_map.keySet();
			for(String word:words){
				if(words_map.containsKey(word)){
					int n1=word_map.get(word);
					int n2=words_map.get(word);
					words_map.put(word, n1+n2);
					count=count+n1;
				}
				else{
					int n1=word_map.get(word);
					words_map.put(word, n1);
					count=count+n1;
					v++;
				}
			}
		}
		Dic dic = new Dic(count,v,words_map);
		return dic;
	}
	
	/**
	 * 得到分类器
	 * @param train_sample
	 * @return
	 */
	public static Map<String,Dic>getAllDictionary(Map<String,ArrayList<Post>>train_sample){
		Map<String,Dic>dic_map=new HashMap<String,Dic>();
		Set<String>txt_names=train_sample.keySet();
		for(String txt_name:txt_names){
			ArrayList<Post>sample=train_sample.get(txt_name);
			dic_map.put(txt_name, getOneDictionary(sample));
		}
		return dic_map;
	}
	
	
	/**
	 * 十折划分训练样本
	 * @param n
	 * @param test_sample
	 * @param train_sample
	 */
	public static void divide(int n,ArrayList<Post>test_sample,Map<String,ArrayList<Post>>train_sample){
		Set<String>txt_names=all_post_list.keySet();
		for(String txt_name:txt_names){
			ArrayList<Post>list=new ArrayList<Post>();
			train_sample.put(txt_name, list);
			ArrayList<Post>post_list=all_post_list.get(txt_name);
			for(int i=0;i<post_list.size();i++){
				if(i>=10*n&&i<(n+1)*10){
					test_sample.add(post_list.get(i));
				}
				else{
					train_sample.get(txt_name).add(post_list.get(i));
				}
			}
		}
		
	}
	
	
	
	/**
	 * @description 计算某篇帖子属于某个类的概率
	 * @param post
	 * @param matrix
	 * @return probility
	 */
	public static double getProbility(Post post,Dic dic){
		double probility=0.0;
		Map<String,Integer>post_word_map=post.getWordMap();
		Map<String,Integer>dic_word_map=dic.get_words_map();
		int count=dic.get_count();
		int v=dic.get_v();
		Set<String>words=post_word_map.keySet();
		for(String word:words){
			double p;
			if(dic_word_map.containsKey(word)){
				p=1.0*(dic_word_map.get(word)+1)/(count+v);
			}
			else{
				p=1.0/(count+v);
			}
			probility=probility+Math.log(p);
		}
		return probility;
	}
	
	/**
	 * @计算某篇帖子属于哪个类
	 * @param post
	 * @param ten_matrix_map
	 * @return 帖子所属类别
	 */
    private static String getResult(Post post, Map<String, Dic> dic_map) {
    	String result="";
    	double probility=Double.NEGATIVE_INFINITY;
    	Set<String>txt_names=dic_map.keySet();
    	for(String txt_name:txt_names){
    		double temp=getProbility(post,dic_map.get(txt_name));
    		if(temp>probility){
				probility=temp;
				result=txt_name;
			}
    	}
		return result;
	}
    
	public static void process(){
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int k=0;k<10;k++){
			int count=0;
			ArrayList<Post>test_sample=new ArrayList<Post>();
			Map<String,ArrayList<Post>>train_sample=new HashMap<String,ArrayList<Post>>();
			divide(k,test_sample,train_sample);
			Map<String,Dic>dic_map=getAllDictionary(train_sample);
			for(int i=0;i<test_sample.size();i++){
				String result=getResult(test_sample.get(i),dic_map);
				String post_id=test_sample.get(i).getPostid();
				if(result.equals(post_id)){
					count++;
				}
			}
			System.out.println("第"+(k+1)+"折命中率为:"+count/100.0);
			resultList.add(count/100.0);
		}
		System.out.println("十折验证平均值为:"+Tools.getMean(resultList));
	}
	
	public static void main(String[] args){
		getAllpostList();
		process();
	}

}
