/*
 * Copyright (C) 2014 Matteo Gazzetta, Alessandro Fato
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
import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.entities.Calendar;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.EventNotification;
import it.polimi.meteocal.entities.Forecast;
import it.polimi.meteocal.entities.RescheduleNotification;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.entities.Weather;
import it.polimi.meteocal.exception.ErrorRequestException;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.Status;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class HandleEventImpl
 */
@Stateless
public class HandleEventImpl implements HandleEvent {

    private static final Logger LOGGER = LogManager.getLogger(HandleEventImpl.class.getName());

    @PersistenceContext
    EntityManager em;

    @EJB
    HandleUser handleUser;

    @EJB
    HandleForecast handleForecast;

    @Override
    public long addEvent(Long userId, EventDTO insertEvent) throws ErrorRequestException {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new ErrorRequestException("no user", false);

        }
        // NEW EVENT 
        java.util.Calendar startCalendar = java.util.Calendar.getInstance();
        startCalendar.setTime(insertEvent.getStartDate());
        java.util.Calendar endCalendar = java.util.Calendar.getInstance();
        endCalendar.setTime(insertEvent.getEndDate());
        Event event = new Event(user, insertEvent.getTitle(), insertEvent.getDescription(), insertEvent.getLocation(), insertEvent.getSite(), startCalendar, endCalendar, null, insertEvent.getVisibility(), new ArrayList<>(), new ArrayList<>());

        if (event.getStartDate().get(java.util.Calendar.DAY_OF_YEAR) >= java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
                && event.getStartDate().get(java.util.Calendar.DAY_OF_YEAR) <= (java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR) + 15)
                && insertEvent.getLocation() != null) {
            // ADD FORECAST INFORMATION IF AVAILABLE
            ForecastDTO forecastDTO = handleForecast.getForecast(insertEvent.getLocation(), insertEvent.getStartDate());
            if (forecastDTO != null) {
                event.setForecast(mapForecastDTOToForecast(forecastDTO));

            }

        }
        // ADD INVITED USER
        List<User> invitedUser = new ArrayList<>();
        if (insertEvent.getInvitedUsers() != null) {
            for (UserDTO userDto : insertEvent.getInvitedUsers()) {
                User userInvited = em.find(User.class, userDto.getId());
                if (userInvited == null) {
                    throw new ErrorRequestException("no user", false);

                }
                invitedUser.add(userInvited);
            }
        }
        event.setInvitedUsers(invitedUser);
        // ADD NEW EVENT TO USER CALENDAR
        Calendar calendar = user.getCalendar();
        calendar.addOrganizedEvent(event);
        em.persist(event);
        em.merge(user);
        em.flush();
        return event.getId();
    }

    @Override
    public List<EventDTO> getEvents(Long userId) throws ErrorRequestException {
        List<EventDTO> events = new ArrayList<>();
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new ErrorRequestException("no user", false);

        }

        for (Event event : user.getCalendar().getOrganizedEvents()) {
            List<UserDTO> eventParticipants = new ArrayList<>();
            for (User userParticipate : event.getEventParticipants()) {
                UserDTO ep = handleUser.getUser(userParticipate.getId());
                if (ep != null) {
                    eventParticipants.add(ep);
                }
            }
            List<UserDTO> invitedUsers = new ArrayList<>();
            for (User userInvited : event.getInvitedUsers()) {
                UserDTO ei = handleUser.getUser(userInvited.getId());
                if (ei != null) {
                    invitedUsers.add(ei);
                }

            }
            EventDTO eventDto;
            if (event.getForecast() != null) {
                eventDto = new EventDTO(event.getId().toString(), event.getEo().getId().toString(), event.getName(), event.getStartDate().getTime(), event.getEndDate().getTime(), true, event.getSite(), event.getVisibility(), event.getDescription(), event.getLocation(), eventParticipants, invitedUsers, handleForecast.getForecast(event.getForecast().getId()).getWeather());
            } else {
                eventDto = new EventDTO(event.getId().toString(), event.getEo().getId().toString(), event.getName(), event.getStartDate().getTime(), event.getEndDate().getTime(), true, event.getSite(), event.getVisibility(), event.getDescription(), event.getLocation(), eventParticipants, invitedUsers, null);
            }
            events.add(eventDto);
        }

        for (Event event : user.getCalendar().getParticipatedEvents()) {
            List<UserDTO> eventParticipants = new ArrayList<>();
            for (User userParticipate : event.getEventParticipants()) {
                eventParticipants.add(handleUser.getUser(userParticipate.getId()));
            }
            List<UserDTO> invitedUsers = new ArrayList<>();
            for (User userInvited : event.getInvitedUsers()) {
                invitedUsers.add(handleUser.getUser(userInvited.getId()));
            }
            EventDTO eventDto;
            if (event.getForecast() != null) {
                eventDto = new EventDTO(event.getId().toString(), event.getEo().getId().toString(), event.getName(), event.getStartDate().getTime(), event.getEndDate().getTime(), false, event.getSite(), event.getVisibility(), event.getDescription(), event.getLocation(), eventParticipants, invitedUsers, handleForecast.getForecast(event.getForecast().getId()).getWeather());
            } else {
                eventDto = new EventDTO(event.getId().toString(), event.getEo().getId().toString(), event.getName(), event.getStartDate().getTime(), event.getEndDate().getTime(), false, event.getSite(), event.getVisibility(), event.getDescription(), event.getLocation(), eventParticipants, invitedUsers, null);
            }
            events.add(eventDto);
        }

        return events;
    }

    @Override
    public EventDTO getEvent(Long userId, String eventId) throws ErrorRequestException {
        Event event = em.find(Event.class, Long.valueOf(eventId));
        if (event == null) {
            throw new ErrorRequestException("no event", false);
        }

        List<UserDTO> eventParticipants = new ArrayList<>();
        for (User user : event.getEventParticipants()) {
            eventParticipants.add(handleUser.getUser(user.getId()));
        }
        List<UserDTO> invitedUsers = new ArrayList<>();
        for (User user : event.getInvitedUsers()) {
            invitedUsers.add(handleUser.getUser(user.getId()));
        }
        EventDTO evento = new EventDTO(eventId, null, event.getName(), event.getStartDate().getTime(),
                event.getEndDate().getTime(), true, event.getSite(), event.getVisibility(), event.getDescription(),
                event.getLocation(), eventParticipants, invitedUsers, null);
        if (Objects.equals(userId, event.getEo().getId())) {
            evento.setEoId(userId.toString());
        } else {
            evento.setEoId(event.getEo().getId().toString());

        }
        if (event.getForecast() != null) {
            ForecastDTO forecast = handleForecast.getForecast(event.getForecast().getId());
            if (forecast != null) {
                evento.setWeather(forecast.getWeather());
            }
        } else {
            ForecastDTO forecast = handleForecast.getForecast(event.getLocation(), event.getStartDate().getTime());
            if (forecast != null) {
                evento.setWeather(forecast.getWeather());
            }
        }
        return evento;

    }

    /**
     * Method that updates the information of the event in the db with the new
     * information in the updated insert event
     *
     * @param eventOld the current event in the DB
     * @param insertEvent the new information to update in the old event
     * @return Event the updated event
     */
    private Event modifyEvent(Event eventOld, EventDTO insertEvent) {
        java.util.Calendar startCalendar = eventOld.getStartDate();
        startCalendar.setTime(insertEvent.getStartDate());
        java.util.Calendar endCalendar = eventOld.getEndDate();
        endCalendar.setTime(insertEvent.getEndDate());
        eventOld.setName(insertEvent.getTitle());
        eventOld.setDescription(insertEvent.getDescription());
        eventOld.setLocation(insertEvent.getLocation());
        eventOld.setSite(insertEvent.getSite());
        eventOld.setStartDate(startCalendar);
        eventOld.setEndDate(endCalendar);
        eventOld.setVisibility(insertEvent.getVisibility());

        if (insertEvent.getEventParticipants() != null) {
            List<User> userParticiants = new ArrayList<>();
            for (UserDTO insertUserParticipants : insertEvent.getEventParticipants()) {
                User user = em.find(User.class, Long.valueOf(insertUserParticipants.getId()));
                if (user != null) {
                    userParticiants.add(user);
                }
            }
            eventOld.setEventParticipants(userParticiants);
        }
        if (insertEvent.getInvitedUsers() != null) {
            List<User> userInvited = new ArrayList<>();
            for (UserDTO insertUserInvited : insertEvent.getInvitedUsers()) {
                User user = em.find(User.class, Long.valueOf(insertUserInvited.getId()));
                if (user != null) {

                    userInvited.add(user);
                }
            }
            eventOld.setInvitedUsers(userInvited);
        }

        if (insertEvent.getLocation() != null) {
            ForecastDTO forecast = handleForecast.getForecast(insertEvent.getLocation(), insertEvent.getStartDate());
            if (forecast != null) {
                eventOld.setForecast(em.find(Forecast.class, forecast.getId()));
            } else {
                eventOld.setForecast(null);
            }
        }

        return eventOld;
    }

    @Override
    public long updateEvent(Long userId, EventDTO insertEvent) throws ErrorRequestException {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new ErrorRequestException("no user", false);

        }

        Event event = em.find(Event.class, Long.valueOf(insertEvent.getId()));
        if (event != null) {
            // MODIFY EVENT
            event = modifyEvent(event, insertEvent);
            em.merge(event);
            return event.getId();
        } else {
            throw new ErrorRequestException("no event", false);
        }
    }

    @Override
    public void removeEvent(Long userId, EventDTO event) throws ErrorRequestException {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new ErrorRequestException("no user", false);
        }
        Event eventDb = em.find(Event.class, Long.valueOf(event.getId()));
        if (eventDb != null) {
            // CHECK EO = EVENT.EO
            if (eventDb.getEo().getId().equals(userId)) {
                // REMOVE EVENT FROM EO LIST
                user.getCalendar().getOrganizedEvents().remove(eventDb);

                // REMOVE EVENT FROM EP LIST
                for (User ep : eventDb.getEventParticipants()) {
                    ep.getCalendar().getParticipatedEvents().remove(eventDb);
                    em.merge(ep);
                }

                // REMOVE NOTIFICATION RELATED TO EVENT
                TypedQuery<EventNotification> queryNotify = em.createNamedQuery(EventNotification.FIND_BY_EVENT,
                        EventNotification.class);
                queryNotify.setParameter("event", eventDb);
                for (EventNotification notif : queryNotify.getResultList()) {
                    notif.getUser().getListNotifications().remove(notif);
                    em.remove(notif);
                }

                em.merge(user);
                // DELETE EVENT
                em.remove(eventDb);
                em.flush();
            } else {
                throw new ErrorRequestException("event eo != current user", false);
            }
        } else {
            throw new ErrorRequestException("no event", false);
        }
    }

    @Override
    public void cancelEvent(Long userId, EventDTO event) throws ErrorRequestException {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new ErrorRequestException("no user", false);
        }

        Event eventDb = em.find(Event.class, Long.valueOf(event.getId()));
        if (eventDb != null) {
            // CHECK EO = EVENT.EO
            if (eventDb.getEventParticipants().contains(user)) {
                // DELETE USER FROM EVENT PARTICIPANTS
                eventDb.getEventParticipants().remove(user);
                em.merge(eventDb);
                // REMOVE EVENT FROM USER CALENDAR
                user.getCalendar().getParticipatedEvents().remove(eventDb);
                em.merge(user);
                em.flush();
            } else {
                throw new ErrorRequestException("user not in the event participants", false);
            }
        } else {
            throw new ErrorRequestException("no event", false);
        }

    }

    @Override
    public List<ResultDTO> search(String queryString) {
        TypedQuery<Event> query = em.createNamedQuery(Event.FIND_BY_SEARCHQUERY,
                Event.class);
        query.setParameter("query", queryString + "%");
        User user = em.find(User.class, AuthUtil.getUserID());
        List<Event> resultEvents = new ArrayList<>();
        List<ResultDTO> results = new ArrayList<>();
        if (!query.getResultList().isEmpty()) {
            resultEvents.addAll(query.getResultList());
        }
        if (user != null) {
            Set<Event> fooSet = new LinkedHashSet<>(resultEvents);
            for (Event event : user.getCalendar().getOrganizedEvents()) {
                if (event.getName().contains(queryString)) {
                    fooSet.add(event);
                }
            }
            for (Event event : user.getCalendar().getParticipatedEvents()) {
                if (event.getName().contains(queryString)) {
                    fooSet.add(event);
                }
            }
            resultEvents = new ArrayList<>(fooSet);
        }

        for (Event event : resultEvents) {
            ResultDTO result = new ResultDTO();
            result.setId(event.getId().toString());
            result.setType("EVENT");
            result.setName(event.getName());
            LOGGER.log(Level.INFO, event.toString());
            results.add(result);

        }

        return results;
    }

    @Override
    public void moveEvent(String id, int dayDelta, int minuteDelta) throws ErrorRequestException {
        Event event = em.find(Event.class, Long.valueOf(id));
        if (event != null) {
            java.util.Calendar startDate = event.getStartDate();

            startDate.add(java.util.Calendar.DAY_OF_MONTH, dayDelta);
            startDate.add(java.util.Calendar.MINUTE, minuteDelta);

            java.util.Calendar endDate = event.getEndDate();

            endDate.add(java.util.Calendar.DAY_OF_MONTH, dayDelta);
            if (minuteDelta != 0) {
                endDate.add(java.util.Calendar.MINUTE, minuteDelta + 120);
            }

            event.setStartDate(startDate);
            event.setEndDate(endDate);
            LOGGER.log(Level.INFO, event.toString());

            em.merge(event);
            em.flush();

        } else {
            throw new ErrorRequestException("no event", false);
        }
    }

    @Override
    public void resizeEvent(String id, int dayDelta, int minuteDelta) throws ErrorRequestException {
        Event event = em.find(Event.class, Long.valueOf(id));
        if (event != null) {
            java.util.Calendar endDate = event.getEndDate();

            endDate.add(java.util.Calendar.DAY_OF_MONTH, dayDelta);
            endDate.add(java.util.Calendar.MINUTE, minuteDelta);

            event.setEndDate(endDate);
            LOGGER.log(Level.INFO, event.toString());

            em.merge(event);
            em.flush();

        } else {
            throw new ErrorRequestException("no event", false);
        }
    }

    @Override
    public void addParticipant(String eventId, ResultDTO selectedResult) throws ErrorRequestException {
        if (eventId == null) {

            throw new ErrorRequestException("Event need to be created before adding Participants", false);
        }
        Event event = null;
        try {
            event = em.find(Event.class, Long.valueOf(eventId));
        } catch (NumberFormatException e) {
            throw new ErrorRequestException("Event ID not valid", false);
        }
        if (event == null) {
            throw new ErrorRequestException("Event doesn't exits", false);
        }

        User user = em.find(User.class, Long.valueOf(selectedResult.getId()));

        if (user == null) {
            throw new ErrorRequestException("User doesn't exits", false);
        }

        if (event.getEventParticipants().contains(user) || event.getInvitedUsers().contains(user)) {
            throw new ErrorRequestException("User already invited", false);
        }
        event.getInvitedUsers().add(user);

        LOGGER.log(Level.INFO, "User participant " + user.toString() + " added to event: " + event.toString());
        em.merge(event);
        em.flush();

    }

    @Override
    public void checkEventWeatherCondition(long userId) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.set(java.util.Calendar.MINUTE, 0);
        today.set(java.util.Calendar.SECOND, 0);
        today.set(java.util.Calendar.MILLISECOND, 1);
        java.util.Calendar threeday = java.util.Calendar.getInstance();
        threeday.set(java.util.Calendar.HOUR_OF_DAY, 23);
        threeday.set(java.util.Calendar.MINUTE, 59);
        threeday.set(java.util.Calendar.SECOND, 59);
        threeday.set(java.util.Calendar.MILLISECOND, 999);
        threeday.add(java.util.Calendar.DAY_OF_YEAR, 3);
        TypedQuery<Event> query = em.createNamedQuery(Event.FIND_NEAR_OUTDOOR,
                Event.class);
        query.setParameter("today", today);
        query.setParameter("threeday", threeday);
        for (Event event : query.getResultList()) {
            TypedQuery<Calendar> queryCalendar = em.createNamedQuery(Calendar.FIND_BY_ORGANIZEDEVENT,
                    Calendar.class);
            queryCalendar.setParameter("event", event);
            // UPDATE FORECAST
            LOGGER.log(Level.INFO, "CHECK EVENT WEATHER CONDITION DATE: " + event.getStartDate().getTime());
            ForecastDTO forecast = handleForecast.getForecast(event.getLocation(), event.getStartDate().getTime());
            if (forecast != null && !queryCalendar.getResultList().isEmpty()) {
                // RECOMENDER SYSTEM
                if (Integer.valueOf(forecast.getWeather().getWeatherConditionCode()) < 800 || Integer.valueOf(forecast.getWeather().getWeatherConditionCode()) > 804) {
                    // UPDATED FORECAST IS ALSO BAD

                    // NOTIFY TO EP IF 1 DAY BEFORE EVENT
                    java.util.Calendar daybefore = (java.util.Calendar) event.getStartDate().clone();
                    daybefore.add(java.util.Calendar.DAY_OF_YEAR, -1);
                    if (java.util.Calendar.getInstance().after(daybefore)) {
                        for (User ep : event.getEventParticipants()) {
                            if (ep.getCalendar().getParticipatedEvents().contains(event)) {
                                TypedQuery<EventNotification> q = em.createNamedQuery(EventNotification.FIND_BY_EVENT_AND_USER,
                                        EventNotification.class);
                                q.setParameter("event", event);
                                q.setParameter("user", ep);
                                if (q.getResultList().isEmpty()) {
                                    EventNotification notif = new EventNotification();
                                    notif.setEvent(event);
                                    notif.setUser(ep);
                                    notif.setMessage("Alert: For the event " + event.getName() + " is forecasted " + event.getForecast().getWeather().getDescription() + ". However do you want partecipate the event?");
                                    notif.setStatus(Status.PENDING);
                                    em.persist(notif);
                                }
                            }
                        }
                    }

                    // CHECK IF ALREADY NOTIFIED TO EO
                    TypedQuery<RescheduleNotification> q = em.createNamedQuery(RescheduleNotification.FIND_BY_EVENT,
                            RescheduleNotification.class);
                    q.setParameter("event", event);
                    if (q.getResultList().isEmpty()) {
                        // FIND SUGGESTED EVENT
                        Event suggestedEvent = checkBestAlternativeDay(event);
                        if (suggestedEvent != null) {
                            // SEND NOTIFICATION
                            RescheduleNotification notification = new RescheduleNotification();
                            notification.setEvent(event);
                            notification.setUser(event.getEo());
                            notification.setSuggestedEvent(suggestedEvent);
                            notification.setMessage("For the event: " + event.getName() + " is forecasted bad weather do you want to reschedule the event on: " + suggestedEvent.getStartDate().getTime() + " ?");
                            notification.setStatus(Status.PENDING);
                            suggestedEvent.setId(null);
                            em.persist(suggestedEvent);
                            em.persist(notification);
                            em.flush();
                        }
                    }
                } else {
                    // UPDATED FORECAST GOOD
                    event.setForecast(em.find(Forecast.class, forecast.getId()));
                    // REMOVE NOTIFICATION IF PRESENT
                    TypedQuery<RescheduleNotification> q = em.createNamedQuery(RescheduleNotification.FIND_BY_EVENT,
                            RescheduleNotification.class);
                    q.setParameter("event", event);
                    for (RescheduleNotification rnotif : q.getResultList()) {
                        User user = rnotif.getUser();
                        user.getListNotifications().remove(rnotif);
                        em.merge(user);
                        em.remove(rnotif);
                    }

                    em.merge(event);
                    em.flush();
                }
            }
        }

    }

    /**
     * Method that find an alternative event schedule for a given event with
     * good weather condition
     *
     * @param event the bad weather event to find a reschedule
     * @return the suggester rescheduled event or null if there isn't any
     */
    private Event checkBestAlternativeDay(Event event) {
        LOGGER.log(Level.INFO, "CHECK BEST ALTERNATIVE EVENT DATE: " + event.getStartDate().getTime());
        Event suggestedEvent = new Event(event.getEo(), event.getName(), event.getDescription(), event.getLocation(), event.getSite(), null, null, null, event.getVisibility(), event.getEventParticipants(), event.getInvitedUsers());
        // GET FORECASTS FOR THE LOCATION
        List<ForecastDTO> forecasts = handleForecast.getForecasts(event.getLocation());
        if (!forecasts.isEmpty()) {
            int i = 0;
            do {
                ForecastDTO forecast = forecasts.get(i);
                if (Integer.valueOf(forecast.getWeather().getWeatherConditionCode()) < 800 || Integer.valueOf(forecast.getWeather().getWeatherConditionCode()) > 804) {
                    forecasts.remove(forecast);
                } else {
                    i++;
                }
            } while (i < forecasts.size());
        }

        // RETURN IF NO GOOD WEATHER CONDTION FORECAST
        if (forecasts.isEmpty()) {
            return null;
        }
        // FIND LAST FORECAST DAY
        Date lastForecastDay = new Date();
        for (ForecastDTO forecast : forecasts) {
            if (forecast.getDate().getTime().after(lastForecastDay)) {
                lastForecastDay = forecast.getDate().getTime();
            }
        }
        // FIND USER OCCUPATION FROM TOMORROW TO LAST FORECAST DAY
        java.util.Calendar tomorrow = java.util.Calendar.getInstance();
        tomorrow.set(java.util.Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(java.util.Calendar.MINUTE, 0);
        tomorrow.set(java.util.Calendar.SECOND, 0);
        tomorrow.set(java.util.Calendar.MILLISECOND, 1);
        tomorrow.add(java.util.Calendar.DAY_OF_YEAR, 1);
        TypedQuery<Event> query = em.createNamedQuery(Event.FIND_USER_OCCUPATION_RESCHEDULE,
                Event.class);
        query.setParameter("tomorrow", tomorrow);
        query.setParameter("lastforecastday", lastForecastDay);
        // REMOVE FORECAST WITH THE SAME DATE OF USER OCCUPATION
        for (Event eventOccuping : query.getResultList()) {
            if (!forecasts.isEmpty()) {
                int i = 0;
                do {
                    ForecastDTO forecast = forecasts.get(i);
                    if (eventOccuping.getStartDate().getTimeInMillis() <= forecast.getDate().getTimeInMillis() && forecast.getDate().getTimeInMillis() <= eventOccuping.getEndDate().getTimeInMillis()) {
                        forecasts.remove(forecast);
                    } else {
                        i++;
                    }
                } while (i < forecasts.size());
            }
        }
        // RETURN IF THE USER HAVE NO SUITABLE FREE TIME
        if (forecasts.isEmpty()) {
            return null;
        }
        // SELECT THE NEAREST FORECAST TO THE ORIGINAL EVENT
        List<java.util.Calendar> forecastDates = new ArrayList<>();
        for (ForecastDTO forecast : forecasts) {
            forecastDates.add(forecast.getDate());
            LOGGER.log(Level.INFO, "Date Forecast Rimaste: " + forecast.getDate().getTime().toString());
        }
        java.util.Calendar nearestDate = getDateNearest(forecastDates, event.getStartDate());

        long forecastId = -1;
        for (ForecastDTO forecast : forecasts) {
            if (forecast.getDate().equals(nearestDate)) {
                forecastId = forecast.getId();
                LOGGER.log(Level.INFO, "ForecastID: " + forecastId);
            }
        }
        if (forecastId != -1) {
            // ADD FINAL FORECAST TO SUGGESTED EVENT AND SETUP DATE
            Forecast selectedForecast = em.find(Forecast.class, forecastId);
            suggestedEvent.setForecast(selectedForecast);
            suggestedEvent.setStartDate(nearestDate);
            suggestedEvent.setEndDate(nearestDate);
            return suggestedEvent;
        } else {
            return null;
        }
    }

    /**
     * Method that return the nearest date from a list to a target date
     *
     * @param dates
     * @param targetDate
     * @return nearest date to target date
     */
    private java.util.Calendar getDateNearest(List<java.util.Calendar> dates, java.util.Calendar targetDate) {
        LOGGER.log(Level.INFO, "targetDate: " + targetDate.getTime());
        java.util.Calendar nearestDate = new TreeSet<>(dates).floor(targetDate);
        if (nearestDate != null) {
            return nearestDate;
        } else {
            nearestDate = new TreeSet<>(dates).ceiling(targetDate);
            return nearestDate;
        }

    }

    /**
     * Method that map the ForecastDTO class to Forecast class
     *
     * @param forecastDTO the needed to map class
     * @return Forecast class
     */
    private Forecast mapForecastDTOToForecast(ForecastDTO forecastDTO) {
        Forecast forecast = new Forecast();
        if (forecastDTO != null) {
            // SETUP FORECAST
            forecast.setId(forecastDTO.getId());
            forecast.setCreationDate(forecastDTO.getCreationDate());
            forecast.setForecastDate(forecastDTO.getDate());
            forecast.setLatitude(forecastDTO.getLatitude());
            forecast.setLongitude(forecastDTO.getLongitude());
            forecast.setLocation(forecastDTO.getLocation());
            // SETUP WEATHER
            Weather weather = new Weather();
            weather.setId(weather.getId());
            weather.setDescription(forecastDTO.getWeather().getDescription());
            weather.setIcon(forecastDTO.getWeather().getIcon());
            weather.setTemperature(forecastDTO.getWeather().getTemperature());
            weather.setWeatherConditionCode(forecastDTO.getWeather().getWeatherConditionCode());
            forecast.setWeather(weather);

            return forecast;
        }
        return null;
    }

}
