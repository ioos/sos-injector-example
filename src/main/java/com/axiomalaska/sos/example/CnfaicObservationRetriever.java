package com.axiomalaska.sos.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ucar.units.ConversionException;
import ucar.units.SI;

import com.axiomalaska.phenomena.CustomUnits;
import com.axiomalaska.phenomena.Phenomena;
import com.axiomalaska.phenomena.Phenomenon;
import com.axiomalaska.phenomena.UnitCreationException;
import com.axiomalaska.phenomena.UnitResolver;
import com.axiomalaska.sos.ObservationRetriever;
import com.axiomalaska.sos.data.ObservationCollection;
import com.axiomalaska.sos.data.SosSensor;
import com.axiomalaska.sos.tools.HttpSender;

public class CnfaicObservationRetriever implements ObservationRetriever {

	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------
    private final static Logger LOGGER = Logger.getLogger(CnfaicObservationRetriever.class);
    //CNFAIC time is locked to AKST, even in summer!
    private final static DateTimeZone akTime = DateTimeZone.forID("Etc/GMT+9");
    private final static DateTimeFormatter akTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(akTime);
    
	private final static int DATE_INDEX = 1;
	private final static int AIR_TEMPERATURE_INDEX = 2;
	private final static int RELATIVE_HUMIDITY_INDEX = 3;
	private final static int WIND_SPEED_INDEX = 4;
	private final static int WIND_DIRECTION_INDEX = 5;
	private final static int WIND_GUST_INDEX = 6;
	
	private final static int MAX_HOURS_TO_FETCH = 720;
	
	// -------------------------------------------------------------------------
	// Public ObservationRetriever
	// -------------------------------------------------------------------------
	@Override
	public List<ObservationCollection> getObservationCollection(SosSensor sensor,
	        Phenomenon phenomenon, DateTime startDate) {
        ArrayList<ObservationCollection> observationCollections = new ArrayList<ObservationCollection>();
        DateTime nowAk = DateTime.now(akTime);
        int hoursToFetch = MAX_HOURS_TO_FETCH;
        if (Years.yearsBetween(startDate, nowAk).getYears() == 0){
            hoursToFetch = Math.min(MAX_HOURS_TO_FETCH, Hours.hoursBetween(startDate, nowAk).getHours());
        }
		
		boolean nonRecoverableExceptionThrown = false;
		String rawObservationData = null;
        try {
            rawObservationData = HttpSender.sendGetMessage(
            		"http://www.cnfaic.org/library/grabbers/nws_feed.php?hours=" + hoursToFetch);
        } catch (IOException e) {
            LOGGER.error("Error fetching CNFAIC data", e);
            nonRecoverableExceptionThrown = true;
        }

		Phenomenon airTemperaturePhenomenon = null;
		Phenomenon relativeHumidityPhenomenon = null;
		Phenomenon windSpeedPhenomenon = null;
		Phenomenon windfromDirectionPhenomenon = null;
		Phenomenon windSpeedOfGustPhenomenon = null;
        try {
            airTemperaturePhenomenon = Phenomena.instance().AIR_TEMPERATURE;
            relativeHumidityPhenomenon = Phenomena.instance().RELATIVE_HUMIDITY;
            windSpeedPhenomenon = Phenomena.instance().WIND_SPEED;
            windfromDirectionPhenomenon = Phenomena.instance().WIND_FROM_DIRECTION;
            windSpeedOfGustPhenomenon = Phenomena.instance().WIND_SPEED_OF_GUST;            
        } catch (UnitCreationException e) {
            LOGGER.error("Error creating unit.", e);
            nonRecoverableExceptionThrown = true;
        }

        if (nonRecoverableExceptionThrown) {
            return observationCollections;
        }
        
		Pattern observationParser = Pattern.compile(sensor.getStation().getAsset().getStation() + 
		        ",(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}),(\\d+.\\d+),(\\d+),(\\d+),(\\d+),(\\d+)");
	
		Matcher matcher = observationParser.matcher(rawObservationData);
		
        ObservationCollection observationCollection = new ObservationCollection();        
        observationCollection.setSensor(sensor);
        observationCollection.setPhenomenon(phenomenon);
        observationCollection.setGeometry(sensor.getLocation());

		while(matcher.find()){ 
			DateTime dateTime = akTimeFormatter.parseDateTime(matcher.group(DATE_INDEX));
			if (dateTime.isAfter(startDate) && !observationCollection.hasObservationValue(dateTime)) {			    
			    String valueString = null;
                try {			    
    				if (phenomenon.getId().equals(airTemperaturePhenomenon.getId())) {
                            valueString = Double.toString(UnitResolver.instance().resolveUnit("degrees_F")
                                    .convertTo(Double.valueOf(matcher.group(AIR_TEMPERATURE_INDEX)),
                                            SI.DEGREE_CELSIUS));
    				} else if (phenomenon.getId().equals(relativeHumidityPhenomenon.getId())) {
    				    valueString = matcher.group(RELATIVE_HUMIDITY_INDEX);				    
    				} else if (phenomenon.getId().equals(windSpeedPhenomenon.getId())) {
                        valueString = Double.toString(UnitResolver.instance().resolveUnit("miles per hour")
                                .convertTo(Double.valueOf(matcher.group(WIND_SPEED_INDEX)),
                                        CustomUnits.instance().METERS_PER_SECOND));
    				} else if (phenomenon.getId().equals(windfromDirectionPhenomenon.getId())) {
    				    valueString = matcher.group(WIND_DIRECTION_INDEX);				    
    				} else if (phenomenon.getId().equals(windSpeedOfGustPhenomenon.getId())) {
                        valueString = Double.toString(UnitResolver.instance().resolveUnit("miles per hour")
                                .convertTo(Double.valueOf(matcher.group(WIND_GUST_INDEX)),
                                        CustomUnits.instance().METERS_PER_SECOND));
    				}    
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (ConversionException e) {
                    e.printStackTrace();
                } catch (UnitCreationException e) {
                    e.printStackTrace();
                }
                
                if (valueString != null) {
                    observationCollection.addObservationValue(dateTime, Double.parseDouble(valueString));
                }                
			}
		}
		
		if (observationCollection.getObservationValues().size() > 0) {
		    observationCollections.add(observationCollection);
		} else {
		    LOGGER.info("No new observations for " + observationCollection + ", skipping");		    
		}
		return observationCollections;
	}
}
