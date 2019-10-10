package algorithms;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
public class AdaBoost {


    public static void main(String[] args) throws Exception {

        // String array consisting of titles of all attributes ( title of each column)
        String[] attName = {"age", "workclass", "fnlwgt", "education", "education-num", "marital-status", "occupation", "relationship", "race", "sex", "capital-gain", "capital-loss", "hours-per-week", "native-country"};

        //2-D Array consisting of traits in each attribute (15 attributes)
        String[][] attributes = new String[15][];
        attributes[0] = new String[]{"0", "1", "2", "3", "4"};
        attributes[1] = new String[]{"Private", "Self-emp-not-inc", "Self-emp-inc", "Federal-gov", "Local-gov", "State-gov", "Without-pay", "Never-worked"};
        attributes[2] = new String[]{"0", "1", "2", "3", "4"};
        attributes[3] = new String[]{"Bachelors", "Some-college", "11th", "HS-grad", "Prof-school", "Assoc-acdm", "Assoc-voc", "9th", "7th-8th", "12th", "Masters", "1st-4th", "10th", "Doctorate", "5th-6th", "Preschool"};
        attributes[4] = new String[]{"0", "1", "2", "3", "4"};
        attributes[5] = new String[]{"Married-civ-spouse", "Divorced", "Never-married", "Separated", "Widowed", "Married-spouse-absent", "Married-AF-spouse"};
        attributes[6] = new String[]{"Tech-support", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Farming-fishing", "Transport-moving", "Priv-house-serv", "Protective-serv", "Armed-Forces"};
        attributes[7] = new String[]{"Wife", "Own-child", "Husband", "Not-in-family", "Other-relative", "Unmarried"};
        attributes[8] = new String[]{"White", "Asian-Pac-Islander", "Amer-Indian-Eskimo", "Other", "Black"};
        attributes[9] = new String[]{"Female", "Male"};
        attributes[10] = new String[]{"0", "1", "2", "3", "4"};
        attributes[11] = new String[]{"0", "1", "2", "3", "4"};
        attributes[12] = new String[]{"0", "1", "2", "3", "4"};
        attributes[13] = new String[]{"United-States", "Cambodia", "England", "Puerto-Rico", "Canada", "Germany", "Outlying-US(Guam-USVI-etc)", "India", "Japan", "Greece", "South", "China", "Cuba", "Iran", "Honduras", "Philippines", "Italy", "Poland", "Jamaica", "Vietnam", "Mexico", "Portugal", "Ireland", "France", "Dominican-Republic", "Laos", "Ecuador", "Taiwan", "Haiti", "Columbia", "Hungary", "Guatemala", "Nicaragua", "Scotland", "Thailand", "Yugoslavia", "El-Salvador", "Trinadad&Tobago", "Peru", "Hong", "Holand-Netherlands"};
        attributes[14] = new String[]{"0", "1"};

        //creating reader objects to extract training data and putting it into 2D array "data".
        BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Ayushi\\Desktop\\ML\\MLassignment1\\adult.data.txt"));
        String[][] data = new String[32561][15];
        String line;
        int i = 0;
        while ((line = in.readLine()) != null) {
            data[i] = line.split(", ");
            if (data[i][14].equals("<=50K")) {
                data[i][14] = "0";
            } else {
                data[i][14] = "1";
            }
            i++;
        }

        //creating reader objects to extract testing data and putting it into 2D array "test".
        BufferedReader in1 = new BufferedReader(new FileReader("C:\\Users\\Ayushi\\Desktop\\ML\\MLassignment1\\adult.test.txt"));
        String[][] test = new String[16281][15];
        i = 0;
        while ((line = in1.readLine()) != null) {
            test[i] = line.split(", ");
            if (test[i][14].equals("<=50K.")) {
                test[i][14] = "0";
            } else {
                test[i][14] = "1";
            }
            i++;

        }
        //storing column indexes which have continuous values.
        int[] continuous = new int[6];
        continuous[0] = 0;
        continuous[1] = 2;
        continuous[2] = 4;
        continuous[3] = 10;
        continuous[4] = 11;
        continuous[5] = 12;

        //creating object of "algorithms.Calculation" class.
        Calculation calc = new Calculation();

        //converting all continuous valued attributes to discrete valued attributes in both "test" and "data"
        data = calc.dealCont(data, continuous, test, 16281);

        //replacing all missing entries with relevant data in both "test" and "data"
        data = calc.dealMissing(data, 32561, attributes, test, 16281);

        //starting of constructing ID3
        long startTime = System.currentTimeMillis();

        int forestNo = 45;     //number of decision trees in a forest
        int forestData = 1707; //number of instances each decision tree contains
        int forest = 0;

        Node[] fNode = new Node[forestNo];  //array of nodes where each "node" corresponds to a decision tree

        //2D array to store the output of the various trees
        String[][] output = new String[forestNo][16281];

        // "hit" array to store the hits and misses of forests when tested on training data
        int[][] hit = new int[forestNo][32561];

        // array to store the "classifier-weight"(weight of each tree in the forest)
        double[] forestWeight = new double[forestNo];

        //array to store weights of instances in training data
        double[] weightData = new double[32561];

        //int totalWeight=0;                               //to store the sum of weights of all the instances in training data

        double[] negWeight = new double[forestNo];       //to store the sum of weights corresponding to "training data instances" which are classified incorrectly using each tree of the forest

        // weight initialization to each forest
        for (forest = 0; forest < forestNo; forest++) {
            forestWeight[forest] = 1;
        }

        //weight initialization to training data.
        for (int rows = 0; rows < 32561; rows++) {
            weightData[rows] = 1;
        }

        for (forest = 0; forest < forestNo; forest++) {

            //code to generate random number of row indexes
            int numbersNeeded = forestData;
            Random rng = new Random();
            Set<Integer> generated = new LinkedHashSet<>();
            while (generated.size() < numbersNeeded) {
                Integer next = rng.nextInt(32561);
                // As we're adding to a set, this will automatically do a containment check
                generated.add(next);
            }

            //2D array "dataF" contains number of rows equal to "forestData"
            String[][] dataF = new String[forestData][];
            int row = 0;
            for (int p : generated) {
                dataF[row] = data[p];
                row++;
            }

            //calculating total entropy for training data
            double entropy = calc.totalEntropy(dataF, forestData);


            int indexWithHighestInfogain;

            //extracting the index with highest information gain to make our root node.
            indexWithHighestInfogain = (int) calc.infoGain(dataF, forestData, attributes, entropy, 0);
            //double maxInfoGain = calc.infoGain(dataF,forestData,attributes,entropy,1);

            //creating root node
            fNode[forest] = new Node(dataF, attributes, indexWithHighestInfogain, attName);

            //creating child nodes of root node
            for (i = 0; i < attributes[indexWithHighestInfogain].length; i++) {
                fNode[forest].createChildNode(i);
            }

            //Ada-boosting (testing all our tree nodes on training data and algorithms.Calculation hits and misses on training data.
            String[] testOnTraining = new String[32561];
            for (int rows = 0; rows < 32561; rows++) {
                testOnTraining[rows] = calc.test(fNode[forest], data[rows], attributes);
                if (testOnTraining[rows].equals("yes")) {
                    testOnTraining[rows] = "1";
                } else {
                    testOnTraining[rows] = "0";
                }
                if (testOnTraining[rows].equals(data[rows][14])) {
                    hit[forest][rows] = 1;
                } else {
                    hit[forest][rows] = 0;
                }
            }

            for (int rows = 0; rows < 32561; rows++) {

                if (hit[forest][rows] == 0) {
                    negWeight[forest]++;
                }
            }


            //testing decision tree and filling output array with predicted results ( "adult.test.txt" has 16281 instances)
            for (int rows = 0; rows < 16281; rows++) {
                output[forest][rows] = calc.test(fNode[forest], test[rows], attributes);
                if (output[forest][rows].equals("yes")) {
                    output[forest][rows] = "1";
                } else {
                    output[forest][rows] = "-1";
                }

            }
        }

        //Ada-boosting :
        //temporary array to know whether a classifier is assigned weight or not. "usedForest[i]==1" means weight is already assigned to the 'i'th tree and,   "usedForest[i]==0" means weight is not yet assigned.
        int[] usedForest = new int[forestNo];

        //minimum negative weight of all the trees in the forest
        double minWeight = negWeight[0];

        //index of the tree having minimum negative weight
        int minIndex = 0;

        for (i = 0; i < forestNo; i++) {
            double w = calc.totalWeight(weightData);
            negWeight = calc.totalNegWeight(weightData, hit, negWeight);
            for (int k = 0; k < forestNo; k++) {
                if (usedForest[k] == 0) {
                    minWeight = negWeight[k];
                }
            }

            //finding out the tree with minimum negative weight
            for (int k = 0; k < forestNo; k++) {
                if (usedForest[k] == 0) {
                    if (negWeight[k] < minWeight) {
                        minWeight = negWeight[k];
                        minIndex = k;
                    }
                }
            }

            double em = minWeight / w;
            double alpha = 0;
            alpha = 0.5 * Math.log((1 - em) / em);

            //assigning weight to the tree with index "minIndex"
            forestWeight[minIndex] = alpha;
            usedForest[minIndex] = 1;

            //updating weights of the instances in training data
            weightData = calc.updateWeight(weightData, minIndex, alpha, hit);
        }


        //1D array for storing the majority result from "output[][]"
        double[] finalOut = new double[16281];
        double[][] outputNo = new double[forestNo][16281];

        //calculating final output of testing data using weights of trees calculated in Ada-boosting
        for (int ans = 0; ans < 16281; ans++) {
            for (int h = 0; h < forestNo; h++) {
                outputNo[h][ans] = Integer.parseInt(output[h][ans]);
                finalOut[ans] = finalOut[ans] + forestWeight[h] * outputNo[h][ans];
            }
            if (finalOut[ans] > 0) {
                finalOut[ans] = 1;
            } else {
                finalOut[ans] = 0;
            }

        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        log.info("Time taken for constructing random forest and Ada-boosting(in milli-seconds): " + elapsedTime);


        //calculating accuracy and error of random forest
        int error = 0;
        int accurate = 0;
        for (i = 0; i < 16281; i++) {
            if (finalOut[i] == Integer.parseInt(test[i][14])) {
                accurate++;
            } else {
                error++;
            }
        }

        double totalAccuracy = (double) accurate / 16281;
        log.info("Accuracy%: " + totalAccuracy * 100);
        double totalError = (double) error / 16281;
        log.info("Error%: " + totalError * 100);
    }
}