package nju.iip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


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
	
	private static double train_sample_size;//整个训练样本总数
	
	
	/**
	 * 得到所有帖子的矩阵
	 * @return all_matrix
	 */
	public static ArrayList<ArrayList<Integer>>getAllMatrix(){
		all_matrix=Tools.getAllMatrix();
		System.out.println("preprocess finish!");
		return all_matrix;
	}
	
	/**
	 * 得到某一类对应的P(t/C)向量
	 * @param train_sample
	 * @param i(类别)
	 * @return
	 */
	public static ArrayList<Double>getPtc(ArrayList<ArrayList<Integer>>one_classify_sample){
		ArrayList<Double>Ptc=new ArrayList<Double>();
		int sample_size=one_classify_sample.size();
		int vector_size=one_classify_sample.get(0).size()-1;
		for(int i=0;i<vector_size;i++){
			double count=0.0;//某个单词在某一类帖子中出现次数
			for(int j=0;j<sample_size;j++){
				if(one_classify_sample.get(j).get(i)==1){
					count++;
				}
			}
			Ptc.add((count+1)/(sample_size+2));
		}
		Ptc.add(sample_size*1.0);
		return Ptc;
	}
	
	/**
	 * 根据当前训练样本构建分类器
	 * @param train_sample
	 * @return classifier(<类别，每个类别对应P(t/C)向量>)
	 */
	public static HashMap<Integer,ArrayList<Double>>getClassifier(HashMap<Integer,ArrayList<ArrayList<Integer>>>train_sample){
		HashMap<Integer,ArrayList<Double>>classifier=new HashMap<Integer,ArrayList<Double>>();
		Set<Integer>post_classify=train_sample.keySet();
		double sum=0.0;
		for(int classify:post_classify){
			ArrayList<Double>Ptc=getPtc(train_sample.get(classify));
			classifier.put(classify, Ptc);
			sum=sum+Ptc.get(Ptc.size()-1);
		}
		train_sample_size=sum;
		return classifier;
	}
	 
	
	/**
	 * 计算某篇帖子属于某个类的概率
	 * @param vector(某篇帖子对应的0，1向量)
	 * @param Ptc
	 * @return
	 */
	public static double getProbility(ArrayList<Integer>vector,ArrayList<Double>Ptc){
		double probility=0.0;
		int vector_size=vector.size()-1;
		double p=0.0;
		for(int i=0;i<vector_size;i++){
			if(vector.get(i)==1){
				p=Ptc.get(i);
			}
			else{
				p=1-Ptc.get(i);
			}
			probility=probility+Math.log(p);
		}
		double Pc=Ptc.get(vector_size)/train_sample_size;
		return probility+Math.log(Pc);
	}
	
	/**
	 * 得到某篇帖子所属类别
	 * @param vector
	 * @param classifier
	 * @return
	 */
	public static Integer getResult(ArrayList<Integer>vector,HashMap<Integer,ArrayList<Double>>classifier){
		int result=0;
		double probility=-Double.POSITIVE_INFINITY;
		Set<Integer>post_classifies=classifier.keySet();
		for(Integer classify:post_classifies){
			double temp=getProbility(vector,classifier.get(classify));
			if(temp>probility){
				probility=temp;
				result=classify;
			}
		}
		return result;
	}
	
	public static void process(){
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int k=0;k<10;k++){
			double count=0.0;
			HashMap<Integer,ArrayList<ArrayList<Integer>>>train_sample=new HashMap<Integer,ArrayList<ArrayList<Integer>>>();
			ArrayList<ArrayList<Integer>>test_sample=new ArrayList<ArrayList<Integer>>();
			Tools.divide(k, all_matrix, test_sample, train_sample);
			int vector_size=test_sample.get(0).size()-1;
			HashMap<Integer,ArrayList<Double>>classifier=getClassifier(train_sample);
			for(int i=0;i<test_sample.size();i++){
				int result=getResult(test_sample.get(i),classifier);
				int classify=test_sample.get(i).get(vector_size);
				if(result==classify){
					count++;
				}
			}
			System.out.println("第"+(k+1)+"折命中率为:"+count/100.0);
			resultList.add(count/100.0);
		}
		System.out.println("十折验证平均值为:"+Tools.getMean(resultList));
	}
	
	
	
	public static void main(String[] args){
		getAllMatrix();
		process();
	}

}
