package chakri;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Ngrams {
	public static double vocabulary;
	public static Map<String,Double> unigramCounts=new HashMap();
	public static Map<String,Double> bigramCounts=new HashMap();
	public static Map<String,Double> trigramCounts=new HashMap();


	public static void main(String[] args) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		   String statement1="Milstein is a gifted violinist who creates all sorts of sounds and arrangements";
		   String statement2="It was a strange and emotional thing to be at the opera on a Friday night";
		   System.out.println("Kindly enter values for n-gram model and smoothing");
		   System.out.println("ex: -N 2 b 0 ; where n{2,3} is value to choose model in bigram and trigram and b{0,1} is value to select one plus smoothing or not ");
		   Scanner in=new Scanner(System.in);
		   String r=in.nextLine();
		   String readConsole[]=r.split("\\s+");
		   int n,b;
		   n=Integer.parseInt(readConsole[1]);
		   b=Integer.parseInt(readConsole[3]);
		BufferedReader br = new BufferedReader(new FileReader("input.txt"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		    	String[] s=line.split("\\s+\\.");
		    	for(String a:s) {sb.append("<s> ");sb.append(a+" ");sb.append("<\\s> ");}
		        br.readLine();
		        line=br.readLine();
		    }
		    String everything = sb.toString();
		    calculateCounts(everything);
		    double[] output=new double[2];
		    String model="";
		    String gram="";
		    String smoothing="without Smoothing";
		    switch(n) {
		    case 2:model+="bigramProbability";gram="Bi";break;
		    	default:gram="Tri";model+="trigramProbability";
		    }
		    if(b==1) {smoothing="with Smoothing";model+="Smoothing";}
		    Method method = Ngrams.class.getDeclaredMethod(model,String.class);
		    
		    output[0]=(double)method.invoke(Ngrams.class,statement1);
		    output[1]=(double)method.invoke(Ngrams.class,statement2);
		    System.out.println("Bigram Counts for statement1");
		    System.out.println(bigramsCount(statement1));
		    System.out.println("Trigram Counts for statement1");
		    System.out.println(trigramsCount(statement1));
		    System.out.println("Bigram Counts for statement2");
		    System.out.println(bigramsCount(statement2));
		    System.out.println("Trigram Counts for statement2");
		    System.out.println(trigramsCount(statement2));
		    System.out.println(gram+"gram Probabilities "+smoothing);
		    
		    System.out.println(Arrays.toString(output));
		    
		} finally {
		    br.close();
		}
	}
	public static List<List<Double>> bigramsCount(String statement) {
		List<List<Double>> list=new ArrayList();
		String[] words=statement.split("\\s+");
		String[] addTags=new String[words.length+2];int k=1;
		for(String a:words) {addTags[k]=a;k++;}
		addTags[words.length+1]="<\\s>";
		addTags[0]="<s>";
		for(int i=0;i<addTags.length;i++) {
			List<Double> temp=new ArrayList();
			for(int j=0;j<addTags.length;j++) {
				temp.add(bigramCounts.getOrDefault(addTags[j]+" "+addTags[i],0.0));
			}list.add(temp);
		}
		return list;
		
	}
	public static List<List<Double>> trigramsCount(String statement) {
		List<List<Double>> list=new ArrayList();
		String[] words=statement.split("\\s+");
		String[] addTags=new String[words.length+2];int k=1;
		for(String a:words) {addTags[k]=a;k++;}
		addTags[words.length+1]="<\\s>";
		addTags[0]="<s>";
		for(int i=0;i<addTags.length;i++) {List<Double> temp=new ArrayList();
			for(int j=1;j<addTags.length;j++) {
				temp.add(trigramCounts.getOrDefault(addTags[j-1]+" "+addTags[j]+" "+addTags[i],0.0));
				}list.add(temp);
		}
		return list;
	}
	public static void calculateCounts(String corpus) {
		String[] words=corpus.split("\\s+");
		for(String a:words) {
			if(unigramCounts.containsKey(a)) {
			Double target=unigramCounts.get(a)+1.0;
			unigramCounts.put(a, target);
		}else {unigramCounts.put(a, 1.0);}
			vocabulary=unigramCounts.size();

			}
		for(int i=1;i<words.length;i++) {
			if(bigramCounts.containsKey(words[i-1]+" "+words[i])) {
				Double target=bigramCounts.get(words[i-1]+" "+words[i])+1.0;
				bigramCounts.put(words[i-1]+" "+words[i], target);
			}else {bigramCounts.put(words[i-1]+" "+words[i], 1.0);}
			
		}
		for(int i=2;i<words.length;i++) {
			if(trigramCounts.containsKey(words[i-2]+" "+words[i-1]+" "+words[i])) {
				Double target=trigramCounts.get(words[i-2]+" "+words[i-1]+" "+words[i])+1.0;
				trigramCounts.put(words[i-2]+" "+words[i-1]+" "+words[i], target);
			}
			else {trigramCounts.put(words[i-2]+" "+words[i-1]+" "+words[i], 1.0);}
		}
	}
	
	public static double bigramProbability(String statement) {
		double probability=1;
		String[] words=statement.split("\\s+");
		String[] addTags=new String[words.length+2];int k=1;
		for(String a:words) {addTags[k]=a;k++;}
		addTags[words.length+1]="<\\s>";
		addTags[0]="<s>";
		for(int i=1;i<addTags.length;i++) {
			double combination=0,given=0;
			if(bigramCounts.containsKey(addTags[i-1]+" "+addTags[i])) {combination=bigramCounts.get(addTags[i-1]+" "+addTags[i]);}
			if(unigramCounts.containsKey(addTags[i-1])) {given=unigramCounts.get(addTags[i-1]);}
			if(given==0) {return 0;}
			else {
				probability*=(combination/given);
			}
			
		}
		return probability;
	}
	public static double bigramProbabilitySmoothing(String statement) {
		double probability=1;
		String[] words=statement.split("\\s+");
		String[] addTags=new String[words.length+2];int k=1;
		for(String a:words) {addTags[k]=a;k++;}
		addTags[words.length+1]="<\\s>";
		addTags[0]="<s>";
		for(int i=1;i<addTags.length;i++) {
			double combination=0,given=0;
			if(bigramCounts.containsKey(addTags[i-1]+" "+addTags[i])) {combination=bigramCounts.get(addTags[i-1]+" "+addTags[i]);}
			if(unigramCounts.containsKey(addTags[i-1])) {given=unigramCounts.get(addTags[i-1]);}
				probability*=((combination+1)/(given+vocabulary));
			
		}
		return probability;
	}
	public static double trigramProbabilitySmoothing(String statement) {
		double probability=1;
		String[] words=statement.split("\\s+");
		String[] addTags=new String[words.length+2];int k=1;
		for(String a:words) {addTags[k]=a;k++;}
		addTags[words.length+1]="<\\s>";
		addTags[0]="<s>";
		for(int i=2;i<addTags.length;i++) {
			double combination=0,given=0;
			if(trigramCounts.containsKey(addTags[i-2]+" "+addTags[i-1]+" "+addTags[i])) {combination=trigramCounts.get(addTags[i-2]+" "+addTags[i-1]+" "+addTags[i]);}
			if(bigramCounts.containsKey(addTags[i-2]+" "+addTags[i-1])) {given=bigramCounts.get(addTags[i-2]+" "+addTags[i-1]);}
				probability*=((combination+1)/(given+vocabulary));
			
		}
		return probability;
	}
	public static double trigramProbability(String statement) {
		double probability=1;
		String[] words=statement.split("\\s+");
		String[] addTags=new String[words.length+2];int k=1;
		for(String a:words) {addTags[k]=a;k++;}
		addTags[words.length+1]="<\\s>";
		addTags[0]="<s>";
		for(int i=2;i<addTags.length;i++) {
			double combination=0,given=0;
			if(trigramCounts.containsKey(addTags[i-2]+" "+addTags[i-1]+" "+addTags[i])) {combination=trigramCounts.get(addTags[i-2]+" "+addTags[i-1]+" "+addTags[i]);}
			if(bigramCounts.containsKey(addTags[i-2]+" "+addTags[i-1])) {given=bigramCounts.get(addTags[i-2]+" "+addTags[i-1]);}
			if(given==0) {return 0;}	
			probability*=(combination/given);
			
		}
		return probability;
	}
	
	

	

}
