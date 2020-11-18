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
package it.polimi.meteocal.web;

import it.polimi.meteocal.auth.User;
import it.polimi.meteocal.dto.EventDTO;
import it.polimi.meteocal.dto.EventNotificationDTO;
import it.polimi.meteocal.dto.NotificationDTO;
import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.ejb.HandleEvent;
import it.polimi.meteocal.ejb.HandleForecast;
import it.polimi.meteocal.ejb.HandleUser;
import it.polimi.meteocal.exception.ErrorRequestException;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.DateFormat;
import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Status;
import it.polimi.meteocal.util.Visibility;
import it.polimi.meteocal.web.schedule.DefaultWeatherScheduleModel;
import it.polimi.meteocal.web.schedule.WeatherScheduleEvent;
import it.polimi.meteocal.web.schedule.WeatherScheduleModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;

/**
 * Class that manage the calendar (schedule), the events, the notifications of
 * the user on the web site
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named
@ViewScoped
public class CalendarBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(CalendarBean.class.getName());

    /**
     * Method that add days and minutes to the given date
     *
     * @param date the date to modify
     * @param days the days delta to add to the date
     * @param minutes the minutes delta to add to the date
     * @return the modified date
     */
    public static Date addDaysMinutes(Date date, int days, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    private WeatherScheduleEvent event = new WeatherScheduleEvent();

    private WeatherScheduleModel eventModel;

    @EJB
    HandleEvent handleEvent;

    @EJB
    HandleUser handleUser;

    @EJB
    HandleForecast handleForecast;

    private UserDTO currentUser;

    private UserDTO loggedUser;

    private NotificationDTO selectedNotification;

    private String selectedCalendarId;

    private ResultDTO selectedResult;

    /**
     *
     * @return the logged user
     */
    public UserDTO getLoggedUser() {
        return loggedUser;
    }

    /**
     *
     * @param loggedUser the logged user to set
     */
    public void setLoggedUser(UserDTO loggedUser) {
        this.loggedUser = loggedUser;
    }

    /**
     *
     * @return the selected result
     */
    public ResultDTO getSelectedResult() {
        return selectedResult;
    }

    /**
     *
     * @param selectedResult the selected result to set
     */
    public void setSelectedResult(ResultDTO selectedResult) {
        this.selectedResult = selectedResult;
    }

    /**
     *
     * @return the current user
     */
    public UserDTO getCurrentUser() {
        return currentUser;
    }

    /**
     *
     * @param currentUser the current user to set
     */
    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    /**
     *
     * @return the selected calendar id
     */
    public String getSelectedCalendarId() {
        return selectedCalendarId;
    }

    /**
     *
     * @param selectedCalendarId the calendar id to set
     */
    public void setSelectedCalendarId(String selectedCalendarId) {
        this.selectedCalendarId = selectedCalendarId;
    }

    /**
     *
     * @return the selected notification
     */
    public NotificationDTO getSelectedNotification() {
        return selectedNotification;
    }

    /**
     *
     * @param selectedNotification the selected notification to set
     */
    public void setSelectedNotification(NotificationDTO selectedNotification) {
        this.selectedNotification = selectedNotification;
    }

    /**
     *
     * @return the notifications of the current user
     */
    public List<NotificationDTO> getNotifications() {

        return getCurrentUser().getNotifications();
    }

    /**
     * Method that retrieve the user's calendar name
     *
     * @param calendarId the calendar id to set
     * @return the name of the owner of the calendar
     */
    public String getOwnerName(String calendarId) {
        UserDTO user = handleUser.getOwner(calendarId);
        return user.getFirstName() + " " + user.getLastName();

    }

    private void changeUser() {
        try {
            currentUser = handleUser.getUser(Long.parseLong(selectedResult.getId()));
        } catch (ErrorRequestException ex) {
            LOGGER.log(Level.WARN, ex);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error: ", ex.getMessage()));
        }

        loadEventsModel(Long.valueOf(currentUser.getId()));
        eventPrivacyCheck();

        // UPDATE CONTEXT
        PrimeFaces.Ajax ajax = PrimeFaces.current().ajax();
        ajax.update("userInfoForm");
        ajax.update("scheduleForm");

        selectedResult = null;

    }

    /**
     * Method that add or update the event in the system
     *
     */
    public void addEvent() {
        long idEventDb = 0;
        if (event.getId() == null) {
            event.setEoId(currentUser.getId());

            // NEW EVENT
            EventDTO evento = new EventDTO(null, event.getEoId(), event.getTitle(), event.getStartDate(), event.getEndDate(), true, Site.valueOf(event.getSite()), Visibility.valueOf(event.getVisibility()), event.getDescription(), event.getLocation(), event.getEventParticipants(), event.getInvitedUsers(), null);

            try {
                idEventDb = handleEvent.addEvent(AuthUtil.getUserID(), evento);
            } catch (ErrorRequestException e) {
                LOGGER.log(Level.ERROR, e);
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", e.getMessage()));
            }
            event.setId(String.valueOf(idEventDb));
            eventModel.addEvent(event);
            LOGGER.log(Level.INFO, "Evento aggiunto: " + evento.toString());
            loadEventsModel(AuthUtil.getUserID());
        } else {
            // UPDATE EVENT
            try {

                EventDTO evento = new EventDTO(event.getId(), event.getEoId(), event.getTitle(), event.getStartDate(), event.getEndDate(), event.isEditable(), Site.valueOf(event.getSite()), Visibility.valueOf(event.getVisibility()), event.getDescription(), event.getLocation(), event.getEventParticipants(), event.getInvitedUsers(), event.getWeather());
                idEventDb = handleEvent.updateEvent(AuthUtil.getUserID(), evento);

            } catch (ErrorRequestException e) {
                LOGGER.log(Level.ERROR, e);
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", e.getMessage()));
            }
            EventDTO updatedEvent = null;
            try {
                updatedEvent = handleEvent.getEvent(AuthUtil.getUserID(), String.valueOf(idEventDb));
            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
            if (updatedEvent != null) {
                eventModel.updateEvent(mapEventDTOtoWeatherScheduleEvent(updatedEvent));
            }
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Event", "Updated Successfully"));
        }
        // RESET BEAN EVENT
        event = new WeatherScheduleEvent();
    }

    /**
     * PostConstruct that initialize the class
     */
    @PostConstruct
    public void init() {
        handleUser.removeOldNotification();
        handleForecast.removeOldForecast();
        handleEvent.checkEventWeatherCondition(AuthUtil.getUserID());
        loadEventsModel(AuthUtil.getUserID());
        //handleForecast.setLocations();

        // LOAD LOGGED USER 
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context
                .getExternalContext().getRequest();
        HttpSession session = request.getSession();

        User authUtente = null;
        if (session != null) {
            authUtente = (User) session.getAttribute(User.AUTH_KEY);
        } else {
            LOGGER.log(Level.ERROR, "no active user session");
            redirect("../index.xhtml");
            currentUser = new UserDTO();
        }

        if (authUtente != null) {
            try {
                LOGGER.log(Level.INFO, handleUser.getUser(
                        authUtente.getUserID()).toString());
                currentUser = handleUser.getUser(
                        authUtente.getUserID());
                loggedUser = currentUser;
                LOGGER.log(Level.INFO, "NotificationDTO SIZE: " + currentUser.getNotifications().size());

            } catch (ErrorRequestException e) {
                LOGGER.log(Level.ERROR, e);
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, e);
                redirect("../index.xhtml");
            }
        } else {
            LOGGER.log(Level.ERROR, "no user authUser == null");
            redirect("../index.xhtml");
            currentUser = new UserDTO();

        }
    }

    /**
     *
     * @return the event of the calendar
     */
    public WeatherScheduleEvent getEvent() {
        return event;
    }

    /**
     *
     * @return the event model of the calendar
     */
    public WeatherScheduleModel getEventModel() {
        return eventModel;
    }

    /**
     *
     * @return the initial date
     */
    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY,
                calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    /**
     * Initialize the event on SelectEvent
     *
     * @param selectEvent the event on the calendar
     */
    public void onDateSelect(SelectEvent selectEvent) {
        event = new WeatherScheduleEvent("", (Date) selectEvent.getObject(),
                (Date) selectEvent.getObject(), true);
        event.setEoId(currentUser.getId());

    }

    /**
     * Method that move the event based on the deltas
     *
     * @param event the event to move with deltas
     */
    public void onEventMove(ScheduleEntryMoveEvent event) {
        if (loggedUser.getId().equals(eventModel.getEvent(event.getScheduleEvent().getId()).getEoId())) {
            try {
                handleEvent.moveEvent(event.getScheduleEvent().getId(), event.getDayDelta(), event.getMinuteDelta());
            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
            // UPDATE VIEW EVENT
            EventDTO updatedEvent = null;
            try {
                updatedEvent = handleEvent.getEvent(AuthUtil.getUserID(), event.getScheduleEvent().getId());
            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
            if (updatedEvent != null) {
                eventModel.updateEvent(mapEventDTOtoWeatherScheduleEvent(updatedEvent));
            }
            // RESET BEAN EVENT
            this.event = new WeatherScheduleEvent();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event moved", "Day delta:" + event.getDayDelta()
                    + ", Minute delta:" + event.getMinuteDelta());
            addMessage(message);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "EVENT NOT MOVED", "Yuo are not the owner of the event");
            addMessage(message);
        }
    }

    /**
     * Method that resize the event based on the deltas
     *
     * @param event the event to resize with deltas
     */
    public void onEventResize(ScheduleEntryResizeEvent event) {
        if (loggedUser.getId().equals(eventModel.getEvent(event.getScheduleEvent().getId()).getEoId())) {
            try {
                handleEvent.resizeEvent(event.getScheduleEvent().getId(), event.getDayDelta(), event.getMinuteDelta());
            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event resized", "Day delta:" + event.getDayDelta()
                    + ", Minute delta:" + event.getMinuteDelta());
            addMessage(message);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "EVENT NOT RESIZED", "Yuo are not the owner of the event");
            addMessage(message);
        }

    }

    /**
     * Method that load the selected event in the calendar by the user
     *
     * @param selectEvent the selected event
     */
    public void onEventSelect(SelectEvent selectEvent) {
        event = (WeatherScheduleEvent) selectEvent.getObject();
    }

    /**
     *
     * @param event the event to set
     */
    public void setEvent(WeatherScheduleEvent event) {
        this.event = event;
    }

    /**
     *
     * @param eventModel the event model to set
     */
    public void setEventModel(WeatherScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Method that remove the event from the model and from the system and send
     * visual notification to the user
     */
    public void removeEvent() {
        if (event.getId() == null) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error: ", "Not Valid Event"));
            LOGGER.log(Level.ERROR, "Evento non rimosso: " + event.toString());
        } else {
            // REMOVE EVENT
            try {
                EventDTO evento = new EventDTO(event.getId(), event.getEoId(), event.getTitle(), event.getStartDate(), event.getEndDate(), event.isEditable(), Site.valueOf(event.getSite()), Visibility.valueOf(event.getVisibility()), event.getDescription(), event.getLocation(), event.getEventParticipants(), event.getInvitedUsers(), event.getWeather());
                handleEvent.removeEvent(AuthUtil.getUserID(), evento);
            } catch (ErrorRequestException e) {
                LOGGER.log(Level.ERROR, e);
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", e.getMessage()));
            }
            eventModel.deleteEvent(event);
            // REMOVE NOTIFICATION RELATED TO EVENT
            loggedUser.getNotifications().removeIf(notif -> ((EventNotificationDTO) notif).getEventId().equals(event.getId()));
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event removed", "Successfully removed " + event.getTitle()));
        }
        // RESET BEAN EVENT
        event = new WeatherScheduleEvent();
    }

    /**
     * Method that cancel the user from the partecipated users and the event
     * from the model and send visual notification to the user
     */
    public void cancelEvent() {
        if (event.getId() == null) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error: ", "Evento Selezionato Non Valido"));
            LOGGER.log(Level.ERROR, "Evento non rimosso: " + event.toString());
        } else {
            // CANCEL EVENT
            try {
                EventDTO evento = new EventDTO(event.getId(), event.getEoId(), event.getTitle(), event.getStartDate(), event.getEndDate(), event.isEditable(), Site.valueOf(event.getSite()), Visibility.valueOf(event.getVisibility()), event.getDescription(), event.getLocation(), event.getEventParticipants(), event.getInvitedUsers(), event.getWeather());
                handleEvent.cancelEvent(AuthUtil.getUserID(), evento);
            } catch (ErrorRequestException e) {
                LOGGER.log(Level.ERROR, e);
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", e.getMessage()));
            }
            eventModel.deleteEvent(event);
            // REMOVE NOTIFICATION RELATED TO EVENT
            loggedUser.getNotifications().removeIf(notif -> ((EventNotificationDTO) notif).getEventId().equals(event.getId()));
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event canceled", "Not partecipating to " + event.getTitle()));
        }
        // RESET BEAN EVENT
        event = new WeatherScheduleEvent();
    }

    /**
     * Method that add the current calendar to the preferred
     */
    public void addPreferedCalendar() {
        handleUser.addPreferedCalendar(currentUser.getCalendarId());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Calendar prefered", currentUser.getFirstName() + " calendar added to prefered");

        addMessage(message);
        loggedUser.getPreferedCalendarsIDs().add(currentUser.getCalendarId());
    }

    /**
     * Method that remove the current calendar from the preferred
     */
    public void delPreferedCalendar() {
        handleUser.delPreferedCalendar(currentUser.getCalendarId());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Calendar prefered", currentUser.getFirstName() + " calendar removed from prefered");
        addMessage(message);
        loggedUser.getPreferedCalendarsIDs().remove(currentUser.getCalendarId());
    }

    /**
     * Method that handle the user selection in the search bar
     *
     * @param event the select event of the user
     */
    public void handleSelect(SelectEvent event) {
        // SELECT SEARCH BAR
        selectedResult = (ResultDTO) event.getObject();
        LOGGER.log(Level.INFO, "Selected Result: " + selectedResult.toString());
        LOGGER.log(Level.INFO, "UIcomponent: " + event.getComponent().getId());

        if ("searchBar".equals(event.getComponent().getId())) {
            switch (selectedResult.getType()) {
                case "USER":
                    changeUser();
                    break;
                case "EVENT":
                    showPublicEvent();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Redirect Method
     *
     * @param outcome the outcome to reach
     */
    public void redirect(String outcome) {
         try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(outcome);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Logout Error", "Redirect Failed"));

        }
    }

    /**
     * Method that handle the consistency of the event date
     */
    public void checkEventDate() {
        event.setAllDay(false);
        if (event.getStartDate().after(event.getEndDate())) {
            event.setEndDate(event.getStartDate());
        }

        if (event.getEndDate().before(event.getStartDate())) {
            event.setStartDate(event.getEndDate());
        }
    }

    /**
     * Method that add the user to the invited and send an event notification
     */
    public void addParticipant() {
        if (selectedResult != null && event.getId() != null) {
            try {
                UserDTO user = handleUser.getUser(Long.parseLong(selectedResult.getId()));
                // CHECK IF ALREADY ADDED
                boolean alreadyAdded = true;
                if (!event.getListParticipantAndInvitedUsers().isEmpty()) {
                    for (UserDTO userList : event.getListParticipantAndInvitedUsers()) {
                        if (userList.equals(user)) {
                            addMessage(
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                            "Error: ", "User Already Invited"));
                            alreadyAdded = false;
                        }
                    }
                }
                if (alreadyAdded) {
                    if (event.getInvitedUsers() == null) {
                        event.setInvitedUsers(new ArrayList<>());
                    }
                    event.getInvitedUsers().add(user);
                    if (event.getListParticipantAndInvitedUsers() == null) {
                        event.setListParticipantAndInvitedUsers(new ArrayList<>());
                    }
                    event.getListParticipantAndInvitedUsers().add(user);

                    // ADD PARTICIPANT
                    handleEvent.addParticipant(event.getId(), selectedResult);

                }

                // ADD NOTIFICATION TO PARTICIPANT
                String message = "This is an invite from " + handleUser.getUser(Long.parseLong(event.getEoId())).getFirstName() + " to " + event.getTitle() + " on " + event.getStartDate() + ". Do you want join the event?";
                if (handleUser.addNotification(new EventNotificationDTO(null, event.getId(), Status.PENDING, selectedResult.getId(), message))) {

                    addMessage(
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                    "Event " + event.getTitle(), "Invitation send to " + selectedResult.getName()));
                }

            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
                addMessage(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", ex.getMessage()));
            }
        } else {
            if (event.getId() != null) {
                addMessage(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", "No Valid User"));

            } else {
                addMessage(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error: ", "No Event"));
            }
        }
        selectedResult = null;
    }

    /**
     * Method that accept the notification
     */
    public void acceptNotification() {
        LOGGER.log(Level.INFO, "Selected Notification: " + selectedNotification.toString());
        handleUser.acceptNotification(selectedNotification);
        loggedUser.getNotifications().remove(selectedNotification);
        LOGGER.log(Level.INFO, "Size Notifications: " + loggedUser.getNotifications().size());
        selectedNotification = null;
        init();

    }

    /**
     * Method that decline the notification
     */
    public void declineNotification() {
        LOGGER.log(Level.INFO, "Selected Notification: " + selectedNotification.toString());
        handleUser.declineNotification(selectedNotification);
        loggedUser.getNotifications().remove(selectedNotification);
        LOGGER.log(Level.INFO, "Size Notifications: " + loggedUser.getNotifications().size());
        selectedNotification = null;
    }

    private void showPublicEvent() {
        try {
            EventDTO selectedEvent = handleEvent.getEvent(AuthUtil.getUserID(), selectedResult.getId());
            boolean allDay = selectedEvent.getStartDate().equals(selectedEvent.getEndDate());
            List<UserDTO> listParticipantAndInvitedUser = new ArrayList<>();
            listParticipantAndInvitedUser.addAll(selectedEvent.getEventParticipants());
            listParticipantAndInvitedUser.addAll(selectedEvent.getInvitedUsers());

            event = new WeatherScheduleEvent(selectedEvent.getId(), selectedEvent.getTitle(), selectedEvent.getDescription(), selectedEvent.getStartDate(), selectedEvent.getEndDate(), allDay, selectedEvent.getLocation(), selectedEvent.getSite(), selectedEvent.getVisibility(), selectedEvent.getEoId(), listParticipantAndInvitedUser, selectedEvent.getEventParticipants(), selectedEvent.getInvitedUsers(), selectedEvent.getWeather());
            LOGGER.log(Level.INFO, "Show Public Event: " + event.toString());
        } catch (ErrorRequestException ex) {
            LOGGER.log(Level.ERROR, ex);
            addMessage(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error: ", ex.getMessage()));
        }

        // UPDATE CONTEXT
        PrimeFaces pf = PrimeFaces.current();
        pf.ajax().update("scheduleForm");
        pf.executeScript("PF('eventDialog').show();");

        selectedResult = null;
    }

    private void loadEventsModel(Long userID) {
        // LOAD EVENTS MODEL
        eventModel = new DefaultWeatherScheduleModel();

        List<EventDTO> eventi = new ArrayList<>();
        try {
            eventi = handleEvent.getEvents(userID);
        } catch (ErrorRequestException e) {
            LOGGER.log(Level.ERROR, e);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error: ", e.getMessage()));
        }
        for (EventDTO evento : eventi) {
            WeatherScheduleEvent weatherEvent = mapEventDTOtoWeatherScheduleEvent(evento);
            eventModel.addEvent(weatherEvent);
            LOGGER.log(Level.INFO, "LOAD EVENT EVENT: " + weatherEvent.toString());
        }

        LOGGER.log(Level.INFO, "SIZE EVENTI: " + eventi.size());
    }

    private void eventPrivacyCheck() {
        for (WeatherScheduleEvent eventWeather : eventModel.getWeatherEvents()) {
            eventWeather.setEditable(false);
            if (eventWeather.getVisibility().equals(Visibility.PRIVATE.name())) {
                eventWeather.setDescription("");
                eventWeather.setTitle(currentUser.getFirstName() + " " + currentUser.getLastName() + " Private Event");
                eventWeather.getEventParticipants().clear();
                eventWeather.getInvitedUsers().clear();
                eventWeather.getListParticipantAndInvitedUsers().clear();
                eventWeather.setLocation("");
                eventWeather.setWeather(null);

            }

        }
    }

    /**
     * Method that handle the link to shows the user his prefered calendar
     *
     * @param preferedCalendarId the id of the preferred calendar
     */
    public void handlePreferedCalendar(String preferedCalendarId) {
        selectedCalendarId = preferedCalendarId;
        selectedResult = new ResultDTO();
        selectedResult.setId(handleUser.getOwner(selectedCalendarId).getId());
        selectedResult.setType("USER");
        changeUser();
    }

    private WeatherScheduleEvent mapEventDTOtoWeatherScheduleEvent(EventDTO evento) {
        boolean allDay = evento.getStartDate().equals(evento.getEndDate());
        List<UserDTO> listParticipantAndInvitedUser = new ArrayList<>();
        listParticipantAndInvitedUser.addAll(evento.getEventParticipants());
        listParticipantAndInvitedUser.addAll(evento.getInvitedUsers());
        WeatherScheduleEvent weatherEvent = new WeatherScheduleEvent(evento.getId(), evento.getTitle(), evento.getDescription(), evento.getStartDate(), evento.getEndDate(), allDay, evento.getLocation(), evento.getSite(), evento.getVisibility(), evento.getEoId(), listParticipantAndInvitedUser, evento.getEventParticipants(), evento.getInvitedUsers(), evento.getWeather());
        weatherEvent.setEditable(evento.isEditable());
        return weatherEvent;
    }

    /**
     * Method that return the correct columnformat setting for the primefaces
     * schedule component based on the user settings
     *
     * @return primefaces columnformat setting
     */
    public String getColumnFormat() {
        if (loggedUser != null) {
            if (loggedUser.getSetting().getDateFormat().name().equals(DateFormat.DMY.name())) {
                return "month: 'ddd',week: 'dddd dd/MM',day: 'dddd d/M'";
            } else {
                return "month: 'ddd',week: 'dddd MM/dd',day: 'dddd d/M'";
            }
        }
        return "month: 'ddd',week: 'dddd dd/MM',day: 'dddd d/M'";
    }
}
