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
import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.dto.SettingDTO;
import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.entities.Calendar;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.EventNotification;
import it.polimi.meteocal.entities.Notification;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(userInsert.getEmail());
        Calendar calendar = mock(Calendar.class);
        when(calendar.getId()).thenReturn(Long.MIN_VALUE);
        when(user.getCalendar()).thenReturn(calendar);
        when(user.getSetting()).thenReturn(mock(Setting.class));
        when(handleUser.em.find(User.class, 0L)).thenReturn(user);
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
        assertEquals(true, result);
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
     * Answer Class to set the User attributes
     */
    public class UserAnswer implements Answer<Void> {

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
         * @throws Throwable
         */
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            User user = (User) invocation.getArguments()[0];
            user.setId(id);
            user.setCalendar(calendar);
            user.setSetting(setting);
            user.setPassword(password);
            return null;
        }
    }
}
