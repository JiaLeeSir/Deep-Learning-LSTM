package com.lstm.corona.service;

import com.lstm.corona.client.ApiClient;
import com.lstm.corona.deeplearning.DeepLearnLSTM;
import com.lstm.corona.model.*;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DayService {

    @Autowired
    private DeepLearnLSTM deepLearnLSTM;

    public List<Country> allCountry()throws Exception {
        try{
            List<Country> countries = ApiClient.apiClient.getCountry();
            return countries;
        }catch (Exception e){
            throw new Exception(e.toString());
        }
    }


    public List<Day> repeatDayDelete(List<Day> days){
        List<Day> dayList = new ArrayList<>();
        for (int i = 0; i < days.size()-1; i++){
            if(days.get(i).getDate().equals(days.get(i+1).getDate())){
                days.get(i+1).setActive(days.get(i+1).getActive()+days.get(i).getActive());
                days.get(i+1).setConfirmed(days.get(i+1).getConfirmed()+days.get(i).getConfirmed());
                days.get(i+1).setDeaths(days.get(i+1).getDeaths()+days.get(i).getDeaths());
                days.get(i+1).setRecovered(days.get(i+1).getRecovered()+days.get(i).getRecovered());
            }else{
                dayList.add(days.get(i));
            }
            if(i == days.size() - 2){
                dayList.add(days.get(i+1));
            }
        }
        return dayList;
    }

    public LSTMData getCountryData(String country) throws Exception {
        try {
            LSTMData data;
            List<Day> days = ApiClient.apiClient.getCountryAllDay(country);
            days = repeatDayDelete(days);
            data = deepLearnLSTM.lstm(days);
            return data;
        }catch (Exception e){
            throw new Exception(e.toString());
        }
    }

}
