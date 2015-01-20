/*
 * Copyright (C) 2015 Matteo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.meteocal.ejb;

import it.polimi.meteocal.dto.ForecastDTO;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.Forecast;
import it.polimi.meteocal.entities.Location;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Matteo
 */
public class HandleForecastImplTest {

    private HandleForecastImpl handleForecast;

    private TypedQuery<Forecast> query;

    /**
     *
     */
    public HandleForecastImplTest() {
    }

    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     *
     */
    @Before
    public void setUp() {
        handleForecast = new HandleForecastImpl();
        handleForecast.em = mock(EntityManager.class);

        query = mock(TypedQuery.class);
        when(handleForecast.em.createNamedQuery(Forecast.FIND_OLD_FORECAST,
                Forecast.class)).thenReturn(query);
        when(handleForecast.em.createNamedQuery(Forecast.FIND_BY_LOCATION,
                Forecast.class)).thenReturn(query);

    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of getForecast method, of class HandleForecastImpl.
     */
    @Test
    public void testGetForecast_String_Date() {
        System.out.println("getForecast");
        String location = "";
        Date date = new Date();
        ForecastDTO expResult = null;
        ForecastDTO result = handleForecast.getForecast(location, date);
        assertEquals(expResult, result);
    }

    /**
     * Test of getForecasts method, of class HandleForecastImpl.
     */
    @Test
    public void testGetForecasts() {
        System.out.println("getForecasts");
        String location = "";

        List<ForecastDTO> expResult = new ArrayList<>();
        List<ForecastDTO> result = handleForecast.getForecasts(location);
        assertEquals(expResult, result);
    }

    /**
     * Test of getForecast method, of class HandleForecastImpl.
     */
    @Test
    public void testGetForecast_long() {
        System.out.println("getForecast");
        long idForecast = 0L;

        ForecastDTO expResult = null;
        ForecastDTO result = handleForecast.getForecast(idForecast);
        assertEquals(expResult, result);

    }

    /**
     * Test of removeOldForecast method, of class HandleForecastImpl.
     */
    @Test
    public void testRemoveOldForecast() {
        System.out.println("removeOldForecast");
        String location = "";
        List<Forecast> forecasts = new ArrayList<>();
        ForecastDTO forecastDTO = new ForecastDTO();
        forecastDTO.setId(0L);
        Forecast forecast = new Forecast();
        forecast.setId(0L);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        forecast.setCreationDate(yesterday);
        forecast.setForecastDate(tomorrow);
        forecasts.add(forecast);
        when(query.getResultList()).thenReturn(forecasts);
        Event event = new Event();
        event.setLocation("Milano");
        event.setStartDate(tomorrow);
        event.setEndDate(tomorrow);
        event.setForecast(forecast);
        TypedQuery<Event> q = mock(TypedQuery.class);
        when(handleForecast.em.createNamedQuery(Event.FIND_BY_FORECAST, Event.class)).thenReturn(q);
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);
        when(q.getResultList()).thenReturn(eventList);
        when(query.getResultList()).thenReturn(forecasts);
        when(handleForecast.em.find(Forecast.class, forecast.getId())).thenReturn(forecast);
        handleForecast.removeOldForecast();
        verify(handleForecast.em, times(1)).remove(forecast);
        assertNotNull(event.getForecast());
    }

    /**
     * Test of searchLocation method, of class HandleForecastImpl.
     */
    @Test
    public void testSearchLocation() {
        System.out.println("searchLocation");
        String queryString = "Milano, IT";
        List<String> expResult = new ArrayList<>();
        expResult.add(queryString);
        TypedQuery<Location> queryLocation = mock(TypedQuery.class);
        when(handleForecast.em.createNamedQuery(Location.FIND_BY_SEARCHQUERY, Location.class)).thenReturn(queryLocation);
        List<Location> listLocation = new ArrayList<>();
        Location location = new Location();
        location.setName("Milano");
        location.setCountryCode("IT");
        listLocation.add(location);
        when(queryLocation.getResultList()).thenReturn(listLocation);
        List<String> result = handleForecast.searchLocation(queryString);
        assertEquals(expResult, result);
    }

}
