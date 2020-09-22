package com.lstm.corona.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Day {

	private String Country;
	private String CountryCode;
	private Integer Confirmed;
	private Integer Deaths;
	private Integer NewDeaths;
	private Integer Recovered;
	private Integer Active;
	private Integer NewActive;
	private Date Date;

}
