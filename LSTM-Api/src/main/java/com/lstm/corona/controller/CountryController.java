package com.lstm.corona.controller;

import com.lstm.corona.model.Country;
import com.lstm.corona.model.LSTMData;
import com.lstm.corona.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/Api")
public class CountryController {

    @Autowired
    private DayService dayService;

    @GetMapping("/GetCountrySummary/{country}")
    public LSTMData getCountrySummary(@PathVariable String country) throws Exception {
        return dayService.getCountryData(country);
    }

    @GetMapping("/GetAllCountry")
    public List<Country> getAllCountry() throws Exception {
        return dayService.allCountry();
    }

}
