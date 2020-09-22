package com.lstm.corona.deeplearning;

import com.lstm.corona.model.ActiveCase;
import com.lstm.corona.model.Day;
import com.lstm.corona.model.LSTMData;
import com.lstm.corona.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

@Component
public class DeepLearnLSTM {

    private int size = 0;
    private int trainSize = 0;
    private int testSize = 0;
    private final int N_EPOCHS = 50;
    private final double LEARNING_RATED = 0.0015;
    private final double MOMENTUM = 0.9;
    private final int SEED = 1000;

    public LSTMData lstm(List<Day> countryAllDayData) throws Exception {

        long startTime = System.currentTimeMillis();

        this.size = countryAllDayData.size();
        this.trainSize = ((this.size * 75) / 100);
        this.testSize = this.size - this.trainSize;
        System.out.println(this.size+"-"+this.trainSize+"-"+this.testSize);
        // Egim icin gerekli veri seti
        DataSet trainData = getTrainingData(countryAllDayData);
        // Test icin gerekli veri seti
        DataSet testData = getTestData(countryAllDayData);
        // Scale araligi 0 ile 1 arasında
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fitLabel(true);
        // Scale icin veri seti secimi
        normalizer.fit(trainData);
        // Scale islemi gerceklesir
        normalizer.transform(trainData);
        normalizer.transform(testData);


        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder().seed(SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(LEARNING_RATED, MOMENTUM))
                .list()
                .layer(0, new LSTM.Builder().activation(Activation.TANH).nIn(1).nOut(10).build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
                        .nIn(10).nOut(1).build())
                .build();

        MultiLayerNetwork network = new MultiLayerNetwork(configuration);
        network.init();

        for (int i = 1; i <= N_EPOCHS; i++) {
            network.fit(trainData);
            System.out.print(".");
        }
        System.out.println();

        // Init rrnTimeStemp with train data and predict test data
        network.rnnTimeStep(testData.getFeatures());
        INDArray predicted = network.rnnTimeStep(testData.getFeatures());
        // Revert data back to original values for plotting

        normalizer.revert(trainData);
        normalizer.revert(testData);
        normalizer.revertLabels(predicted);

        List<ActiveCase> learn = result(predicted, countryAllDayData,this.trainSize);
        List<ActiveCase> train = result(trainData.getFeatures(), countryAllDayData, 0);
        List<ActiveCase> test = result(testData.getFeatures(), countryAllDayData, this.trainSize);

        LSTMData result = new LSTMData(train,test,learn);

        long endTime = System.currentTimeMillis();
        long estimatedTime = endTime - startTime;
        double seconds = (double) estimatedTime / 1000;
        System.out.println("Processing Time: " + seconds);

        return result;
    }
/*
    private List<ActiveCase> result(INDArray result, List<Day> data) {
        List<ActiveCase> activeCases = new ArrayList<>();
        for (Day day : data) {
            ActiveCase activeCase = new ActiveCase(day.getDate(), (long) day.getActive());
            activeCases.add(activeCase);
        }
        Date date = data.get(data.size()-1).getDate();
        for (int i = 1; i < result.length(); i++) {
            ActiveCase activeCase = new ActiveCase(new Date(date.getYear(), date.getMonth(), date.getDate() + i), (long) result.getDouble(i));
            activeCases.add(activeCase);
        }
        return activeCases;
    }
    */


    private List<ActiveCase> result(INDArray result, List<Day> data, int dataStart) {
        List<ActiveCase> activeCases = new ArrayList<>();
        for (int i = 0,j = dataStart; i < result.length(); i++,j++) {
            ActiveCase activeCase = new ActiveCase(data.get(j).getDate(),(long) result.getDouble(i));
            activeCases.add(activeCase);
        }
        return activeCases;
    }

    private DataSet getTrainingData(List<Day> countryAllDayData) {
        int size = this.trainSize+1;
        double[] seq = new double[size];
        double[] out = new double[size];

        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                seq[i] = countryAllDayData.get(i).getActive();
                out[i] = countryAllDayData.get(i).getActive();
            } else {
                seq[i] = countryAllDayData.get(i).getActive();
                out[i] = countryAllDayData.get(i + 1).getActive();
            }
        }

        INDArray seqNDArray = Nd4j.create(seq, new int[]{size, 1});
        INDArray inputNDArray = Nd4j.zeros(1, 1, size);
        inputNDArray.putRow(0, seqNDArray.transpose());

        INDArray outNDArray = Nd4j.create(out, new int[]{size, 1});
        INDArray outputNDArray = Nd4j.zeros(1, 1, size);
        outputNDArray.putRow(0, outNDArray.transpose());

        DataSet dataSet = new DataSet(inputNDArray, outputNDArray);
        return dataSet;
    }

    private DataSet getTestData(List<Day> countryAllDayData) {
        int size = this.testSize;
        double[] seq = new double[size];
        double[] out = new double[size];

        for (int i = 0,j = this.trainSize ; i < size; i++,j++) {
            if (i == size-1) {
                seq[i] = countryAllDayData.get(j).getActive();
                out[i] = countryAllDayData.get(j).getActive();
            } else {
                seq[i] = countryAllDayData.get(j).getActive();
                out[i] = countryAllDayData.get(j + 1).getActive();
            }
        }

        INDArray seqNDArray = Nd4j.create(seq, new int[]{size, 1});
        INDArray inputNDArray = Nd4j.zeros(1, 1, size);
        inputNDArray.putRow(0, seqNDArray.transpose());

        INDArray outNDArray = Nd4j.create(out, new int[]{size, 1});
        INDArray outputNDArray = Nd4j.zeros(1, 1, size);
        outputNDArray.putRow(0, outNDArray.transpose());

        DataSet dataSet = new DataSet(inputNDArray, outputNDArray);
        return dataSet;
    }
/*
    private List<Double> testData(List<Day> countryAllDayData) {
        int spacing = 3;
        List<Double> test = new ArrayList<>();
        for (int i = countryAllDayData.size() - this.testSize; i < countryAllDayData.size(); i++) {
            test.add(countryAllDayData.get(i).getActive().doubleValue());
        }
        for (int i = 0; i < this.FUTURE_DATA; i++) {
            Double total = 0.0;
            for (int j = test.size() - spacing; j < test.size(); j++) {
                total += test.get(j);
            }
            test.add(total / spacing);
        }
        return test;
    }
*/
}
