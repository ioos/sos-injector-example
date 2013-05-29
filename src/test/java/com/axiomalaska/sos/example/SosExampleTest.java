package com.axiomalaska.sos.example;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.axiomalaska.sos.SosInjector;
import com.axiomalaska.sos.data.PublisherInfo;
import com.axiomalaska.sos.exception.SosInjectorConfigurationException;

public class SosExampleTest {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     * @throws SosInjectorConfigurationException 
     */
    @Test
    public void testFactory() throws SosInjectorConfigurationException{
        PublisherInfo pi = new PublisherInfo();
        pi.setCode("test");
        pi.setCountry("test");
        pi.setEmail("no@no.com");
        pi.setName("test");
        pi.setWebAddress("http://test.com");

        SosInjector sosInjector = CnfaicSosInjectorFactory.buildCnfaicSosInjector("http://test.com", pi);
        assertNotNull(sosInjector);
    }

}
