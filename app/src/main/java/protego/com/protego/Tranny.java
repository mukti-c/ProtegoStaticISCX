package protego.com.protego;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.DecisionStump;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Tranny {

    String trainingSet = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iscx.arff";
    String csvFile = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "ISCXconn.csv";
    String modelFile = Environment.getExternalStorageDirectory().getAbsolutePath()  + File.separator + "model.txt";
    Instances instances;
    //FilteredClassifier classifier = new FilteredClassifier();
    DecisionStump classifier = new DecisionStump();

    public Tranny() {

    }

    //Builds Classifier
    public int build() {
        int flag = 0;
        Instances traindata = null;

        ArffLoader loader = new ArffLoader();
        try {
            loader.setFile(new File(trainingSet));
            traindata = loader.getDataSet();
            traindata.setClassIndex(traindata.numAttributes() - 1);
        } catch (IOException e) {
            flag = 1;
            e.printStackTrace();
        }

        try {
            classifier.buildClassifier(traindata);
        } catch (Exception e) {

            flag = 2;
            e.printStackTrace();
        }

        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(modelFile));
            out.writeObject(classifier);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Getting the source of the trained model
        String fpathnew = "/sdcard/finalmodel.txt";
        File f = new File(fpathnew);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            try {
                bw.write(classifier.toSource("AdaBoostM1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

    //Evalutes the built Classifier model
    public String evaluate () {

        String [] options = new String[2];
        options[0] = "-t";
        options[1] = trainingSet;

        String out = null;

        try {
            out = Evaluation.evaluateModel(classifier, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    //Classifies data
    public String classify() {

        ObjectInputStream in;
        try {
            in = new ObjectInputStream(new FileInputStream(modelFile));
            try {
                Object tmp = in.readObject();
                classifier = (DecisionStump) tmp;
                in.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String out = "";

        String text;

        try {
            ArffLoader arff;

            BufferedReader read = new BufferedReader(new FileReader(csvFile));

            try {
                while((text = read.readLine())!=null) {

                    arff = new ArffLoader();
                    arff.setFile(new File(trainingSet));
                    instances = arff.getStructure();

                    instances.setClassIndex(instances.numAttributes()-1);

                    DenseInstance instance = new DenseInstance(19);
                    instance.setDataset(instances);

                    String [] stringvalues = text.split(",");

                    instance.setValue(0, stringvalues[0]);
                    instance.setValue(1, Integer.parseInt(stringvalues[1]));
                    instance.setValue(2, Integer.parseInt(stringvalues[2]));
                    instance.setValue(3, Integer.parseInt(stringvalues[3]));
                    instance.setValue(4, Integer.parseInt(stringvalues[4]));
                    instance.setValue(5, stringvalues[5]);
                    instance.setValue(6, stringvalues[6]);
                    instance.setValue(7, stringvalues[7]);
                    instance.setValue(8, stringvalues[8]);
                    instance.setValue(9, stringvalues[9]);
                    instance.setValue(10, stringvalues[10]);
                    instance.setValue(11, stringvalues[11]);
                    instance.setValue(12, stringvalues[12]);
                    instance.setValue(13, Integer.parseInt(stringvalues[13]));
                    instance.setValue(14, stringvalues[14]);
                    instance.setValue(15, Integer.parseInt(stringvalues[15]));
                    instance.setValue(16, stringvalues[16]);
                    instance.setValue(17, stringvalues[17]);

                    instances.add(instance);

                    double pred = 0;
                    try {
                        pred = classifier.classifyInstance(instances.instance(0));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    out = out + instances.classAttribute().value((int) pred)+ "\n";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return out;
    }

}