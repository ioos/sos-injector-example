package com.axiomalaska.sos.example;

import com.axiomalaska.sos.SosInjector;
import com.axiomalaska.sos.data.PublisherInfo;
import com.axiomalaska.sos.exception.SosInjectorConfigurationException;

public class CnfaicSosInjectorFactory {

	// -------------------------------------------------------------------------
	// Public Members
	// -------------------------------------------------------------------------
		
	public static SosInjector buildCnfaicSosInjector(String sosUrl, PublisherInfo publisherInfo)
	        throws SosInjectorConfigurationException{

		SosInjector sosInjector = new SosInjector("CNFAIC SOS Injector", sosUrl, publisherInfo,
		        new CnfaicStationRetriever(), new CnfaicObservationRetriever(), null );
		
		return sosInjector;
	}
}
