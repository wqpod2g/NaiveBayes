package nju.iip;

import java.util.ArrayList;


/**
 * 朴素贝叶斯伯努利模型实现
 * @author wangqiang
 * @since 2014-12-1
 *
 */
public class NaiveBayesBernoulli {
	
	/**
	 * 所有帖子组成的特征矩阵
	 */
	private static ArrayList<ArrayList<Integer>>all_matrix=new ArrayList<ArrayList<Integer>>();
	
	
	/**
	 * 得到所有帖子的矩阵
	 * @return all_matrix
	 */
	public static ArrayList<ArrayList<Integer>>getAllMatrix(){
		all_matrix=Tools.getAllMatrix();
		return all_matrix;
	}
	
	
	 
	
	
	public static void main(String[] args){
		getAllMatrix();
		System.out.println(all_matrix.get(0).get(all_matrix.get(0).size()-1));
	}

}
