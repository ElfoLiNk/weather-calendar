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
import it.polimi.meteocal.dto.EventNotificationDTO;
import it.polimi.meteocal.dto.NotificationDTO;
import it.polimi.meteocal.dto.RescheduleNotificationDTO;
import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.dto.SettingDTO;
import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.entities.Calendar;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.EventNotification;
import it.polimi.meteocal.entities.Notification;
import it.polimi.meteocal.entities.RescheduleNotification;
import it.polimi.meteocal.entities.Setting;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.exception.ErrorRequestException;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.PasswordHash;
import it.polimi.meteocal.util.Status;
import it.polimi.meteocal.util.Visibility;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class HandleUserImpl
 */
@Stateless
public class HandleUserImpl implements HandleUser {

    private static final Logger LOGGER = LogManager.getLogger(HandleUserImpl.class.getName());

    /**
     * Method that merge the inforation between the old user and the new user in
     * the DB
     *
     * @param em EntityManager because is a static method
     * @param newUser the new user to maintain in the DB
     * @param oldUser the old user to save informations
     */
    public static void mergeOldUserNewUser(EntityManager em, User newUser, User oldUser) {
        // MOVE/CHANGE EO EVENTS
        for (Event event : oldUser.getCalendar().getOrganizedEvents()) {
            event.setEo(newUser);
            em.merge(event);
            newUser.getCalendar().getOrganizedEvents().add(event);
        }
        // MOVE/CHANGE EP EVENTS
        for (Event event : oldUser.getCalendar().getParticipatedEvents()) {
            event.getEventParticipants().remove(oldUser);
            event.getEventParticipants().add(newUser);
            em.merge(event);
            newUser.getCalendar().getParticipatedEvents().add(event);
        }
        // MOVE SETTING
        //newUser.setSetting(oldUser.getSetting());

        em.merge(newUser);
        em.flush();
    }

    /**
     * Method that merge the user account information and return only one
     * checking if an information is not present needed to get from the old
     * account user.
     *
     * @param newUser the new user
     * @param oldUser the old user to be removed and retrieve the useful
     * information
     * @return newUser after the data merge
     */
    public static User mergeUserAccount(User newUser, User oldUser) {
        if (newUser.getGoogleId() == null) {
            newUser.setGoogleId(oldUser.getGoogleId());
            newUser.setGoogleToken(oldUser.getGoogleToken());
        }
        if (newUser.getTwitterId() == null) {
            newUser.setTwitterId(oldUser.getTwitterId());
            newUser.setTwitterToken(oldUser.getTwitterToken());
            newUser.setTwitterTokenSecret(oldUser.getTwitterTokenSecret());
        }
        if (newUser.getFacebookId() == null) {
            newUser.setFacebookId(oldUser.getFacebookId());
            newUser.setFacebookToken(oldUser.getFacebookToken());

        }
        return newUser;
    }

    @EJB
    HandleEvent handleEvent;

    @PersistenceContext
    EntityManager em;

    @Override
    public UserDTO getUser(long idUtenteCollegato)
            throws ErrorRequestException {
        User account = em.find(User.class, idUtenteCollegato);
        if (account != null) {
            UserDTO user = new UserDTO(String.valueOf(account.getId()), account.getFirstName(), account.getLastName(), account.getGender(), account.getDateBirth(), account.getEmail(), account.getPassword(), account.getAvatar());
            String calendarID = String.valueOf(account.getCalendar().getId());
            user.setCalendarId(calendarID);
            user.setSetting(new SettingDTO(account.getSetting().getDateFormat(), account.getSetting().getTimeFormat(), account.getSetting().getTimeZone()));
            user.setNotifications(getNotifications(account));
            user.setPreferedCalendarsIDs(getPreferedCalendarsID(account));
            LOGGER.log(Level.INFO, "Notification SIZE: " + account.getListNotifications().size());
            return user;
        } else {
            throw new ErrorRequestException("no user", false);
        }
    }

    /**
     * Method that return the list of the notification of the currentUser
     *
     * @param currentUser the user to retrive the notifications
     * @return the list of the user's notification
     */
    private List<NotificationDTO> getNotifications(User currentUser) {
        List<NotificationDTO> notifications = new ArrayList<>();
        TypedQuery<Notification> query = em.createNamedQuery(Notification.FIND_BY_USER,
                Notification.class);
        query.setParameter("user", currentUser);
        for (Notification notif : query.getResultList()) {
            RescheduleNotification reschNotif = em.find(RescheduleNotification.class, notif.getId());
            EventNotification eventNotif = em.find(EventNotification.class, notif.getId());
            if (reschNotif != null) {
                RescheduleNotificationDTO notification = new RescheduleNotificationDTO();
                notification.setId(reschNotif.getId().toString());
                notification.setUserId(reschNotif.getUser().getId().toString());
                notification.setMessage(reschNotif.getMessage());
                notification.setEventId(reschNotif.getEvent().getId().toString());
                notification.setSuggestedEventId(reschNotif.getSuggestedEvent().getId().toString());
                notification.setStatus(Status.valueOf(reschNotif.getStatus().name()));
                notifications.add(notification);
            } else if (eventNotif != null) {
                EventNotificationDTO notification = new EventNotificationDTO();
                notification.setId(eventNotif.getId().toString());
                notification.setUserId(eventNotif.getUser().getId().toString());
                notification.setMessage(eventNotif.getMessage());
                notification.setEventId(eventNotif.getEvent().getId().toString());
                notification.setStatus(Status.valueOf(eventNotif.getStatus().name()));
                notifications.add(notification);
            } else {
                NotificationDTO notification = new NotificationDTO();
                notification.setId(notif.getId().toString());
                notification.setUserId(notif.getUser().getId().toString());
                notification.setMessage(notif.getMessage());
                notifications.add(notification);

            }
        }
        return notifications;
    }

    @Override
    public long checkAccessCredential(String id, String password)
            throws ErrorRequestException {
        User utente = em.find(User.class, Long.valueOf(id));
        if (utente == null) {
            throw new ErrorRequestException("Credenziali non valide", false);
        }

        if (validatePasssword(password, utente.getPassword())) {
            return utente.getId();
        } else {
            throw new ErrorRequestException("Credenziali non valide", false);
        }
    }

    /**
     * Check the validity of user password
     *
     * @param password the password insert by the user to validate
     * @param hash the hash savend in the database for the needed account
     * @return true: if the password is correct; false: otherwise.
     */
    private boolean validatePasssword(String password, String hash) {
        try {
            return PasswordHash.validatePassword(password, hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.log(Level.FATAL, e, e);
        }
        return false;
    }

    @Override
    public void addUser(UserDTO newUser) throws ErrorRequestException {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_EMAIL,
                User.class);
        query.setParameter("email", newUser.getEmail());
        if (query.getResultList().isEmpty()) {
            // The user isn't in the system
            User user = new User();
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setEmail(newUser.getEmail());
            user.setDateBirth(newUser.getDateBirth());
            user.setGender(newUser.getGender());
            try {
                user.setPassword(PasswordHash.createHash(newUser.getPassword()));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                LOGGER.log(Level.FATAL, ex, ex);
            }
            Calendar calendar = new Calendar();
            user.setCalendar(calendar);
            Setting setting = new Setting();
            setting.setTimeZone(TimeZone.getDefault());
            user.setSetting(setting);

            em.persist(user);
            em.flush();
            em.refresh(user);
        } else {

            throw new ErrorRequestException("Email already used by a User", false);
        }
    }

    @Override
    public boolean doLogin(UserDTO loginUser) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_EMAIL,
                User.class);
        query.setParameter("email", loginUser.getEmail());
        if (!query.getResultList().isEmpty()) {
            boolean valid = validatePasssword(loginUser.getPassword(), query.getResultList().get(0).getPassword());
            if (valid) {
                // Make the session for the user
                AuthUtil.makeUserSession(query.getResultList().get(0).getId());
                return true;
            } else {
                LOGGER.log(Level.ERROR, "Not registered user with this email: " + loginUser.getEmail());
                return false;
            }
        }

        return false;

    }

    @Override
    public UserDTO getOwner(String calendarId) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_CALENDAR_ID,
                User.class);
        query.setParameter("calendarId", Long.valueOf(calendarId));

        if (!query.getResultList().isEmpty()) {
            try {
                return getUser(query.getResultList().get(0).getId());
            } catch (ErrorRequestException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
        }

        return null;
    }

    @Override
    public List<ResultDTO> search(String queryString) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_SEARCHQUERY,
                User.class);
        query.setParameter("query", queryString + "%");

        List<ResultDTO> results = new ArrayList<>();
        if (!query.getResultList().isEmpty()) {

            for (User user : query.getResultList()) {
                ResultDTO result = new ResultDTO();
                result.setId(user.getId().toString());
                result.setType("USER");
                result.setName(user.getFirstName() + " " + user.getLastName());
                LOGGER.log(Level.INFO, user.toString());
                results.add(result);

            }
        }

        return results;
    }

    @Override
    public void changeSettings(UserDTO loggedUser) throws ErrorRequestException {
        User user = em.find(User.class, Long.valueOf(loggedUser.getId()));
        if (user != null) {
            // CHECK EMAIL
            if (!user.getEmail().equals(loggedUser.getEmail())) {
                TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_EMAIL,
                        User.class);
                query.setParameter("email", loggedUser.getEmail());
                if (query.getResultList().isEmpty()) {
                    user.setEmail(loggedUser.getEmail());
                } else {
                    throw new ErrorRequestException("email already used", false);
                }
            }
            // Change User BIO
            user.setFirstName(loggedUser.getFirstName());
            user.setLastName(loggedUser.getLastName());
            user.setGender(loggedUser.getGender());
            user.setDateBirth(loggedUser.getDateBirth());
            try {
                user.setPassword(PasswordHash.createHash(loggedUser.getPassword()));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                LOGGER.log(Level.ERROR, ex);
            }
            user.setAvatar(loggedUser.getAvatar());

            // Change User Calendar Setting
            user.getSetting().setDateFormat(loggedUser.getSetting().getDateFormat());
            user.getSetting().setTimeFormat(loggedUser.getSetting().getTimeFormat());
            user.getSetting().setTimeZone(loggedUser.getSetting().getTimeZone());

            em.merge(user);
            em.flush();

        } else {
            throw new ErrorRequestException("no user", false);
        }

    }

    @Override
    public Visibility getCalendarVisibility(String calendarId) throws ErrorRequestException {
        if (calendarId != null) {
            Calendar calendar = em.find(Calendar.class, Long.valueOf(calendarId));
            if (calendar != null) {
                return calendar.getVisibility();
            } else {
                throw new ErrorRequestException("no valid calendar id", false);
            }
        } else {
            throw new ErrorRequestException("no valid calendar id", false);
        }

    }

    @Override
    public void changeCalendarVisibility(Visibility visibility) throws ErrorRequestException {
        User user = em.find(User.class, AuthUtil.getUserID());

        if (user != null) {
            user.getCalendar().setVisibility(visibility);
            em.merge(user);
            em.flush();
        } else {
            throw new ErrorRequestException("no user", false);
        }
    }

    @Override
    public boolean addNotification(EventNotificationDTO notification) {

        User user = em.find(User.class, Long.valueOf(notification.getUserId()));

        if (user != null) {
            Event event = em.find(Event.class, Long.valueOf(notification.getEventId()));
            if (event != null) {
                TypedQuery<EventNotification> query = em.createNamedQuery(EventNotification.FIND_BY_EVENT,
                        EventNotification.class);
                query.setParameter("event", event);
                if (query.getResultList().isEmpty()) {
                    EventNotification notif = new EventNotification();
                    notif.setUser(user);
                    notif.setStatus(notification.getStatus());
                    notif.setMessage(notification.getMessage());
                    notif.setEvent(event);
                    user.getListNotifications().add(notif);
                    em.persist(notif);
                    em.merge(user);
                    em.flush();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ResultDTO> searchUser(String queryString) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_SEARCH, User.class);
        query.setParameter("query", queryString + "%");

        List<ResultDTO> results = new ArrayList<>();
        if (!query.getResultList().isEmpty()) {

            for (User user : query.getResultList()) {
                if (!user.getId().equals(AuthUtil.getUserID())) {

                    ResultDTO result = new ResultDTO();
                    result.setId(user.getId().toString());
                    result.setType("USER");
                    result.setName(user.getFirstName() + " " + user.getLastName());
                    LOGGER.log(Level.INFO, user.toString());
                    results.add(result);
                }

            }
        }

        return results;
    }

    @Override
    public void acceptNotification(NotificationDTO selectedNotification) {
        EventNotification eventNotification = em.find(EventNotification.class, Long.valueOf(selectedNotification.getId()));

        // EVENT NOTIFICATION
        if (eventNotification != null && !eventNotification.getStatus().equals(Status.ACCEPTED)) {
            eventNotification.setStatus(Status.ACCEPTED);

            // ADD USER TO EVENT PARTICIPANS
            User user = eventNotification.getUser();
            Event event = eventNotification.getEvent();
            if (user != null && event != null && !event.getEventParticipants().contains(user)) {
                event.getInvitedUsers().remove(user);
                event.getEventParticipants().add(user);
                em.merge(event);

                // ADD EVENT TO PARTIPATED EVENTS
                user.getCalendar().getParticipatedEvents().add(event);
                // REMOVE NOTIFICATION FROM USER
                user.getListNotifications().remove(eventNotification);
                em.merge(user);
                em.merge(eventNotification);
                em.remove(eventNotification);
                em.flush();

                LOGGER.log(Level.INFO, "ACCEPT PARTICEPITATION TO EVENT: " + eventNotification.getEvent().getId());
                LOGGER.log(Level.INFO, "CHECK REMOVED INVITED " + event.getInvitedUsers().size());
            } else {
                // ALERT MESSAGE
                eventNotification.setStatus(Status.ACCEPTED);
                em.merge(eventNotification);
                em.flush();
            }
        }

        // RESCHEDULE NOTIFICATION
        RescheduleNotification rescheduleNotification = em.find(RescheduleNotification.class, Long.valueOf(selectedNotification.getId()));
        if (rescheduleNotification != null && !rescheduleNotification.getStatus().equals(Status.ACCEPTED)) {
            RescheduleNotificationDTO reschNotif = (RescheduleNotificationDTO) selectedNotification;
            rescheduleNotification.setStatus(Status.ACCEPTED);

            User user = em.find(User.class, Long.valueOf(selectedNotification.getUserId()));
            if (user != null) {
                // REMOVE NOTIFICATION FROM USER
                user.getListNotifications().remove(rescheduleNotification);
                em.merge(user);
                em.merge(rescheduleNotification);
                em.remove(rescheduleNotification);
                em.flush();
                try {
                    EventDTO eventToRemove = handleEvent.getEvent(Long.valueOf(selectedNotification.getUserId()), reschNotif.getEventId());
                    if (eventToRemove != null) {
                        handleEvent.removeEvent(Long.valueOf(reschNotif.getUserId()), eventToRemove);
                    }
                    Event eventToAdd = em.find(Event.class, Long.valueOf(((RescheduleNotificationDTO) selectedNotification).getSuggestedEventId()));
                    if (eventToAdd != null) {
                        if (eventToAdd.getEo().equals(user)) {
                            // EO
                            user.getCalendar().addOrganizedEvent(eventToAdd);
                            // SEND RESCHEDULE NOTIFICATION TO EPs
                            sendRescheduleNotificationEP(eventToAdd);
                            // SET EPs to PENDING
                            eventToAdd.getInvitedUsers().addAll(eventToAdd.getEventParticipants());
                            eventToAdd.getEventParticipants().clear();
                        } else {
                            // EP
                            user.getCalendar().addParticipatedEvent(eventToAdd);
                            eventToAdd.getInvitedUsers().remove(user);
                            eventToAdd.getEventParticipants().add(user);
                        }
                        em.merge(eventToAdd);
                        em.merge(user);

                    }

                } catch (ErrorRequestException ex) {
                    LOGGER.log(Level.ERROR, ex);

                }

            }

        }
    }

    @Override
    public void declineNotification(NotificationDTO selectedNotification) {
        EventNotification notification = em.find(EventNotification.class, Long.valueOf(selectedNotification.getId()));

        // EVENT NOTIFICATION
        if (notification != null && !notification.getStatus().equals(Status.DECLINED)) {
            notification.setStatus(Status.DECLINED);

            // REMOVE USER FROM INVITED USER
            User user = notification.getUser();
            Event event = notification.getEvent();
            if (user != null && event != null && !event.getEventParticipants().contains(user)) {
                event.getInvitedUsers().remove(user);
                em.merge(event);

                // REMOVE NOTIFICATION FROM USER
                user.getListNotifications().remove(notification);
                em.merge(user);
                em.merge(notification);
                em.remove(notification);
                em.flush();
                LOGGER.log(Level.INFO, "DECLINED PARTICEPITATION TO EVENT: " + notification.getEvent().getId());
            } else if (user != null && event != null) {
                // USER DECLINE TO ALERT NOTIFICATION
                event.getEventParticipants().remove(user);
                user.getCalendar().getParticipatedEvents().remove(event);
                user.getListNotifications().remove(notification);
                em.merge(user);
                em.merge(notification);
                em.remove(notification);
                em.flush();
            }
        }

        // RESCHEDULE NOTIFICATION
        RescheduleNotification rescheduleNotification = em.find(RescheduleNotification.class, Long.valueOf(selectedNotification.getId()));
        if (rescheduleNotification != null && !rescheduleNotification.getStatus().equals(Status.ACCEPTED)) {
            RescheduleNotificationDTO reschNotif = (RescheduleNotificationDTO) selectedNotification;
            rescheduleNotification.setStatus(Status.ACCEPTED);

            User user = em.find(User.class, Long.valueOf(selectedNotification.getUserId()));
            if (user != null) {
                // REMOVE NOTIFICATION FROM USER
                user.getListNotifications().remove(rescheduleNotification);
                em.merge(user);
                em.merge(rescheduleNotification);
                em.remove(rescheduleNotification);
                em.flush();
                try {
                    EventDTO eventToRemove = handleEvent.getEvent(Long.valueOf(reschNotif.getUserId()), ((RescheduleNotificationDTO) selectedNotification).getSuggestedEventId());
                    if (eventToRemove != null) {
                        if (eventToRemove.getEoId().equals(String.valueOf(user.getId()))) {
                            // EO
                            handleEvent.removeEvent(Long.valueOf(reschNotif.getUserId()), eventToRemove);
                        } else {
                            // EP
                            Event event = em.find(Event.class, Long.valueOf(eventToRemove.getId()));
                            if (event != null) {
                                event.getInvitedUsers().remove(user);
                                event.getEventParticipants().remove(user);
                                em.merge(event);
                            }
                        }
                    }
                } catch (ErrorRequestException ex) {
                    LOGGER.log(Level.ERROR, ex);

                }

            }
        }
    }

    @Override
    public void addPreferedCalendar(String calendarId) {
        User user = em.find(User.class, AuthUtil.getUserID());
        if (user != null) {
            Calendar calendar = em.find(Calendar.class, Long.valueOf(calendarId));
            if (calendar != null) {
                if (user.getListPreferedCalendars() == null) {
                    user.setListPreferedCalendars(new ArrayList<>());
                }
                user.getListPreferedCalendars().add(calendar);
                em.merge(user);
                em.flush();
            }
        }
    }

    @Override
    public void delPreferedCalendar(String calendarId) {
        User user = em.find(User.class, AuthUtil.getUserID());
        if (user != null) {
            Calendar calendar = em.find(Calendar.class, Long.valueOf(calendarId));
            if (calendar != null) {
                if (user.getListPreferedCalendars() != null) {
                    user.getListPreferedCalendars().remove(calendar);
                }
                em.merge(user);
                em.flush();
            }
        }
    }

    /**
     * Method that return the list of the prefered calendar's ids of the user
     *
     * @param user the user to retrive the prefered calendars id
     * @return the list of prefered calendars id
     */
    private List<String> getPreferedCalendarsID(User user) {
        List<String> preferedCalendarsID = new ArrayList<>();
        for (Calendar calendar : user.getListPreferedCalendars()) {
            preferedCalendarsID.add(String.valueOf(calendar.getId()));
        }
        return preferedCalendarsID;
    }

    private void sendRescheduleNotificationEP(Event event) {
        if (event != null) {
            for (User ep : event.getEventParticipants()) {
                RescheduleNotification notif = new RescheduleNotification();
                notif.setEvent(event);
                notif.setStatus(Status.PENDING);
                notif.setUser(ep);
                notif.setMessage("The event " + event.getName() + " is reschedule by the EO for bad weather condition to " + event.getStartDate() + ". Do you confirm your partecipation? ");
                em.persist(notif);
                em.flush();

            }
        }
    }

    @Override
    public void removeOldNotification() {
//        TypedQuery<EventNotification> query = em.createNamedQuery(EventNotification.FIND_OLD_NOTIFICATION, EventNotification.class);
//        query.setParameter("now", LocalDateTime.now());
//        for (EventNotification eventNotif : query.getResultList()) {
//            eventNotif.getUser().getListNotifications().remove(eventNotif);
//            em.remove(eventNotif);
//        }
        TypedQuery<RescheduleNotification> queryResch = em.createNamedQuery(RescheduleNotification.FIND_OLD_NOTIFICATION, RescheduleNotification.class);
        queryResch.setParameter("now", LocalDateTime.now());
        for (RescheduleNotification reschNotif : queryResch.getResultList()) {
            Event suggestedEvent = em.find(Event.class, reschNotif.getSuggestedEvent().getId());
            if (suggestedEvent != null) {
                // REMOVE THE UNUSED SUGGESTED EVENT
                em.remove(suggestedEvent);
            }
            reschNotif.getUser().getListNotifications().remove(reschNotif);
            em.remove(reschNotif);
        }
    }

}
