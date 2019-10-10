package algorithms;

import java.util.ArrayList;

//class algorithms.Node contains variables and methods for node creation of the decision tree
public class Node {
    private String[][] data;                                        //"data" contains the instances
    private String[][] attributes;                                  //this is the array containing the attributes and their traits
    int index;                                              //"index" contains the column number
    String name;                                            //"name" stores the attribute name
    ArrayList<Node> children = new ArrayList<>();       //"children" has the list of all the child nodes of "index"
    private String[] attName;                                       //"attName" is the array containing the titles of all the attributes
    String type;                                            //"type" stores whether a node is a leaf node or a non-leaf node


    //constructor with one argument which is called for the leaf nodes
    private Node(String in) {
        this.name = in;
        this.type = "leaf_node";
    }

    //constructor which is called for a non leaf node
    Node(String[][] data, String[][] attributes, int index, String[] attName) {
        this.data = data;
        this.attributes = attributes;
        this.index = index;
        this.name = attName[index];
        this.attName = attName;
        this.type = "non_leaf_node";
    }

    //method for creating nodes
    public void createChildNode(int n) {
        //counting the number of rows that contain a particular trait
        int count = 0;
        for (String[] aData : data) {
            if (attributes[index][n].equals(aData[index])) {
                count++;
            }
        }

        //"data1" is created to store only those rows from "data" that contain that particular trait
        String[][] data1 = new String[count][];
        count = 0;
        for (String[] aData : data) {
            if (attributes[index][n].equals(aData[index])) {
                data1[count] = aData;
                count++;
            }
        }

        //creating object of class "algorithms.Calculation"
        Calculation calc = new Calculation();

        int yes = 0;
        int no = 0;
        for (int rows = 0; rows < count; rows++) {
            if (data1[rows][14].equals("1")) {
                yes++;
            } else if (data1[rows][14].equals("0")) {
                no++;
            }
        }
        if (count == 0)//creating a leaf node
        {
            Node newNode = new Node("no");
            this.children.add(newNode);
            return;
        }
        if (yes == count)//creating a leaf node
        {
            Node newNode = new Node("yes");
            this.children.add(newNode);
        } else if (no == count)//creating a leaf node
        {
            Node newNode = new Node("no");
            this.children.add(newNode);
        } else //creating a non leaf node
        {
            double totalEntropy = calc.totalEntropy(data1, count);
            int indexWithHighestInfoGain = (int) calc.infoGain(data1, count, attributes, totalEntropy, 0);
            double maxInfoGain = calc.infoGain(data1, count, attributes, totalEntropy, 1);
            if (maxInfoGain <= 0.0000000001)//creating a leaf node
            {
                Node newNode = new Node("no");
                this.children.add(newNode);
            } else //recursively going through the entire process of creating a node
            {
                Node newNode = new Node(data1, attributes, indexWithHighestInfoGain, attName);
                this.children.add(newNode);
                for (int l = 0; l < attributes[indexWithHighestInfoGain].length; l++) {
                    newNode.createChildNode(l);
                }
            }
        }
    }
}
