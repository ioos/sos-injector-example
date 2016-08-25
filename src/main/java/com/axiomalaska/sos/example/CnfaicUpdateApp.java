package com.axiomalaska.sos.example;

import org.apache.log4j.Logger;

import com.axiomalaska.ioos.sos.exception.UnsupportedGeometryTypeException;
import com.axiomalaska.sos.SosInjector;
import com.axiomalaska.sos.data.PublisherInfo;
import com.axiomalaska.sos.exception.InvalidObservationCollectionException;
import com.axiomalaska.sos.exception.ObservationRetrievalException;
import com.axiomalaska.sos.exception.SosCommunicationException;
import com.axiomalaska.sos.exception.SosInjectorConfigurationException;
import com.axiomalaska.sos.exception.StationCreationException;
import com.axiomalaska.sos.exception.UnsupportedSosAssetTypeException;

public class CnfaicUpdateApp {
    private static final Logger LOGGER = Logger.getLogger(CnfaicUpdateApp.class);
    
    public static void main(String[] args) throws InvalidObservationCollectionException,
        ObservationRetrievalException, UnsupportedSosAssetTypeException, StationCreationException,
        SosCommunicationException, UnsupportedGeometryTypeException{
        if (args.length < 1 || args.length > 2) {
            LOGGER.error("Usage: sosUrl (HTTP URL of target SOS server) [authorization token]");
            return;
        }

        String sosUrl = args[0];
        String authorizationToken = args.length >= 2 ? args[1] : null;

        LOGGER.info("Using SOS URL: " + sosUrl);
        if (authorizationToken != null) {
            LOGGER.info("Using authorization token: " + authorizationToken);
        }
        
        PublisherInfo publisherInfo = new PublisherInfo();
        publisherInfo.setCode("example");
        publisherInfo.setName("Example publisher");
        publisherInfo.setEmail("example@publisher.com");
        publisherInfo.setCountry("USA");
        publisherInfo.setWebAddress("http://examplepublisher.org");
        
        SosInjector sosInjector = null;
        try {
            sosInjector = CnfaicSosInjectorFactory.buildCnfaicSosInjector(sosUrl, authorizationToken, publisherInfo);
        } catch (SosInjectorConfigurationException e) {
            LOGGER.error("Error configuring CNFAIC SosInjector", e);
        }

        if (sosInjector != null) {
            sosInjector.update();
        }
    }
}
