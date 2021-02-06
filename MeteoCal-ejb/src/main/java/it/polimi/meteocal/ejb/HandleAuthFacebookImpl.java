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

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import it.polimi.meteocal.entities.Calendar;
import it.polimi.meteocal.entities.Setting;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.PasswordHash;
import it.polimi.meteocal.util.Visibility;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class HandleAuthFacebookImpl
 */
@Stateless
public class HandleAuthFacebookImpl implements HandleAuthFacebook {

    private static final String APP_ID = "< Insert APP ID >";
    private static final String APPSECRET = "< Insert APP SECRET >";
    private static final String URL_BASE = "http://www.meteocal.tk";
    
    private static final Logger LOGGER = LogManager.getLogger(HandleAuthFacebook.class.getName());

    /**
     * Method that return the FacebookClient that allows the access to the
     * Facebook API
     *
     * @param user the user in MeteoCal
     * @return null if there was a problem with the creation of the Facebook
     * Client
     */
    public static FacebookClient getFacebookClientObject(User user) {
        String accessToken;
        accessToken = user.getFacebookToken();
        FacebookClient facebookClient;
        facebookClient = new DefaultFacebookClient(accessToken, APPSECRET, Version.VERSION_2_5);
        return facebookClient;
    }

    private String accessToken;
    private FacebookClient facebookClient;

    @PersistenceContext
    EntityManager em;

    /**
     * Default constructor.
     */
    public HandleAuthFacebookImpl() {

    }

    @Override
    public boolean doLoginFacebook(String faceCode) {
        if (faceCode != null && !"".equals(faceCode)) {
            String redirectUrl = URL_BASE
                    + "/MeteoCal-web/loginFacebook.xhtml";
            String newUrl = "https://graph.facebook.com/oauth/access_token?client_id="
                    + APP_ID
                    + "&redirect_uri="
                    + redirectUrl
                    + "&client_secret=" + APPSECRET + "&code=" + faceCode;
            LOGGER.log(Level.INFO, "URL FB: " + newUrl);
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            try {
                HttpGet httpget = new HttpGet(newUrl);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httpget,
                        responseHandler);
                LOGGER.log(Level.INFO, "Response Body: " + responseBody);
                accessToken = StringUtils.removeStart(responseBody,
                        "access_token=");
                int i = accessToken.indexOf("&");
                accessToken = accessToken.substring(0, i);
                LOGGER.log(Level.INFO, "AccessToken: " + accessToken);

                facebookClient = new DefaultFacebookClient(accessToken,
                        APPSECRET, Version.VERSION_2_5);
                com.restfb.types.User userFB = facebookClient.fetchObject("me", com.restfb.types.User.class);

                if (!AuthUtil.isUserLogged()) {
                    // Save the new data of the user
                    User utente;
                    TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_FACEBOOK_ID, User.class);
                    q.setParameter("facebookId", userFB.getId());

                    TypedQuery<User> q2 = em.createNamedQuery(User.FIND_BY_EMAIL, User.class);
                    q2.setParameter("email", userFB.getEmail());

                    if (q.getResultList().isEmpty() && q2.getResultList().isEmpty()) {
                        // The userFB isn't in the system
                        utente = setupNewUser(userFB);
                        em.persist(utente);
                        em.flush();
                        em.refresh(utente);
                    } else if (!q.getResultList().isEmpty()) {
                        // The User is already in the system with fb
                        LOGGER.log(Level.INFO, "User already registered with Facebook");
                        utente = q.getResultList().get(0);
                        if (utente.getFacebookToken().equals(accessToken)) {
                            LOGGER.log(Level.INFO, "Facebook token no needed change");
                        } else {
                            LOGGER.log(Level.INFO, "Facebook token updated");
                            utente.setFacebookToken(accessToken);
                            em.merge(utente);
                            em.flush();
                        }

                    } else {
                        LOGGER.log(Level.INFO, "User already registered with classic method");
                        utente = q2.getResultList().get(0);
                        //TODO merge informazioni da facebook mancanti

                        em.merge(utente);
                        em.flush();

                    }

                    // Make the session for the user
                    AuthUtil.makeUserSession(utente.getId());

                } else {
                    // User already logged in the system
                    User utente = em.find(User.class,
                            AuthUtil.getUserID());

                    TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_FACEBOOK_ID, User.class);
                    q.setParameter("facebookId", userFB.getId());
                    if (q.getResultList().isEmpty()) {
                        // The user account isn't already present in the db so set the new FB data
                        utente.setFacebookId(userFB.getId());
                        utente.setFacebookToken(accessToken);
                        em.merge(utente);
                        em.flush();
                    } else {

                        // User account already in the system
                        LOGGER.log(Level.INFO, "User already registered with Facebook");
                        User oldUser = q.getResultList().get(0);
                        if (!Objects.equals(utente.getId(), oldUser.getId())) {
                            // Need to merge the two account
                            HandleUserImpl.mergeUserAccount(utente,
                                    oldUser);

                            // set the new facebook data
                            utente.setFacebookId(userFB.getId());
                            utente.setFacebookToken(accessToken);

                            em.merge(utente);
                            em.flush();

                            // Transfer all the data that can block the old user remove
                            HandleUserImpl
                                    .mergeOldUserNewUser(
                                            em, utente, oldUser);

                            em.remove(oldUser);
                            em.flush();
                        }
                    }

                }

            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e);
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {

                    LOGGER.log(Level.WARN, e);
                }
            }
        }

        return true;
    }

    @Override
    public String getUrlLoginFacebook() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        String sessionId = session.getId();
        String redirectUrl = URL_BASE + "/MeteoCal-web/loginFacebook.xhtml";
        return "https://www.facebook.com/dialog/oauth?client_id="
                + APP_ID + "&redirect_uri=" + redirectUrl + "&scope="
                + "public_profile,user_friends,email"/*scopeUser + scopeFriend + scopeExtended */ + "&state="
                + sessionId;

    }

    @Override
    public boolean isFacebookCollegato() {
        return isFacebookCollegato(AuthUtil.getUserID());
    }

    @Override
    public boolean isFacebookCollegato(long idUtente) {
        User utente = em.find(User.class, idUtente);
        if (utente == null) {
            return false;
        }
        return null != utente.getFacebookId();
    }

    /**
     * This metod maps the facebook user to MeteoCal User.
     *
     * @param userFB the Facebook Class for the User.
     * @return The User entity of MeteoCal to persist in the DB
     */
    private User setupNewUser(com.restfb.types.User userFB) {
        User utente = new User();
        utente.setFacebookId(userFB.getId());
        utente.setFacebookToken(accessToken);
        utente.setFirstName(userFB.getFirstName());
        utente.setLastName(userFB.getLastName());
        utente.setEmail(userFB.getEmail());
        utente.setDateBirth(userFB.getBirthdayAsDate());
        if (utente.getDateBirth() == null && userFB.getBirthday() != null) {

            try {
                utente.setDateBirth(new SimpleDateFormat("MM/dd").parse(userFB.getBirthday()));
            } catch (ParseException ex) {
                LOGGER.log(Level.WARN, ex);
            }

        }
        utente.setAvatar("https://graph.facebook.com/" + userFB.getId() + "/picture?type=normal");
        try {
            utente.setPassword(PasswordHash.createHash(userFB.getFirstName() + "." + userFB.getLastName()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.log(Level.FATAL, ex, ex);
        }
        // SET UP SETTING
        Setting setting = new Setting();
        setting.setTimeZone(TimeZone.getTimeZone("GMT" + userFB.getTimezone()));
        LOGGER.log(Level.INFO, "TimeZone User " + utente.getId() + ": " + setting.getTimeZone().toString());
        utente.setSetting(setting);

        // SETUP CALENDAR
        Calendar calendar = new Calendar();
        calendar.setVisibility(Visibility.PRIVATE);
        utente.setCalendar(calendar);

        return utente;
    }
}
