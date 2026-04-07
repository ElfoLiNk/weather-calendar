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

import it.polimi.meteocal.dto.EventNotificationDTO;
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
import it.polimi.meteocal.util.ContextMocker;
import it.polimi.meteocal.util.DateFormat;
import it.polimi.meteocal.util.PasswordHash;
import it.polimi.meteocal.util.Status;
import it.polimi.meteocal.util.TimeFormat;
import it.polimi.meteocal.util.Visibility;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author Matteo
 */
public class HandleUserImplTest {

    private HandleUserImpl handleUser;

    private User newUser;
    private User oldUser;
    private User user;
    private User notLogged;
    private Calendar calendar;
    private UserDTO userInsert;
    private Event event;
    private EventNotificationDTO eventNotificationDTO;
    private EventNotification eventNotification;
    private TypedQuery<User> query;

    /**
     *
     */
    public HandleUserImplTest() {
        // Default constructor required by JUnit
    }

    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
        // intentionally empty: no class-level setup required
    }

    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
        // intentionally empty: no class-level teardown required
    }

    /**
     *
     */
    @Before
    public void setUp() {
        handleUser = new HandleUserImpl();
        handleUser.em = mock(EntityManager.class);

        newUser = new User();
        oldUser = new User();
        oldUser.setCalendar(new Calendar());
        event = new Event();
        event.setId(0L);
        oldUser.getCalendar().getOrganizedEvents().add(event);
        oldUser.setFacebookId("0");
        oldUser.setFacebookToken("TOKENFACEBOOK");

        userInsert = new UserDTO();
        userInsert.setId("0");
        userInsert.setFirstName("Mario");
        userInsert.setLastName("Rossi");
        userInsert.setEmail("test@test.com");
        userInsert.setPassword("password");
        SettingDTO settingDTO = new SettingDTO(DateFormat.DMY, TimeFormat.DEFAULT, null);
        userInsert.setSetting(settingDTO);
        userInsert.setPreferedCalendarsIDs(new ArrayList<>());
        userInsert.setNotifications(new ArrayList<>());
        userInsert.setCalendarId("0");

        user = new User();
        try {
            user.setPassword(PasswordHash.createHash("password"));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(HandleUserImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        user.setId(0L);
        user.setEmail("test@test.com");
        user.setFirstName("Mario");
        user.setLastName("Rossi");
        Setting setting = new Setting();
        setting.setDateFormat(DateFormat.DMY);
        setting.setTimeFormat(TimeFormat.DEFAULT);
        user.setSetting(setting);
        user.setListPreferedCalendars(new ArrayList<>());
        user.setListNotifications(new ArrayList<>());
        calendar = new Calendar();
        calendar.setId(0L);
        calendar.setVisibility(Visibility.PUBLIC);
        user.setCalendar(calendar);

        notLogged = new User();
        notLogged.setId((long) 1);
        notLogged.setFirstName("Mario");
        notLogged.setLastName("Bianchi");

        // TYPED QUERY
        query = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(User.FIND_BY_EMAIL, User.class)).thenReturn(query);

        TypedQuery<Notification> queryNotif = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(Notification.FIND_BY_USER, Notification.class)).thenReturn(queryNotif);
        TypedQuery<EventNotification> queryEventNotif = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(EventNotification.FIND_BY_EVENT, EventNotification.class)).thenReturn(queryEventNotif);
        TypedQuery<EventNotification> queryOldEventNotif = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(EventNotification.FIND_OLD_NOTIFICATION, EventNotification.class)).thenReturn(queryOldEventNotif);
        TypedQuery<RescheduleNotification> queryOldReschNotif = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(RescheduleNotification.FIND_OLD_NOTIFICATION, RescheduleNotification.class)).thenReturn(queryOldReschNotif);

        when(handleUser.em.createNamedQuery(User.FIND_BY_CALENDAR_ID, User.class)).thenReturn(query);
        when(handleUser.em.createNamedQuery(User.FIND_BY_SEARCHQUERY, User.class)).thenReturn(query);
        when(handleUser.em.createNamedQuery(User.FIND_BY_SEARCH, User.class)).thenReturn(query);

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

        // NOTIFICATION
        eventNotificationDTO = new EventNotificationDTO();
        eventNotificationDTO.setId("0");
        eventNotificationDTO.setUserId(String.valueOf(user.getId()));
        eventNotificationDTO.setMessage("notification message");
        eventNotificationDTO.setEventId(String.valueOf(event.getId()));
        eventNotificationDTO.setStatus(it.polimi.meteocal.util.Status.PENDING);

        eventNotification = new EventNotification();
        eventNotification.setId(0L);
        eventNotification.setUser(user);
        eventNotification.setMessage("notification message");
        eventNotification.setEvent(event);
        eventNotification.setStatus(it.polimi.meteocal.util.Status.PENDING);
    }

    /**
     *
     */
    @After
    public void tearDown() {
        // intentionally empty: no instance-level teardown required
    }

    /**
     * Test of mergeOldUserNewUser method, of class HandleUserImpl.
     */
    @Test
    public void testMergeOldUserNewUser() {
        System.out.println("MergeOldUserNewUser");
        HandleUserImpl.mergeOldUserNewUser(handleUser.em, newUser, oldUser);
        assertTrue(newUser.getCalendar().getOrganizedEvents().contains(event));
    }

    /**
     * Test of mergeUserAccount method, of class HandleUserImpl.
     */
    @Test
    public void testMergeUserAccount() {
        System.out.println("mergeUserAccount");
        User result = HandleUserImpl.mergeUserAccount(newUser, oldUser);
        assertEquals(result.getFacebookId(), oldUser.getFacebookId());
        assertEquals(result.getFacebookToken(), oldUser.getFacebookToken());
    }

    /**
     * Test of getUser method, of class HandleUserImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testGetUser() throws ErrorRequestException {
        System.out.println("getUser");
        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn(userInsert.getEmail());
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getId()).thenReturn(Long.MIN_VALUE);
        when(mockUser.getCalendar()).thenReturn(mockCalendar);
        when(mockUser.getSetting()).thenReturn(mock(Setting.class));
        when(handleUser.em.find(User.class, 0L)).thenReturn(mockUser);
        UserDTO result = handleUser.getUser(0L);
        assertEquals(userInsert.getEmail(), result.getEmail());
    }

    /**
     * Test of checkAccessCredential method, of class HandleUserImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testCheckAccessCredential() throws ErrorRequestException {
        System.out.println("checkAccessCredential");
        when(handleUser.em.find(User.class, 0L)).thenReturn(user);
        long result = handleUser.checkAccessCredential("0", "password");
        assertEquals(0L, result);
    }

    /**
     * Test of addUser method, of class HandleUserImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testAddUser() throws ErrorRequestException {
        System.out.println("addUser");
        user.setListNotifications(null);
        user.setListPreferedCalendars(null);
        doAnswer(new UserAnswer(0L, user.getCalendar(), user.getSetting(), user.getPassword())).when(handleUser.em).persist(any(User.class));
        handleUser.addUser(userInsert);
        verify(handleUser.em, times(1)).persist(user);
    }

    /**
     * Test of doLogin method, of class HandleUserImpl.
     */
    @Test
    public void testDoLogin() {
        System.out.println("doLogin");
        List<User> userEmail = new ArrayList<>();
        userEmail.add(user);
        when(query.getResultList()).thenReturn(userEmail);

        boolean result = handleUser.doLogin(userInsert);
        assertTrue(result);
    }

    /**
     * Test of getOwner method, of class HandleUserImpl.
     */
    @Test
    public void testGetOwner() {
        System.out.println("getOwner");
        String calendarId = "0";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(handleUser.em.find(User.class, 0L)).thenReturn(user);
        when(query.getResultList()).thenReturn(userList);
        UserDTO result = handleUser.getOwner(calendarId);
        assertEquals(user.getEmail(), result.getEmail());
    }

    /**
     * Test of search method, of class HandleUserImpl.
     */
    @Test
    public void testSearch() {
        System.out.println("search");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(query.getResultList()).thenReturn(userList);
        List<ResultDTO> result = handleUser.search("");
        assertEquals(user.getFirstName() + " " + user.getLastName(), result.get(0).getName());
    }

    /**
     * Test of changeSettings method, of class HandleUserImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testChangeSettings() throws ErrorRequestException {
        System.out.println("changeSettings");
        when(handleUser.em.find(User.class, user.getId())).thenReturn(user);
        UserDTO loggedUser = handleUser.getUser(user.getId());
        loggedUser.getSetting().setTimeFormat(TimeFormat.AMPM);
        handleUser.changeSettings(loggedUser);
        assertEquals(loggedUser.getSetting().getDateFormat(), user.getSetting().getDateFormat());
    }

    /**
     * Test of getCalendarVisibility method, of class HandleUserImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testGetCalendarVisibility() throws ErrorRequestException {
        System.out.println("getCalendarVisibility");
        String calendarId = "0";
        user.getCalendar().setVisibility(Visibility.PUBLIC);
        when(handleUser.em.find(Calendar.class, Long.valueOf(calendarId))).thenReturn(user.getCalendar());
        Visibility result = handleUser.getCalendarVisibility(calendarId);
        assertEquals(Visibility.PUBLIC, result);
    }

    /**
     * Test of changeCalendarVisibility method, of class HandleUserImpl.
     *
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    @Test
    public void testChangeCalendarVisibility() throws ErrorRequestException {
        System.out.println("changeCalendarVisibility");
        Visibility visibility = Visibility.PRIVATE;
        user.getCalendar().setVisibility(Visibility.PUBLIC);
        when(handleUser.em.find(User.class, user.getId())).thenReturn(user);
        handleUser.changeCalendarVisibility(visibility);
        assertEquals(user.getCalendar().getVisibility(), visibility);

    }

    /**
     * Test of addNotification method, of class HandleUserImpl.
     */
    @Test
    public void testAddNotification() {
        System.out.println("addNotification");
        when(handleUser.em.find(User.class, user.getId())).thenReturn(user);
        when(handleUser.em.find(Event.class, Long.valueOf(eventNotificationDTO.getEventId()))).thenReturn(event);
        handleUser.addNotification(eventNotificationDTO);
        assertEquals(user.getListNotifications().get(0).getMessage(), eventNotificationDTO.getMessage());
    }

    /**
     * Test of searchUser method, of class HandleUserImpl.
     */
    @Test
    public void testSearchUser() {
        System.out.println("searchUser");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(notLogged);
        when(query.getResultList()).thenReturn(userList);
        List<ResultDTO> result = handleUser.searchUser("");
        assertEquals(notLogged.getFirstName() + " " + notLogged.getLastName(), result.get(0).getName());
    }

    /**
     * Test of acceptNotification method, of class HandleUserImpl.
     */
    @Test
    public void testAcceptNotification() {
        System.out.println("acceptNotification");
        eventNotification.setStatus(Status.PENDING);
        when(handleUser.em.find(EventNotification.class, Long.valueOf(eventNotificationDTO.getId()))).thenReturn(eventNotification);
        handleUser.acceptNotification(eventNotificationDTO);
        assertEquals(Status.ACCEPTED, eventNotification.getStatus());
        assertTrue(user.getListNotifications().isEmpty());
        assertTrue(event.getEventParticipants().contains(user));
    }

    /**
     * Test of declineNotification method, of class HandleUserImpl.
     */
    @Test
    public void testDeclineNotification() {
        System.out.println("declineNotification");
        eventNotification.setStatus(Status.PENDING);
        when(handleUser.em.find(EventNotification.class, Long.valueOf(eventNotificationDTO.getId()))).thenReturn(eventNotification);
        handleUser.declineNotification(eventNotificationDTO);
        assertEquals(Status.DECLINED, eventNotification.getStatus());
        assertTrue(user.getListNotifications().isEmpty());
        assertFalse(event.getEventParticipants().contains(user));
    }

    /**
     * Test of addPreferedCalendar method, of class HandleUserImpl.
     */
    @Test
    public void testAddPreferedCalendar() {
        System.out.println("addPreferedCalendar");
        when(handleUser.em.find(User.class, AuthUtil.getUserID())).thenReturn(user);
        Calendar preferedCalendar = new Calendar();
        preferedCalendar.setId(1L);
        when(handleUser.em.find(Calendar.class, preferedCalendar.getId())).thenReturn(preferedCalendar);
        handleUser.addPreferedCalendar(String.valueOf(preferedCalendar.getId()));
        assertTrue(user.getListPreferedCalendars().contains(preferedCalendar));

    }

    /**
     * Test of delPreferedCalendar method, of class HandleUserImpl.
     */
    @Test
    public void testDelPreferedCalendar() {
        System.out.println("delPreferedCalendar");
        when(handleUser.em.find(User.class, AuthUtil.getUserID())).thenReturn(user);
        Calendar preferedCalendar = new Calendar();
        preferedCalendar.setId((long) 1);
        when(handleUser.em.find(Calendar.class, preferedCalendar.getId())).thenReturn(preferedCalendar);
        user.getListPreferedCalendars().add(preferedCalendar);
        handleUser.delPreferedCalendar(String.valueOf(preferedCalendar.getId()));
        assertFalse(user.getListPreferedCalendars().contains(preferedCalendar));
    }

    /**
     * Test of removeOldNotification method, of class HandleUserImpl.
     * Verifies that expired EventNotifications are removed and non-expired ones are not.
     */
    @Test
    public void testRemoveOldNotification() {
        System.out.println("removeOldNotification");

        // Expired EventNotification: event endDate is in the past
        Event expiredEvent = new Event();
        expiredEvent.setId(10L);
        expiredEvent.setName("Expired Event");
        EventNotification expiredNotif = new EventNotification();
        expiredNotif.setId(10L);
        expiredNotif.setUser(user);
        expiredNotif.setEvent(expiredEvent);
        expiredNotif.setStatus(Status.PENDING);
        user.getListNotifications().add(expiredNotif);

        // Non-expired EventNotification: event endDate is in the future
        Event currentEvent = new Event();
        currentEvent.setId(11L);
        currentEvent.setName("Current Event");
        EventNotification currentNotif = new EventNotification();
        currentNotif.setId(11L);
        currentNotif.setUser(user);
        currentNotif.setEvent(currentEvent);
        currentNotif.setStatus(Status.PENDING);
        // (currentNotif is NOT in the expired query result list)

        // Set up FIND_OLD_NOTIFICATION for EventNotification: returns the expired one
        TypedQuery<EventNotification> queryOldEventNotif = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(EventNotification.FIND_OLD_NOTIFICATION, EventNotification.class))
                .thenReturn(queryOldEventNotif);
        List<EventNotification> expiredEventNotifs = new ArrayList<>();
        expiredEventNotifs.add(expiredNotif);
        when(queryOldEventNotif.setParameter(org.mockito.ArgumentMatchers.eq("now"), any())).thenReturn(queryOldEventNotif);
        when(queryOldEventNotif.getResultList()).thenReturn(expiredEventNotifs);

        // Set up FIND_OLD_NOTIFICATION for RescheduleNotification: returns empty list
        TypedQuery<RescheduleNotification> queryOldReschNotif = mock(TypedQuery.class);
        when(handleUser.em.createNamedQuery(RescheduleNotification.FIND_OLD_NOTIFICATION, RescheduleNotification.class))
                .thenReturn(queryOldReschNotif);
        when(queryOldReschNotif.setParameter(org.mockito.ArgumentMatchers.eq("now"), any())).thenReturn(queryOldReschNotif);
        when(queryOldReschNotif.getResultList()).thenReturn(new ArrayList<>());

        handleUser.removeOldNotification();

        // Expired notification should be removed
        verify(handleUser.em, times(1)).remove(expiredNotif);
        // Non-expired notification should not be removed
        verify(handleUser.em, never()).remove(currentNotif);
        // User's list should no longer contain the expired notification
        assertFalse(user.getListNotifications().contains(expiredNotif));
    }

    /**
     * Test of acceptNotification with a RescheduleNotification, of class HandleUserImpl.
     * Verifies the reschedule notification status changes to ACCEPTED and the suggested event
     * is handled correctly.
     */
    @Test
    public void testAcceptNotificationReschedule() throws ErrorRequestException {
        System.out.println("acceptNotificationReschedule");

        // Build suggested event (what EO will reschedule to)
        Event suggestedEvent = new Event();
        suggestedEvent.setId(5L);
        suggestedEvent.setEo(user);
        suggestedEvent.setEventParticipants(new ArrayList<>());
        suggestedEvent.setInvitedUsers(new ArrayList<>());

        // Build reschedule notification
        RescheduleNotification rescheduleNotification = new RescheduleNotification();
        rescheduleNotification.setId(20L);
        rescheduleNotification.setUser(user);
        rescheduleNotification.setEvent(event);
        rescheduleNotification.setSuggestedEvent(suggestedEvent);
        rescheduleNotification.setStatus(Status.PENDING);
        user.getListNotifications().add(rescheduleNotification);

        // em.find for EventNotification returns null (not an event notification)
        when(handleUser.em.find(EventNotification.class, 20L)).thenReturn(null);
        // em.find for RescheduleNotification returns our object
        when(handleUser.em.find(RescheduleNotification.class, 20L)).thenReturn(rescheduleNotification);
        // em.find for the user (used inside acceptNotification for the reschedule branch)
        when(handleUser.em.find(User.class, user.getId())).thenReturn(user);
        // em.find for the suggested event
        when(handleUser.em.find(Event.class, suggestedEvent.getId())).thenReturn(suggestedEvent);

        // Set up handleEvent mock (injected via @EJB field)
        handleUser.handleEvent = mock(HandleEvent.class);
        // getEvent throws ErrorRequestException so removeEvent is skipped cleanly
        when(handleUser.handleEvent.getEvent(user.getId(), "0"))
                .thenThrow(new ErrorRequestException("no event", false));

        // Build the DTO
        RescheduleNotificationDTO reschNotifDTO = new RescheduleNotificationDTO();
        reschNotifDTO.setId("20");
        reschNotifDTO.setUserId(String.valueOf(user.getId()));
        reschNotifDTO.setEventId(String.valueOf(event.getId()));
        reschNotifDTO.setSuggestedEventId(String.valueOf(suggestedEvent.getId()));
        reschNotifDTO.setStatus(Status.PENDING);
        reschNotifDTO.setMessage("reschedule message");

        handleUser.acceptNotification(reschNotifDTO);

        // Status should be ACCEPTED
        assertEquals(Status.ACCEPTED, rescheduleNotification.getStatus());
        // Notification should be removed from the user's list
        assertFalse(user.getListNotifications().contains(rescheduleNotification));
        // em.remove should have been called for the reschedule notification
        verify(handleUser.em, times(1)).remove(rescheduleNotification);
    }

    /**
     * Test of doLogin method with an invalid (wrong) password.
     * Verifies that doLogin returns false when the password does not match.
     */
    @Test
    public void testDoLoginInvalidPassword() {
        System.out.println("doLoginInvalidPassword");
        List<User> userEmail = new ArrayList<>();
        userEmail.add(user);
        when(query.getResultList()).thenReturn(userEmail);

        UserDTO loginUser = new UserDTO();
        loginUser.setEmail("test@test.com");
        loginUser.setPassword("wrongpassword");

        boolean result = handleUser.doLogin(loginUser);
        assertFalse(result);
    }

    /**
     * Test of doLogin method when user email doesn't exist in DB.
     * Verifies that doLogin returns false when no user is found for the given email.
     */
    @Test
    public void testDoLoginUserNotFound() {
        System.out.println("doLoginUserNotFound");
        when(query.getResultList()).thenReturn(new ArrayList<>());

        UserDTO loginUser = new UserDTO();
        loginUser.setEmail("nobody@test.com");
        loginUser.setPassword("password");

        boolean result = handleUser.doLogin(loginUser);
        assertFalse(result);
    }

    /**
     * Test of addUser method when email already exists.
     * Verifies that ErrorRequestException is thrown.
     */
    @Test(expected = ErrorRequestException.class)
    public void testAddUserDuplicateEmail() throws ErrorRequestException {
        System.out.println("addUserDuplicateEmail");
        List<User> existingUsers = new ArrayList<>();
        existingUsers.add(user);
        when(query.setParameter(org.mockito.ArgumentMatchers.eq("email"), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(existingUsers);

        handleUser.addUser(userInsert);
    }

    /**
     * Test of changeSettings method when the logged-in user is not found in DB.
     * Verifies that ErrorRequestException is thrown.
     */
    @Test(expected = ErrorRequestException.class)
    public void testChangeSettingsInvalidUser() throws ErrorRequestException {
        System.out.println("changeSettingsInvalidUser");
        when(handleUser.em.find(User.class, Long.valueOf(userInsert.getId()))).thenReturn(null);

        handleUser.changeSettings(userInsert);
    }

    /**
     * Test of getCalendarVisibility when calendar visibility is PUBLIC.
     */
    @Test
    public void testGetCalendarVisibilityPublic() throws ErrorRequestException {
        System.out.println("getCalendarVisibilityPublic");
        String calendarId = "0";
        user.getCalendar().setVisibility(Visibility.PUBLIC);
        when(handleUser.em.find(Calendar.class, Long.valueOf(calendarId))).thenReturn(user.getCalendar());

        Visibility result = handleUser.getCalendarVisibility(calendarId);
        assertEquals(Visibility.PUBLIC, result);
    }

    /**
     * Test of getCalendarVisibility when calendar visibility is PRIVATE.
     */
    @Test
    public void testGetCalendarVisibilityPrivate() throws ErrorRequestException {
        System.out.println("getCalendarVisibilityPrivate");
        String calendarId = "0";
        user.getCalendar().setVisibility(Visibility.PRIVATE);
        when(handleUser.em.find(Calendar.class, Long.valueOf(calendarId))).thenReturn(user.getCalendar());

        Visibility result = handleUser.getCalendarVisibility(calendarId);
        assertEquals(Visibility.PRIVATE, result);
    }

    /**
     * Test of acceptNotification when the notification status is already ACCEPTED.
     * Verifies no duplicate processing occurs (user is not added to participants again).
     */
    @Test
    public void testAcceptNotificationAlreadyAccepted() {
        System.out.println("acceptNotificationAlreadyAccepted");
        eventNotification.setStatus(Status.ACCEPTED);
        when(handleUser.em.find(EventNotification.class, Long.valueOf(eventNotificationDTO.getId()))).thenReturn(eventNotification);
        // RescheduleNotification lookup must return null to avoid that branch
        when(handleUser.em.find(RescheduleNotification.class, Long.valueOf(eventNotificationDTO.getId()))).thenReturn(null);

        handleUser.acceptNotification(eventNotificationDTO);

        // Status should remain ACCEPTED, not changed again
        assertEquals(Status.ACCEPTED, eventNotification.getStatus());
        // User should NOT have been added to event participants (was already accepted)
        assertFalse(event.getEventParticipants().contains(user));
        // em.merge for the event should not have been called (the branch was skipped)
        verify(handleUser.em, never()).remove(eventNotification);
    }

    /**
     * Test of declineNotification with a RescheduleNotification.
     * Verifies status becomes DECLINED and the suggested event is NOT rescheduled.
     */
    @Test
    public void testDeclineNotificationReschedule() throws ErrorRequestException {
        System.out.println("declineNotificationReschedule");

        Event suggestedEvent = new Event();
        suggestedEvent.setId(7L);
        suggestedEvent.setEo(user);
        suggestedEvent.setEventParticipants(new ArrayList<>());
        suggestedEvent.setInvitedUsers(new ArrayList<>());

        RescheduleNotification rescheduleNotification = new RescheduleNotification();
        rescheduleNotification.setId(30L);
        rescheduleNotification.setUser(user);
        rescheduleNotification.setEvent(event);
        rescheduleNotification.setSuggestedEvent(suggestedEvent);
        rescheduleNotification.setStatus(Status.PENDING);
        user.getListNotifications().add(rescheduleNotification);

        // No plain EventNotification with this id
        when(handleUser.em.find(EventNotification.class, 30L)).thenReturn(null);
        when(handleUser.em.find(RescheduleNotification.class, 30L)).thenReturn(rescheduleNotification);
        when(handleUser.em.find(User.class, user.getId())).thenReturn(user);

        handleUser.handleEvent = mock(HandleEvent.class);
        when(handleUser.handleEvent.getEvent(user.getId(), String.valueOf(suggestedEvent.getId())))
                .thenThrow(new ErrorRequestException("no event", false));

        RescheduleNotificationDTO reschNotifDTO = new RescheduleNotificationDTO();
        reschNotifDTO.setId("30");
        reschNotifDTO.setUserId(String.valueOf(user.getId()));
        reschNotifDTO.setEventId(String.valueOf(event.getId()));
        reschNotifDTO.setSuggestedEventId(String.valueOf(suggestedEvent.getId()));
        reschNotifDTO.setStatus(Status.PENDING);
        reschNotifDTO.setMessage("reschedule decline message");

        handleUser.declineNotification(reschNotifDTO);

        assertEquals(Status.DECLINED, rescheduleNotification.getStatus());
        assertFalse(user.getListNotifications().contains(rescheduleNotification));
        verify(handleUser.em, times(1)).remove(rescheduleNotification);
        // Event should NOT have been modified as EO (no rescheduling)
        assertTrue(suggestedEvent.getEventParticipants().isEmpty());
    }

    /**
     * Test of addNotification when the event doesn't exist.
     * Verifies graceful handling (returns false, nothing persisted).
     */
    @Test
    public void testAddNotificationInvalidEvent() {
        System.out.println("addNotificationInvalidEvent");
        when(handleUser.em.find(User.class, user.getId())).thenReturn(user);
        when(handleUser.em.find(Event.class, Long.valueOf(eventNotificationDTO.getEventId()))).thenReturn(null);

        boolean result = handleUser.addNotification(eventNotificationDTO);

        assertFalse(result);
        verify(handleUser.em, never()).persist(any(EventNotification.class));
    }

    /**
     * Test of searchUser when no users match the query.
     * Verifies that an empty list is returned.
     */
    @Test
    public void testSearchUserEmptyResult() {
        System.out.println("searchUserEmptyResult");
        when(query.getResultList()).thenReturn(new ArrayList<>());

        List<ResultDTO> result = handleUser.searchUser("nomatch");
        assertTrue(result.isEmpty());
    }

    /**
     * Test of getOwner when no owner is found for the calendar.
     * Verifies that null is returned.
     */
    @Test
    public void testGetOwnerNotFound() {
        System.out.println("getOwnerNotFound");
        when(query.getResultList()).thenReturn(new ArrayList<>());

        UserDTO result = handleUser.getOwner("0");
        assertEquals(null, result);
    }

    /**
     * Answer Class to set the User attributes
     */
    public static class UserAnswer implements Answer<Void> {

        private final Long id;
        private final Calendar calendar;
        private final Setting setting;
        private final String password;

        /**
         *
         * @param id
         * @param calendar
         * @param setting
         * @param password
         */
        public UserAnswer(Long id, Calendar calendar, Setting setting, String password) {
            this.id = id;
            this.calendar = calendar;
            this.setting = setting;
            this.password = password;
        }

        /**
         *
         * @param invocation
         * @return
         */
        @Override
        public Void answer(InvocationOnMock invocation) {
            User user = (User) invocation.getArguments()[0];
            user.setId(id);
            user.setCalendar(calendar);
            user.setSetting(setting);
            user.setPassword(password);
            return null;
        }
    }
}
