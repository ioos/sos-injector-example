sos-injector-example
====================

Example usage of the sos-injector

Creating a Custom Source SOS Injector
-------------------------------------
The source code provides an example (referenced below) to explain how to implement a custom source SOS injector.
The user needs to provide the URL for their own 52 North SOS. This example is located in the source at com.axiomalaska.sos.cnfaic.
To run the example use the following code:

    import com.axiomalaska.sos.ObservationUpdater;
    import com.axiomalaska.sos.cnfaic.CnfaicObservationUpdaterFactory;
    import com.axiomalaska.sos.data.PublisherInfo;
    import com.axiomalaska.sos.data.PublisherInfoImp;
    
    public class Main{
        public static void main(String[] args){
    
            CnfaicObservationUpdaterFactory factory = new CnfaicObservationUpdaterFactory();
    
            PublisherInfo publisherInfo = new PublisherInfoImp();
    
            ObservationUpdater observationUpdater = 
                factory.buildCnfaicObservationUpdater(“http://localhost/sos/sos”, publisherInfo);
    
            observationUpdater.update();
        }
    }

This example contains 5 stations with 3 sensors each. One of the sensors contains 3 phenomena, while the others only contain one.
When running this code for the first time, 5 stations, 15 sensors, 5 phenomena, 1 network, and around 10,000 observations will be
added to the SOS. This example uses a real source that is located at http://www.cnfaic.org/. Observations are pulled in real time from this website. 

To follow along with how this example works, open up the CnfaicObservationUpdaterFactory.java file and look at the buildCnfaicObservationUpdater
method. This method builds an ObservationUpdater object with a CnfaicStationRetriever (StationRetriever) and a CnfaicObservationRetriever
(ObservationRetriever) object.

