public class Find_accuracy{

/*  public static int Find_accuracy(Node parent)
  {
      ArrayList<string> temp;                     //just a temporary ArrayList
      double count=0.0;                           // obvious
      for(int i=0;i<testDataSet.size();i++)       // iterating over testData
      {
          temp=testDataSet.get(i);
          if(checkPositiveCaseForTestData(temp,parent))   //checking whether data is positive or not
           {
            count++;
          }
      }
      double accuracy=(double)count/testDataSet.size();
      return accuracy;
  }
  public static boolean checkPositiveCaseForTestData(ArrayList<String> tempString,Node parent)   //returns true or false depending
  {
    if(parent.leafBit==1)
    {
      return parent.leafValue;
    }
    String[] words=parent.children.get(0).nodeName.split(":");  // for fetching out attributetype eg: wind from wind:high
    int attributeindextemp=attributeRangeHashMap.keySet().indexOf(words[0]); // calculating indexof element in hashmap so as to pick attributename (eg; high) from tempstring
    String attributeName=tempString.get(attributeindextemp); //getting the attributename from tempstring list
    String completeName = words[0] + ":"+attributeName; // making the nodename for finding node compared in children list
    for(int i=0;i<parent.children.size();i++)   //just interating to find that node with the name completename
    {
      if(parent.children.get(i).nodeName.equals(completeName))
      {
        return checkPositiveCaseForTestData(tempString,parent.children.get(i)); //obvious
        break;
      }
    }
  }*/
  public static void prune(Node newtree)
  {
      Node temp=new Node(newtree);
  }
}
