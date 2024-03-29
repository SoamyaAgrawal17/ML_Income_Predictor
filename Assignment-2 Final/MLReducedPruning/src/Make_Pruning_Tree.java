import java.util.*;
import java.io.*;
import java.util.ArrayList;
class Make_Pruning_Tree{
	public static void main(String args[]){
		
		
	// String array consisting of titles of all attributes ( title of each column)
		String[] attName = {"age","workclass","fnlwgt","education","education-num","marital-status","occupation","relationship","race","sex","capital-gain","capital-loss","hours-per-week","native-country"};
		
		//2-D Array consisting of traits in each attribute (15 attributes)
		String[][] attributes = new String[15][];
		attributes[0] = new String[]{"0","1","2","3","4"};
		attributes[1] = new String[]{"Private", "Self-emp-not-inc", "Self-emp-inc", "Federal-gov", "Local-gov", "State-gov", "Without-pay", "Never-worked"};
		attributes[2] = new String[]{"0","1","2","3","4"};
		attributes[3] = new String[]{"Bachelors", "Some-college", "11th", "HS-grad", "Prof-school", "Assoc-acdm", "Assoc-voc", "9th", "7th-8th", "12th", "Masters", "1st-4th", "10th", "Doctorate", "5th-6th", "Preschool"};
		attributes[4] = new String[]{"0","1","2","3","4"};
		attributes[5] = new String[]{"Married-civ-spouse", "Divorced", "Never-married", "Separated", "Widowed", "Married-spouse-absent", "Married-AF-spouse"};
		attributes[6] = new String[]{"Tech-support", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Farming-fishing", "Transport-moving", "Priv-house-serv", "Protective-serv", "Armed-Forces"};
		attributes[7] = new String[]{"Wife", "Own-child", "Husband", "Not-in-family", "Other-relative", "Unmarried"};
		attributes[8] = new String[]{"White", "Asian-Pac-Islander", "Amer-Indian-Eskimo", "Other", "Black"};
		attributes[9] = new String[]{"Female", "Male"};
		attributes[10] = new String[]{"0","1","2","3","4"};
		attributes[11] = new String[]{"0","1","2","3","4"};
		attributes[12] = new String[]{"0","1","2","3","4"};
		attributes[13] = new String[]{"United-States","Cambodia","England","Puerto-Rico","Canada", "Germany", "Outlying-US(Guam-USVI-etc)", "India", "Japan", "Greece", "South", "China", "Cuba", "Iran", "Honduras", "Philippines", "Italy", "Poland", "Jamaica", "Vietnam", "Mexico","Portugal", "Ireland", "France","Dominican-Republic", "Laos", "Ecuador", "Taiwan", "Haiti", "Columbia", "Hungary", "Guatemala", "Nicaragua", "Scotland", "Thailand", "Yugoslavia", "El-Salvador", "Trinadad&Tobago", "Peru", "Hong", "Holand-Netherlands"};
		attributes[14] = new String[]{"0", "1"};
		
		
		//creating reader objects to extract training data and putting it into 2D array "data".
		BufferedReader in = new BufferedReader(new FileReader("adult.data.txt"));
		String[][] data = new String[22792][15];
		String line = new String();
		
		
		int i=0;
		while((line=in.readLine())!=null)
		{
			data[i] = line.split(", ");
			if(data[i][14].equals("<=50K"))
			{
				data[i][14] = "0";
			}
			else
			{
				data[i][14] = "1";
			}
			i++;
		}
		in.close();
		//creating reader objects to extract testing data and putting it into 2D array "test".
		BufferedReader in1 = new BufferedReader(new FileReader("adult.test.txt"));
		String[][] test = new String[16281][15];
			
		
		i=0;
		while((line=in1.readLine())!=null)
		{
			test[i] = line.split(", ");
			if(test[i][14].equals("<=50K."))
			{
				test[i][14] = "0";
			}
			else
			{
				test[i][14] = "1";
			}
			i++;
			
		}
		in1.close();
		
		//storing column indexes which have continuous values.
		int[] continuous = new int[6];
		continuous[0] = 0;
		continuous[1] = 2;
		continuous[2] = 4;
		continuous[3] = 10;
		continuous[4] = 11;
		continuous[5] = 12;
		
		
		//creating object of "calculation" class.	
		calculation calc = new calculation();
		
		//converting all continuous valued attributes to discrete valued attributes in both "test" and "data"
		data = calc.dealContinuous(data, continuous,test,16281);
		
		//replacing all missing entries with relevant data in both "test" and "data"
		data = calc.dealMissing(data,22792,attributes,test,16281);
		
		//starting of constructing ID3
		long startTime = System.currentTimeMillis();
		
		//calculating total entropy for training data
		double entropy = calc.totalEntropy(data,i);
		
		int indexWithHighestInfogain;
		
		//extracting the index with highest information gain to make our root node.
		indexWithHighestInfogain = (int)calc.infoGain(data,32561,attributes,entropy,0);
		
		//creating root node 
		Node node = new Node(data,attributes,indexWithHighestInfogain,attName);
		
		//creating child nodes of root node
		for( i=0;i<attributes[indexWithHighestInfogain].length;i++)
		{			
			node.createChildNode(i);			
		}
		
		//creating string array to store predicted outputs of decision tree
		String[] output = new String[16281];
		
		
		//testing decision tree and filling output array with predicted results ( "adult.test.txt" has 16281 instances)
		for(int rows=0;rows<16281;rows++)
		{
			
			output[rows] = calc.test(node, test[rows], attributes);
			if(output[rows].equals("yes"))
			{
				output[rows] = "1";
				//System.out.println("Answer of "+ rows+1 + "is: >50K");
			}
			else
			{
				output[rows] = "0";
				//System.out.println("Answer of "+ rows+1 + "is: <=50K");
			}
			
		}
		PruningTest pt=new PruningTest();
		pt.pruneTree(root);
		
	}
}
		long stopTime = System.currentTimeMillis();
		
		long elapsedTime = stopTime - startTime;
		System.out.println("Time taken to do reduced error pruning decision tree(in milli-seconds): "+ elapsedTime);
		//calculating accuracy and error of decision tree
		
		int error = 0;
		int accurate = 0;
		int fp=0;
		int fn=0;
		int tp=0
		int tn=0;
		for(i=0;i<16281;i++)
		{
			
			
			if(Integer.parseInt(output[i])==Integer.parseInt(test[i][14]))
			{
				accurate++;
			}
			else
			{
				error++;
			}
			if(Integer.parseInt(output[i])==Integer.parseInt(test[i][14]) && Integer.parseInt(output[i])==1 && Integer.parseInt(test[i][14])==1)
			{
				tp++;
			}
			if(Integer.parseInt(output[i])!=Integer.parseInt(test[i][14]) && Integer.parseInt(output[i])==1 && Integer.parseInt(test[i][14])==0)
			{
				fp++;
			}
			if(Integer.parseInt(output[i])==Integer.parseInt(test[i][14]) && Integer.parseInt(output[i])==0 && Integer.parseInt(test[i][14])==0)
			{
				fn++;
			}
			if(Integer.parseInt(output[i])!=Integer.parseInt(test[i][14]) && Integer.parseInt(output[i])==0 && Integer.parseInt(test[i][14])==1)
			{
				tn++;
			}
			
		}
		System.out.println(tp+" "+fp+" "+fn+" "+tn);
		double totalAccuracy = (double)accurate/16281;
		System.out.println("Accuracy%: " + totalAccuracy*100);
		double totalError = (double)error/16281;
		System.out.println("Error%: " + totalError*100);
		double precision=0;
		//precision = tp/tp+fp
		precision=(double)(tp)/(double)(tp+fp);
		double recall=0;
		//recall =tp/tp+fn
		recall=(double)(tp)/(double)(tp+fn);
		double f_measure;
		//2*precision*recall /(precision+recall);
		f_measure=(2*precision*recall)/(precision+recall);
		System.out.println("precision "+precision);
		System.out.println("recall "+recall);
		System.out.println("f-measure "+f_measure);
		
	}

}
