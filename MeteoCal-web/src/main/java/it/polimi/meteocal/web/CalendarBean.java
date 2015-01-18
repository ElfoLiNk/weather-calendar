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

import it.polimi.meteocal.web.schedule.DefaultWeatherScheduleModel;
import it.polimi.meteocal.web.schedule.WeatherScheduleEvent;
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
import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Status;
import it.polimi.meteocal.util.Visibility;
import it.polimi.meteocal.web.schedule.WeatherScheduleModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
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

    public UserDTO getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(UserDTO loggedUser) {
        this.loggedUser = loggedUser;
    }

    public ResultDTO getSelectedResult() {
        return selectedResult;
    }

    public void setSelectedResult(ResultDTO selectedResult) {
        this.selectedResult = selectedResult;
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    public String getSelectedCalendarId() {
        return selectedCalendarId;
    }

    public void setSelectedCalendarId(String selectedCalendarId) {
        this.selectedCalendarId = selectedCalendarId;
    }

    public NotificationDTO getSelectedNotification() {
        return selectedNotification;
    }

    public void setSelectedNotification(NotificationDTO selectedNotification) {
        this.selectedNotification = selectedNotification;
    }

    public List<NotificationDTO> getNotifications() {

        return getCurrentUser().getNotifications();
    }

    public String getOwnerName(String calendarId) {
        UserDTO user = handleUser.getOwner(calendarId);
        return user.getFirstName() + " " + user.getLastName();

    }

    private void changeUser() {
        try {
            currentUser = handleUser.getUser(Long.valueOf(selectedResult.getId()));
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
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("userInfoForm");
        context.update("scheduleForm");

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
            redirect("index");
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
                redirect("http://www.meteocal.tk/MeteoCal-web/index.xhtml");
            }
        } else {
            LOGGER.log(Level.ERROR, "no user authUser == null");
            redirect("index");
            currentUser = new UserDTO();

        }
    }

    public WeatherScheduleEvent getEvent() {
        return event;
    }

    public WeatherScheduleModel getEventModel() {
        return eventModel;
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY,
                calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new WeatherScheduleEvent("", (Date) selectEvent.getObject(),
                (Date) selectEvent.getObject(), true);
        event.setEoId(currentUser.getId());

    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        if (loggedUser.getId().equals(eventModel.getEvent(event.getScheduleEvent().getId()).getEoId())) {
            try {
                handleEvent.moveEvent(event.getScheduleEvent().getId(), event.getDayDelta(), event.getMinuteDelta());
            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
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

    public void onEventSelect(SelectEvent selectEvent) {
        event = (WeatherScheduleEvent) selectEvent.getObject();
    }

    public void setEvent(WeatherScheduleEvent event) {
        this.event = event;
    }

    public void setEventModel(WeatherScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

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
            for (NotificationDTO notif : loggedUser.getNotifications()) {
                if (((EventNotificationDTO) notif).getEventId().equals(event.getId())) {
                    loggedUser.getNotifications().remove(notif);
                }
            }
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event removed", "Successfully removed " + event.getTitle()));
        }
        // RESET BEAN EVENT
        event = new WeatherScheduleEvent();
    }

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
            for (NotificationDTO notif : loggedUser.getNotifications()) {
                if (((EventNotificationDTO) notif).getEventId().equals(event.getId())) {
                    loggedUser.getNotifications().remove(notif);
                }
            }
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Event canceled", "Not partecipating to " + event.getTitle()));
        }
        // RESET BEAN EVENT
        event = new WeatherScheduleEvent();
    }

    public void addPreferedCalendar() {
        handleUser.addPreferedCalendar(currentUser.getCalendarId());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Calendar prefered", currentUser.getFirstName() + " calendar added to prefered");

        addMessage(message);
        loggedUser.getPreferedCalendarsIDs().add(currentUser.getCalendarId());
    }

    public void delPreferedCalendar() {
        handleUser.delPreferedCalendar(currentUser.getCalendarId());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Calendar prefered", currentUser.getFirstName() + " calendar removed from prefered");
        addMessage(message);
        loggedUser.getPreferedCalendarsIDs().remove(currentUser.getCalendarId());
    }

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

    public void redirect(String outcome) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
    }

    public void checkEventDate() {
        event.setAllDay(false);
        if (event.getStartDate().after(event.getEndDate())) {
            event.setEndDate(event.getStartDate());
        }

        if (event.getEndDate().before(event.getStartDate())) {
            event.setStartDate(event.getEndDate());
        }
    }

    public void addParticipant() {
        if (selectedResult != null && event.getId() != null) {
            try {
                UserDTO user = handleUser.getUser(Long.valueOf(selectedResult.getId()));
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
                String message = "This is an invite from " + handleUser.getUser(Long.valueOf(event.getEoId())).getFirstName() + " to " + event.getTitle() + " on " + event.getStartDate() + ". Do you want join the event?";
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

    public void acceptNotification() {
        LOGGER.log(Level.INFO, "Selected Notification: " + selectedNotification.toString());
        handleUser.acceptNotification(selectedNotification);
        loggedUser.getNotifications().remove(selectedNotification);
        LOGGER.log(Level.INFO, "Size Notifications: " + loggedUser.getNotifications().size());
        selectedNotification = null;
        init();

    }

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
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("scheduleForm");
        context.execute("PF('eventDialog').show();");

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

            }

        }
    }

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
}
