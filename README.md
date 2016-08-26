# sos-injector-example

Example usage of the sos-injector

## Run the example

To run this example, download the `sos-injector-example.jar` from the
[GitHub releases page](https://github.com/ioos/sos-injector-example/releases)
(or compile this project using Maven) and then run the jar against a running
i52n-sos with `InsertSensor` and `InsertObservation` operations enabled for your IP.

Example:

```shell
java -jar sos-injector-example.jar http://localhost:8080/i52n-sos
```

If you have configured your i52n-sos to require an authorization token for transactional
requests, you can specify the token as a second argument.

Example:

```shell
java -jar sos-injector-example.jar http://localhost:8080/i52n-sos F6tfXAnPoy
```

## Run the example with Docker

The example can also be run with Docker using an image on the IOOS Docker hub.
Be aware that you __cannot__ specify `localhost` as the target i52n-sos hostname
when running in Docker, as `localhost` will refer to the container's hostname.

Example:

```
docker run --rm ioos/sos-injector-example http://10.0.0.23:8080/i52n-sos
```

## Creating a Custom Source SOS Injector

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

