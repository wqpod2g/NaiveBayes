package nju.iip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class Tools {
	
	
	private static String file_path="lily";//测试文本路径
	
	
	private static ArrayList<String>all_word_list=new ArrayList<String>();//样本中所有出现过的单词集合(不重复)
	
	
	/**
    * @Description: 返回某篇帖子的分词结果
    * @param @param content
    * @return Map<String,Integer>（单词，个数）
    */
    public static HashMap<String, Integer> segStr(String content){
        // 分词
    	StringReader input = new StringReader(content);
        IKSegmenter iks = new IKSegmenter(input, true);
        Lexeme lexeme = null;
        HashMap<String, Integer> words = new HashMap<String, Integer>();
        try {
            while ((lexeme = iks.next()) != null) {
            	if(lexeme.getLength()>1){
            		if(!all_word_list.contains(lexeme.getLexemeText())){
            			all_word_list.add(lexeme.getLexemeText());
            		}
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
     * 得到某一类所有帖子集合
     * @param txt_name
     * @return ArrayList<Post>
     */
    public static ArrayList<Post> getOneClassifyPosts(String txt_name){
    	ArrayList<Post>post_list=new ArrayList<Post>();
    	try{
    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(txt_name), "UTF-8")); 
   	        String line = br.readLine();
   	        while(line != null){
   	        	HashMap<String,Integer>post_map=segStr(line);//将每行即每篇帖子分词统计
   	        	Post post=new Post(post_map,txt_name);
   	        	post_list.add(post);
   	        	line = br.readLine();
   	        }
   	        br.close();
   	        }catch(Exception e){
    		   e.getStackTrace();
    		   }
    	return post_list;
    }
    
    
    /**
     * 得到所有帖子集合
     * @return HashMap<String,ArrayList<Post>>(<类别，类别下所有帖子集合>)
     */
    public static HashMap<String,ArrayList<Post>>getAllPostsMap(){
    	HashMap<String,ArrayList<Post>>all_post_map=new HashMap<String,ArrayList<Post>>();
    	File f=new File(file_path);
    	String[] txt_names=f.list();//得到所有分类名
    	for(String txt_name:txt_names){
    		String path=file_path+File.separator+txt_name;
    		ArrayList<Post>post_list=getOneClassifyPosts(path);
    		all_post_map.put(txt_name, post_list);
    	}
    	return all_post_map;
    }
    
    
    /**
     * 得测试样本到所有单词的统计(不重复)
     * @return
     */
    public static ArrayList<String>getAllWordList(){
    	getAllPostsMap();
    	return all_word_list;
    }
    
    public static void main(String[] args){
    	HashMap<String,ArrayList<Post>>all_post_map=getAllPostsMap();
    	System.out.println(all_word_list.size());
    }
    
    

}
