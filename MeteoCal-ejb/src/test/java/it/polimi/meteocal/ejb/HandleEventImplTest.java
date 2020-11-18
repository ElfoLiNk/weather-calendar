/*
 * Copyright (C) 2014 Matteo
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

import it.polimi.meteocal.dto.EventDTO;
import it.polimi.meteocal.dto.ForecastDTO;
import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.dto.WeatherDTO;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.EventNotification;
import it.polimi.meteocal.entities.Forecast;
import it.polimi.meteocal.entities.RescheduleNotification;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.entities.Weather;
import it.polimi.meteocal.exception.ErrorRequestException;
import it.polimi.meteocal.util.ContextMocker;
import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Visibility;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

/**
 * Test class of HandleEventImpl
 *
 * @author Matteo
 * @see HandleEventImpl
 */
public class HandleEventImplTest {

    @InjectMocks
    private HandleEventImpl handleEvent;

    @Spy
    private HandleForecastImpl handleForecast;

    private it.polimi.meteocal.entities.Calendar calendar;

    private User user;

    private Event event;
    private Forecast forecast;
    private ForecastDTO forecastDTO;
    private Weather weather;
    private WeatherDTO weatherDTO;
    private EventDTO eventDTO;

    private TypedQuery<EventNotification> queryNotify;
    private TypedQuery<Event> query;
    private User ep;
    private TypedQuery<RescheduleNotification> queryReschNotify;

    private AutoCloseable closeable;

    /**
     * HandleEventImplTest constructor
     */
    public HandleEventImplTest() {
    }

    /**
     * setUpClass method
     */
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     * tearDownClass method
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * setUp method
     */
    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        handleEvent = new HandleEventImpl();
        handleEvent.em = mock(EntityManager.class);
        handleEvent.handleForecast = mock(HandleForecastImpl.class);
        handleEvent.handleUser = mock(HandleUserImpl.class);

        // USER
        calendar = new it.polimi.meteocal.entities.Calendar();
        user = new User();
        user.setId(0L);
        user.setCalendar(calendar);
        when(handleEvent.em.find(User.class, user.getId())).thenReturn(user);

        // EVENT PARTICIPANT
        ep = new User();
        ep.setId(1L);
        ep.setFirstName("Mario");
        ep.setLastName("Biachi");
        it.polimi.meteocal.entities.Calendar calendarEP = new it.polimi.meteocal.entities.Calendar();
        calendarEP.addParticipatedEvent(event);
        ep.setCalendar(calendarEP);
        when(handleEvent.em.find(User.class, ep.getId())).thenReturn(ep);

        // EVENT
        event = new Event();
        event.setId(0L);
        event.setEo(user);
        event.setName("Event Title");
        event.setDescription("Description");
        event.setLocation("Milan");
        event.setStartDate(Calendar.getInstance());
        event.setEndDate(Calendar.getInstance());
        event.setEventParticipants(new ArrayList<>());
        event.setInvitedUsers(new ArrayList<>());
        event.getEventParticipants().add(ep);
        event.setVisibility(Visibility.PUBLIC);
        event.setSite(Site.INDOOR);
        forecast = new Forecast();
        forecast.setId(0L);
        forecast.setCreationDate(Calendar.getInstance());
        forecast.setForecastDate(Calendar.getInstance());
        forecast.setLocation(event.getLocation());
        weather = new Weather();
        weather.setId(0L);
        weather.setDescription("Weather Description");
        weather.setWeatherConditionCode("500");
        forecast.setWeather(weather);
        event.setForecast(forecast);
        when(handleEvent.em.find(Event.class, event.getId())).thenReturn(event);

        forecastDTO = new ForecastDTO();
        // SETUP FORECAST
        forecastDTO.setId(forecast.getId());
        forecastDTO.setLocation(event.getLocation());
        forecastDTO.setCreationDate(forecast.getCreationDate());
        forecastDTO.setDate(forecast.getCreationDate());
        // SETUP WEATHER
        weatherDTO = new WeatherDTO(0L, weather.getWeatherConditionCode(), weather.getDescription(), weather.getTemperature(), weather.getIcon());
        forecastDTO.setWeather(weatherDTO);
        eventDTO = new EventDTO("0", String.valueOf(user.getId()), "Event Title",
                Calendar.getInstance().getTime(), Calendar.getInstance().getTime(),
                true, Site.INDOOR, Visibility.PUBLIC,
                "Description", "Milan", new ArrayList<>(), new ArrayList<>(), weatherDTO);

        calendar.addOrganizedEvent(event);
        user.setCalendar(calendar);

        // TYPED QUERY
        queryNotify = mock(TypedQuery.class);
        when(handleEvent.em.createNamedQuery(EventNotification.FIND_BY_EVENT, EventNotification.class)).thenReturn(queryNotify);
        when(handleEvent.em.createNamedQuery(EventNotification.FIND_BY_EVENT_AND_USER,
                EventNotification.class)).thenReturn(queryNotify);
        queryReschNotify = mock(TypedQuery.class);
        when(handleEvent.em.createNamedQuery(RescheduleNotification.FIND_BY_EVENT,
                RescheduleNotification.class)).thenReturn(queryReschNotify);

        query = mock(TypedQuery.class);
        when(handleEvent.em.createNamedQuery(Event.FIND_NEAR_OUTDOOR, Event.class)).thenReturn(query);
        when(handleEvent.em.createNamedQuery(Event.FIND_BY_SEARCHQUERY, Event.class)).thenReturn(query);

        // SESSION
        FacesContext context = ContextMocker.mockFacesContext();
        ExternalContext ext = mock(ExternalContext.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(ext.getRequest()).thenReturn(request);
        when(context.getExternalContext()).thenReturn(ext);
        it.polimi.meteocal.auth.User authUser = new it.polimi.meteocal.auth.User(user.getId());
        when(session.getAttribute(it.polimi.meteocal.auth.User.AUTH_KEY)).thenReturn(authUser);
    }

    /**
     * tearDown method
     */
    @After
    public void tearDown() throws Exception {
        reset(handleEvent.em);
        closeable.close();
    }

    /**
     * Test of addEvento method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testAddEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("addEvent: " + timeMilli);
        doAnswer((Answer<Event>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            Event event = (Event) args[0];
            event.setId((long) 0);
            return null;
        }).when(handleEvent.em).persist(any(Event.class));
        event.setForecast(null);
        event.setEventParticipants(new ArrayList<>());
        event.setInvitedUsers(new ArrayList<>());
        long eventID = handleEvent.addEvent(user.getId(), eventDTO);
        assertEquals(eventID, 0L);
        verify(handleEvent.em, times(1)).persist(event);
    }

    /**
     * Test of getEvents method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testGetEvents() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("getEvents: " + timeMilli);
        List<EventDTO> expResult = new ArrayList<>();
        expResult.add(eventDTO);
        when(handleEvent.handleForecast.getForecast(event.getForecast().getId())).thenReturn(forecastDTO);
        List<EventDTO> result = handleEvent.getEvents(user.getId());
        assertEquals(expResult.get(0), result.get(0));
    }

    /**
     * Test of getEvent method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testGetEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("getEvent: " + timeMilli);
        when(handleEvent.handleForecast.getForecast(event.getForecast().getId())).thenReturn(forecastDTO);
        EventDTO result = handleEvent.getEvent(user.getId(), eventDTO.getId());
        assertEquals(eventDTO.getId(), result.getId());

    }

    /**
     * Test of updateEvent method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testUpdateEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("updateEvent: " + timeMilli);
        long expResult = 0L;
        eventDTO.setDescription("MODIFIED EVENT");
        long result = handleEvent.updateEvent(user.getId(), eventDTO);
        assertEquals(expResult, result);
        assertEquals(eventDTO.getDescription(), event.getDescription());
    }

    /**
     * Test of removeEvent method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testRemoveEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("removeEvent: " + timeMilli);
        handleEvent.removeEvent(user.getId(), eventDTO);
        assertFalse(user.getCalendar().getOrganizedEvents().contains(event));

    }

    /**
     * Test of cancelEvent method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testCancelEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("cancelEvent: " + timeMilli);
        handleEvent.cancelEvent(ep.getId(), eventDTO);
        assertFalse(ep.getCalendar().getParticipatedEvents().contains(event));
    }

    /**
     * Test of search method, of class HandleEventImpl.
     */
    @Test
    public void testSearch() {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("search: " + timeMilli);
        List<Event> resultEvents = new ArrayList<>();
        resultEvents.add(event);
        when(query.getResultList()).thenReturn(resultEvents);

        List<ResultDTO> expResults = new ArrayList<>();
        ResultDTO expResult = new ResultDTO();
        expResult.setId(String.valueOf(event.getId()));
        expResult.setType("EVENT");
        expResult.setName(event.getName());
        expResults.add(expResult);
        List<ResultDTO> result = handleEvent.search(event.getName());
        assertEquals(expResults, result);
    }

    /**
     * Test of moveEvent method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testMoveEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("moveEvent: " + timeMilli);
        int dayDelta = 1;
        int minuteDelta = 0;
        Calendar startDate = event.getStartDate();
        Calendar endDate = event.getEndDate();
        startDate.add(Calendar.DAY_OF_YEAR, dayDelta);
        endDate.add(Calendar.DAY_OF_YEAR, dayDelta);
        handleEvent.moveEvent(eventDTO.getId(), dayDelta, minuteDelta);
        assertEquals(event.getStartDate(), startDate);
        assertEquals(event.getEndDate(), endDate);
    }

    /**
     * Test of resizeEvent method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testResizeEvent() throws ErrorRequestException {
        //Getting the current date
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        System.out.println("resizeEvent: " + timeMilli);
        int dayDelta = 0;
        int minuteDelta = 25;
        Calendar startDate = event.getStartDate();
        Calendar endDate = event.getEndDate();
        endDate.add(Calendar.MINUTE, minuteDelta);
        handleEvent.resizeEvent(eventDTO.getId(), dayDelta, minuteDelta);
        assertEquals(event.getStartDate(), startDate);
        assertEquals(event.getEndDate(), endDate);
    }

    /**
     * Test of addParticipant method, of class HandleEventImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testAddParticipant() throws ErrorRequestException {
        System.out.println("addParticipant");
        event.getEventParticipants().remove(ep);
        ResultDTO selectedResult = new ResultDTO();
        selectedResult.setId(String.valueOf(ep.getId()));
        handleEvent.addParticipant(eventDTO.getId(), selectedResult);
        assertTrue(event.getInvitedUsers().contains(ep));
    }

    /**
     * Test of checkEventWeatherCondition method, of class HandleEventImpl.
     */
    @Test
    public void testCheckEventWeatherCondition() {
        System.out.println("checkEventWeatherCondition");
        TypedQuery<it.polimi.meteocal.entities.Calendar> queryCalendar = mock(TypedQuery.class);
        when(handleEvent.em.createNamedQuery(it.polimi.meteocal.entities.Calendar.FIND_BY_ORGANIZEDEVENT,
                it.polimi.meteocal.entities.Calendar.class)).thenReturn(queryCalendar);
        List<it.polimi.meteocal.entities.Calendar> calendarQL = new ArrayList<>();
        calendarQL.add(user.getCalendar());
        when(queryCalendar.getResultList()).thenReturn(calendarQL);
        List<Event> listEvent = new ArrayList<>();
        listEvent.add(event);
        when(queryReschNotify.getResultList()).thenReturn(new ArrayList<>());
        when(query.getResultList()).thenReturn(listEvent);
        when(handleEvent.handleForecast.getForecast(event.getLocation(), event.getStartDate().getTime())).thenReturn(forecastDTO);
        List<ForecastDTO> forecasts = new ArrayList<>();
        WeatherDTO wDTO = new WeatherDTO(weather.getId(), "801", weatherDTO.getDescription(), weatherDTO.getTemperature(), weatherDTO.getIcon());
        ForecastDTO fDTO = new ForecastDTO(forecastDTO.getId(), forecastDTO.getLocation(), forecastDTO.getLatitude(), forecastDTO.getLongitude(), forecastDTO.getDate(), forecastDTO.getCreationDate(), wDTO);
        forecasts.add(fDTO);
        when(handleEvent.handleForecast.getForecasts(event.getLocation())).thenReturn(forecasts);
        TypedQuery<Event> queryOccupation = mock(TypedQuery.class);
        when(handleEvent.em.createNamedQuery(Event.FIND_USER_OCCUPATION_RESCHEDULE, Event.class)).thenReturn(queryOccupation);
        when(queryOccupation.getResultList()).thenReturn(new ArrayList<>());
        handleEvent.checkEventWeatherCondition(user.getId());
    }

}
