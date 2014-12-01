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
    public  static HashMap<String, Integer> segStr(String content){
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
    public  static ArrayList<Post> getOneClassifyPosts(String txt_name,int post_classify){
    	ArrayList<Post>post_list=new ArrayList<Post>();
    	try{
    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(txt_name), "UTF-8")); 
   	        String line = br.readLine();
   	        while(line != null){
   	        	HashMap<String,Integer>post_map=segStr(line);//将每行即每篇帖子分词统计
   	        	Post post=new Post(post_map,post_classify);
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
    public  static HashMap<String,ArrayList<Post>>getAllPostsMap(){
    	HashMap<String,ArrayList<Post>>all_post_map=new HashMap<String,ArrayList<Post>>();
    	int post_classify=0;
    	File f=new File(file_path);
    	String[] txt_names=f.list();//得到所有分类名
    	for(String txt_name:txt_names){
    		String path=file_path+File.separator+txt_name;
    		ArrayList<Post>post_list=getOneClassifyPosts(path,post_classify);
    		all_post_map.put(txt_name, post_list);
    		post_classify++;
    	}
    	return all_post_map;
    }
    
    
    
    
    /**
	 * 得到一篇帖子对应的特征向量
	 * @param post
	 * @return vector
	 */
	public static ArrayList<Integer>getPostVector(Post post){
		ArrayList<Integer>vector=new ArrayList<Integer>();
		HashMap<String,Integer>word_map=post.getWordMap();
		for(String word:all_word_list){
			if(word_map.containsKey(word)){
				vector.add(1);
			}
			else{
				vector.add(0);
			}
			int post_classify=post.getPostClassify();
			vector.add(post_classify);
		}
		return vector;
	}
	
	/**
	 * 得到某一类所有帖子的特征矩阵
	 * @param one_classify_posts
	 * @return one_classify_matrix
	 */
	public static ArrayList<ArrayList<Integer>>getOneClassifyMatrix(ArrayList<Post>one_classify_posts){
		ArrayList<ArrayList<Integer>>one_classify_matrix=new ArrayList<ArrayList<Integer>>();
		for(Post post:one_classify_posts){
			ArrayList<Integer>vector=getPostVector(post);
			one_classify_matrix.add(vector);
			
		}
		return one_classify_matrix;
	}
	
	/**
	 * 得到所有帖子的矩阵
	 * @return all_matrix
	 */
	public static ArrayList<ArrayList<Integer>>getAllMatrix(){
		HashMap<String,ArrayList<Post>>all_post_map=getAllPostsMap();
		ArrayList<ArrayList<Integer>>all_matrix=new ArrayList<ArrayList<Integer>>();
		 Set<String>txt_names=all_post_map.keySet();
		 for(String txt_name:txt_names){
			 ArrayList<ArrayList<Integer>>one_classify_matrix=getOneClassifyMatrix(all_post_map.get(txt_name));
			 all_matrix.addAll(one_classify_matrix);
		 }
		 return all_matrix;
	 }
    

}
