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
import it.polimi.meteocal.dto.WeatherDTO;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.Forecast;
import it.polimi.meteocal.entities.Location;
import it.polimi.meteocal.entities.Weather;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class of HandleForecastImplTest
 *
 * @author Matteo
 * @see HandleForecastImplTest
 */
public class HandleForecastImplTest {

    private HandleForecastImpl handleForecast;

    private TypedQuery<Forecast> query;
    private Forecast forecast;
    private Weather weather;
    private ForecastDTO forecastDTO;
    private WeatherDTO weatherDTO;

    /**
     * HandleForecastImplTest default construct
     */
    public HandleForecastImplTest() {
        // Default constructor required by JUnit
    }

    /**
     * setUpClass method
     */
    @BeforeClass
    public static void setUpClass() {
        // intentionally empty: no class-level setup required
    }

    /**
     * tearDownClass method
     */
    @AfterClass
    public static void tearDownClass() {
        // intentionally empty: no class-level teardown required
    }

    /**
     * setUp method
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

         // SETUP FORECAST
        forecast = new Forecast();
        forecast.setId(0L);
        forecast.setCreationDate(LocalDateTime.now());
        forecast.setForecastDate(LocalDateTime.now());
        forecast.setLocation("Milan, IT");
        // SETUP WEATHER
        weather = new Weather();
        weather.setId(0L);
        weather.setDescription("Weather Description");
        // SETUP BAD WEATHER CONDITION
        weather.setWeatherConditionCode("500");
        forecast.setWeather(weather);
        List<Forecast> forecasts = new ArrayList<>();
        forecasts.add(forecast);
        when(query.getResultList()).thenReturn(forecasts);

        forecastDTO = new ForecastDTO();
        // SETUP FORECAST DTO 
        forecastDTO.setId(forecast.getId());
        forecastDTO.setLocation(forecast.getLocation());
        forecastDTO.setCreationDate(forecast.getCreationDate());
        forecastDTO.setDate(forecast.getForecastDate());
        // SETUP WEATHER DTO
        weatherDTO = new WeatherDTO(0L, weather.getWeatherConditionCode(), weather.getDescription(), weather.getTemperature(), weather.getIcon());
        forecastDTO.setWeather(weatherDTO);

    }

    /**
     * tearDown method
     */
    @After
    public void tearDown() {
        // intentionally empty: no instance-level teardown required
    }

    /**
     * Test of getForecast method, of class HandleForecastImpl.
     */
    @Test
    public void testGetForecast_String_Date() {
        System.out.println("getForecast");
        String location = "Milan, IT";
        LocalDateTime date = LocalDateTime.now();
        ForecastDTO result = handleForecast.getForecast(location, date);
        assertEquals(forecastDTO, result);
    }

    /**
     * Test of getForecasts method, of class HandleForecastImpl.
     */
    @Test
    public void testGetForecasts() {
        System.out.println("getForecasts");
        String location = "Milan, IT";
        List<ForecastDTO> expResult = new ArrayList<>();
        expResult.add(forecastDTO);
        List<ForecastDTO> result = handleForecast.getForecasts(location);
        assertEquals(expResult, result);
    }

    /**
     * Test of getForecast method, of class HandleForecastImpl.
     */
    @Test
    public void testGetForecast_long() {
        System.out.println("getForecast");
        when(handleForecast.em.find(Forecast.class, forecast.getId())).thenReturn(forecast);
        long idForecast = 0L;
        ForecastDTO result = handleForecast.getForecast(idForecast);
        assertEquals(forecastDTO, result);

    }

    /**
     * Test of removeOldForecast method, of class HandleForecastImpl.
     */
    @Test
    public void testRemoveOldForecast() {
        System.out.println("removeOldForecast");
        List<Forecast> forecasts = new ArrayList<>();
        Forecast localForecast = new Forecast();
        localForecast.setId(0L);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        localForecast.setCreationDate(yesterday);
        localForecast.setForecastDate(tomorrow);
        forecasts.add(localForecast);
        when(query.getResultList()).thenReturn(forecasts);
        Event event = new Event();
        event.setLocation("Milano");
        event.setStartDate(tomorrow);
        event.setEndDate(tomorrow);
        event.setForecast(localForecast);
        TypedQuery<Event> q = mock(TypedQuery.class);
        when(handleForecast.em.createNamedQuery(Event.FIND_BY_FORECAST, Event.class)).thenReturn(q);
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);
        when(q.getResultList()).thenReturn(eventList);
        when(query.getResultList()).thenReturn(forecasts);
        when(handleForecast.em.find(Forecast.class, localForecast.getId())).thenReturn(localForecast);
        handleForecast.removeOldForecast();
        verify(handleForecast.em, times(1)).remove(localForecast);
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

    /**
     * Test of countLocations method, of class HandleForecastImpl.
     */
    @Test
    public void testCountLocations() {
        System.out.println("countLocations");
        long expectedCount = 500L;

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> criteriaQuery = mock(CriteriaQuery.class);
        Root<Location> root = mock(Root.class);
        Expression<Long> countExpression = mock(Expression.class);
        TypedQuery<Long> typedQuery = mock(TypedQuery.class);

        when(handleForecast.em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Location.class)).thenReturn(root);
        when(criteriaBuilder.count(root)).thenReturn(countExpression);
        when(criteriaQuery.select(countExpression)).thenReturn(criteriaQuery);
        when(handleForecast.em.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(expectedCount);

        long result = handleForecast.countLocations();
        assertEquals(expectedCount, result);
    }

    /**
     * Test of getForecast(long) method when no forecast exists for that ID.
     */
    @Test
    public void testGetForecastByIdNotFound() {
        System.out.println("getForecast by id - not found");
        long idForecast = 99L;
        when(handleForecast.em.find(Forecast.class, idForecast)).thenReturn(null);
        ForecastDTO result = handleForecast.getForecast(idForecast);
        assertNull(result);
    }

    /**
     * Test of getForecast(long) method when a forecast exists for that ID.
     */
    @Test
    public void testGetForecastByIdFound() {
        System.out.println("getForecast by id - found");
        Forecast localForecast = new Forecast();
        localForecast.setId(42L);
        LocalDateTime now = LocalDateTime.now();
        localForecast.setCreationDate(now);
        localForecast.setForecastDate(now);
        localForecast.setLocation("Rome, IT");
        localForecast.setLatitude(41.9f);
        localForecast.setLongitude(12.5f);

        Weather localWeather = new Weather();
        localWeather.setId(10L);
        localWeather.setWeatherConditionCode("800");
        localWeather.setDescription("Clear sky");
        localWeather.setTemperature(22.0f);
        localWeather.setIcon("01d");
        localForecast.setWeather(localWeather);

        when(handleForecast.em.find(Forecast.class, 42L)).thenReturn(localForecast);

        ForecastDTO result = handleForecast.getForecast(42L);

        assertNotNull(result);
        assertEquals(Long.valueOf(42L), result.getId());
        assertEquals("Rome, IT", result.getLocation());
        assertEquals(now, result.getDate());
        assertEquals(now, result.getCreationDate());
        assertNotNull(result.getWeather());
        assertEquals("800", result.getWeather().getWeatherConditionCode());
        assertEquals("Clear sky", result.getWeather().getDescription());
    }

    /**
     * Test of getForecasts method when no forecasts exist in DB.
     */
    @Test
    public void testGetForecastsEmpty() {
        System.out.println("getForecasts - empty");
        String location = "UnknownCity, XX";
        List<Forecast> emptyList = new ArrayList<>();
        when(query.getResultList()).thenReturn(emptyList);

        List<ForecastDTO> result = handleForecast.getForecasts(location);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test of searchLocation when the location IS found in DB.
     */
    @Test
    public void testSearchLocationFound() {
        System.out.println("searchLocation - found");
        String queryString = "Rome";
        TypedQuery<Location> queryLocation = mock(TypedQuery.class);
        when(handleForecast.em.createNamedQuery(Location.FIND_BY_SEARCHQUERY, Location.class)).thenReturn(queryLocation);

        List<Location> listLocation = new ArrayList<>();
        Location location = new Location();
        location.setName("Rome");
        location.setCountryCode("IT");
        listLocation.add(location);
        when(queryLocation.getResultList()).thenReturn(listLocation);

        List<String> result = handleForecast.searchLocation(queryString);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rome, IT", result.get(0));
    }

    /**
     * Test of searchLocation when no matches are found.
     */
    @Test
    public void testSearchLocationEmpty() {
        System.out.println("searchLocation - empty");
        String queryString = "XxXnonExistentXxX";
        TypedQuery<Location> queryLocation = mock(TypedQuery.class);
        when(handleForecast.em.createNamedQuery(Location.FIND_BY_SEARCHQUERY, Location.class)).thenReturn(queryLocation);
        when(queryLocation.getResultList()).thenReturn(new ArrayList<>());

        List<String> result = handleForecast.searchLocation(queryString);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
