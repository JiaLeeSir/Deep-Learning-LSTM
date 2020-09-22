package com.lstm.corona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LSTMData {

    List<ActiveCase> trainData;
    List<ActiveCase> testData;
    List<ActiveCase> learnData;

}
