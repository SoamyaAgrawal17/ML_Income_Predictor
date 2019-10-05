

import java.util.*;


public class calculation {
	
	//method to calculate total entropy of the instances present in "input[][]" array of length "count"
	public double totalEntropy(String[][] input,int count)
	{
		double ent = 0;
		int pos = 0;
		int neg = 0;
		for(int i=0;i<count;i++)
		{
			if(input[i][14].equals("0"))
			{
				neg++;
			}
			else
			{
				pos++;
			}
		}
		
		int total = pos+neg;
		
		ent = ((double)pos/(double)total)*((Math.log((double)pos/(double)total))/(Math.log(2))) + ((double)neg/(double)total)*((Math.log((double)neg/(double)total))/(Math.log(2))) ;
		
		ent = -ent;
		return ent;
	}
	
	
	//method to convert continuous valued attributes to discrete valued attributes
	public String[][] dealCont(String[][] data,int[] continuous,String[][] test, int lOfTest)
	{
		//variables to store threshold values
		int c1,c2,c3,c4;
		int j,m;
		
		for( j=0;j<continuous.length;j++){
			int k = continuous[j];
			int[] temp = new int[32561];
			for(m=0;m<32561;m++)
			{
				temp[m] = Integer.parseInt(data[m][k]);
			}
			Arrays.sort(temp);
			//classifying all the instances of a continuous attribute into 5 categories of "0,1,2,3,4" which is obtained at 1/5th,2/5th,3/5th,4/5th of 32561
			c1 = temp[6512];
			c2 = temp[13024];
			c3 = temp[19536];
			c4 = temp[26048];
			
			
			for(m=0;m<32561;m++)
			{
				int g = Integer.parseInt(data[m][k]);
				if(g<c1)
				{
					data[m][k] = "0";
				}
				else if(g>=c1&&g<c2)
				{
					data[m][k] = "1";
				}
				else if(g>=c2&&g<c3)
				{
					data[m][k] = "2";
				}
				else if(g>=c3&&g<c4)
				{
					data[m][k] = "3";
				}
				else 
				{
					data[m][k] = "4";
				}
				
			}
			for(m=0;m<lOfTest;m++)
			{
				int g = Integer.parseInt(test[m][k]);
				if(g<c1)
				{
					test[m][k] = "0";
				}
				else if(g>=c1&&g<c2)
				{
					test[m][k] = "1";
				}
				else if(g>=c2&&g<c3)
				{
					test[m][k] = "2";
				}
				else if(g>=c3&&g<c4)
				{
					test[m][k] = "3";
				}
				else 
				{
					test[m][k] = "4";
				}
				
			}
		}		
		return data;
	}
	
	//method to replace missing data with relevant entries
	public String[][] dealMissing(String[][] data,int length,String[][] attributes,String[][] test,int lOfTest)
	{
		int b=0;
		//Using a set to collect all the column indexes with missing entries
		Set<Integer> set = new HashSet<Integer>();
		for(int i=0;i<32561;i++)
		{
			for(int j=0;j<15;j++)
			{
				if(data[i][j].equals("?"))
				{
					set.add(j);
				}
			}
		}
		
		int n = set.size();
		//converting set to an array "missCol"
		int[] missCol = new int[n];
		for(int i:set){
			missCol[b] = i;
			b++;
		}
		
		//iterating through the entire "missCol" array
		for(b=0;b<n;b++)
		{
			int k = missCol[b];
			//creating two arrays yes[] and no[] to store the number of "1" and "0" results of each trait
			int traits = attributes[k].length;
			int[] yes = new int[traits];
			int[] no = new int[traits];
			for(int l=0;l<traits;l++)
			{
				for(int rows=0;rows<length;rows++)
				{
					if(data[rows][k].equals(attributes[k][l])&&(data[rows][14].equals("1"))){
						yes[l]++;
					}
					else if(data[rows][k].equals(attributes[k][l])&&(data[rows][14].equals("0"))){
						no[l]++;
					}
				}
			}
			//Calculating the trait with the maximum number of "1" and "0" and its index
			int maxYes = 0;
			int maxNo = 0;
			int ansYes=0,ansNo=0;
			for(int l=0;l<traits;l++)
			{
				if(yes[l]>maxYes)
				{
					maxYes = yes[l];
					ansYes = l;
				}
				if(no[l]>maxNo)
				{
					maxNo = no[l];
					ansNo = l;
				}
			}
			
			
			for(int rows=0;rows<length;rows++)
			{
				if(data[rows][k].equals("?"))
				{
					if(data[rows][14].equals("0"))
					{
						data[rows][k] = attributes[k][ansNo];
					}
					else
					{
						data[rows][k] = attributes[k][ansYes];
					}
				}
			
			}
			
			for(int rows = 0;rows<lOfTest;rows++)
			{
				if(test[rows][k].equals("?"))
				{
						test[rows][k] = attributes[k][ansNo];
					
				}
			
			}
			
		}
		
		return data;
	}
	
	//method for calculating the index of the attribute with highest information gain (when flag=0) and the value of the entropy of this trait(when flag=1)
	public double infoGain(String[][] data,int length,String[][] attributes,double totalEntropy,int flag)
	{
		double[] entropy = new double[14];
		double temp;
		
		for(int i=0;i<14;i++)
		{
			
			int[] yes = new int[attributes[i].length];
			int[] no = new int[attributes[i].length];
			int[] total = new int[attributes[i].length];
			for(int j=0;j<attributes[i].length;j++)
			{
				
				for(int rows=0;rows<length;rows++)
				{
					if(data[rows][i].equals(attributes[i][j]))
					{
						if(data[rows][14].equals("1"))
						{
							yes[j]++;
						}
						else if(data[rows][14].equals("0"))
						{
							no[j]++;
						}
					}
				}
			   total[j] = yes[j] + no[j];
			}
			
			
			temp=0.0;
			for(int j=0;j<attributes[i].length;j++)
			{
				if(yes[j]==0||no[j]==0)
				{	}
				else
				temp = temp + ((double)total[j]/length)*((((double)yes[j]/(double)total[j])*(Math.log((double)yes[j]/(double)total[j]))/(Math.log(2))) + ((double)no[j]/(double)total[j])*((Math.log((double)no[j]/(double)total[j]))/(Math.log(2)))) ;
			}
			entropy[i] = -temp;
		}
		
		for(int i=0;i<14;i++)
		{
			entropy[i] = totalEntropy - entropy[i];		
			
		}
		
		double max = 0;
		int maxi=0;
		for(int i=0;i<14;i++)
		{
			if(entropy[i]>max)
			{
				max = entropy[i];
				maxi = i;
			}
		}
		if(flag==0)
		return maxi;
		else
		return max;
	}
	
	//method to traverse the decision tree and predict an output 
	public String test(Node node,String[] test,String[][] attributes){
		String out = "";
		if(node.type.equals("leaf_node"))
		{
			return node.name;
		}
		else
		{	
		int att = node.index;
		String s = test[att];
		Node[] n1 = new Node[node.children.size()];
		n1 = node.children.toArray(n1);
		for(int i=0;i<n1.length;i++)
		{
			if(s.equals(attributes[att][i])){
				out = test(n1[i],test,attributes);
			}
		}		
		}				
		return out;		
	}

}
