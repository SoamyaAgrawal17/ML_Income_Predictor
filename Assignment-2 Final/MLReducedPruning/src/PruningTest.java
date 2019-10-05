import java.util.*;
import java.io.*;
import java.util.ArrayList;
public class PruningTest{
	
	
	
	
	public static void pruneTree(Node root) //driver method for pruning.
	{
	  /*
	  1. Iterate over the tree multiple times. Stop when pruning can no longer increase the accuracy.
	  2. In each iteration, make that node a leaf and check accuracy.
	  3. Each time, mark the node with the maximum accuracy and prune from that node.
	  4. Continue this till no node increases the accuracy of the tree.
	   */
	  boolean iterateCondition = true;
	  double accuracy = Find_accuracy(root);
	  while(iterateCondition)
	  {
		//System.out.println("Entered function");
		Node nodeToBePruned = findBestLeafNode(root); //////gets the node which on pruning gives best accuracy
		//System.out.println("Got one");
		nodeToBePruned.leafBit=1;   //make the best one a leaf
		double maxAccuracy = findAccuracy(root);
		if(maxAccuracy > accuracy) ///checking if pruning is any better
		{
		  accuracy = maxAccuracy;
		}
		else
		{
		  iterateCondition = false; //to break out in the next iteration
		  nodeToBePruned.leafBit = 0; //revert back the changes made
		}
	  }
	}
	static Node best;
	static double maxAccuracy;
	public static Node findBestLeafNode( Node root )
	{
	  maxAccuracy = 0; //maximum accuracy that can be achieved after pruning the present tree
	  DFS(root); //traverse the entire tree to know the best node to be used
	  return  best;
	}

	public static void DFS (Node node)
	{
	  if(node.leafBit == 1) //return if this node itself is a leaf
	  {
		return;
	  }

	  //*****************************************
	  //check if this node gives the best accuracy
	  node.leafBit = 1; //make it a leaf
	  node.leafValue = findMostCommonOutput( node ); //check the majority output of the node
	  //System.out.println("Could find the most common output of a node");
	  double accuracy = findAccuracy(root); //get the accuracy if this node is made a leaf
	  if(accuracy>maxAccuracy)
	  {
		best = node; //make this the best node
		maxAccuracy = accuracy;//make this the best accuracy
	  }
	  node.leafBit = 0; //make the node an internal node again
	  //*****************************************


	  for(Node child : node.children) //repeat the process for its children also
	  {
		DFS(child);
	  }
	}

	public static boolean findMostCommonOutput( Node node)  //Computes if the dataset in a node says YES or NO if it were a leaf
	{
	  int i=0;   //counters
	  int j=0;
	  for(ArrayList<String> datapoint : node.reducedDataSet)  //traverse through and count
	  {
		int lastIndex = datapoint.size()-1;
		String output = datapoint.get( lastIndex );
		if(output.equals("1"))
		{
		  i++;
		}
		else
		{
		  j++;
		}
	  }
	  if(i>=j)
	  {
		return true;
	  }
	  else
	  {
		return false;
	  }
	}
		
	
}


