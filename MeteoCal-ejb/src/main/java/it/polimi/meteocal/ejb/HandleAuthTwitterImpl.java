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

import it.polimi.meteocal.entities.Setting;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.PasswordHash;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.AccessToken;
import twitter4j.OAuthAuthorization;
import twitter4j.RequestToken;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Session Bean implementation class HandleAuthTwitterImpl
 */
@Stateless
public class HandleAuthTwitterImpl implements HandleAuthTwitter {

    // OAuth Data
    private static final String CLIENT_ID = System.getenv("TWITTER_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("TWITTER_CLIENT_SECRET");
    private static final String URL_BASE = System.getenv().getOrDefault("APP_BASE_URL", "http://www.meteocal.tk");
    private static final String REQUEST_TOKEN_KEY = "TWITTER_REQUEST_TOKEN";

    private static final Logger LOGGER = LogManager.getLogger(HandleAuthTwitterImpl.class.getName());


    /**
     * Method that return the Twitter object that allows the access to the
     * Twitter API
     *
     * @param user the user in MeteoCal
     * @return null if there was a problem with the creation of the Twitter
     * object
     */
    public static Twitter getTwitterObject(User user) {
        if (user.getTwitterToken() == null) {
            // Twitter not connected
            return null;
        }

        try {
            return Twitter.newBuilder()
                    .oAuthConsumer(CLIENT_ID, CLIENT_SECRET)
                    .oAuthAccessToken(user.getTwitterToken(), user.getTwitterTokenSecret())
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
    }

    @PersistenceContext
    EntityManager em;


    /**
     * Default constructor.
     */
    public HandleAuthTwitterImpl() {
        // Required no-arg constructor for EJB
    }

    private static OAuthAuthorization newAuthorization() {
        return OAuthAuthorization.newBuilder()
                .oAuthConsumer(CLIENT_ID, CLIENT_SECRET)
                .build();
    }

    @Override
    public String getUrlLoginTwitter() {
        String urlLogin = "../index.xhtml";

        try {
            RequestToken requestToken = newAuthorization().getOAuthRequestToken(URL_BASE
                    + "/MeteoCal-web/loginTwitter.xhtml");
            urlLogin = requestToken.getAuthenticationURL();
            AuthUtil.setSessionAttribute(REQUEST_TOKEN_KEY, requestToken);
        } catch (TwitterException e) {
            LOGGER.log(Level.ERROR, e);
        }
        LOGGER.log(Level.INFO, "URL LOGIN {}", urlLogin);
        return urlLogin;
    }

    @Override
    public boolean doLoginTwitter(String verifier) {
        try {
            LOGGER.log(Level.INFO, () -> "Verifier: " + verifier);

            Object requestToken = AuthUtil.removeSessionAttribute(REQUEST_TOKEN_KEY);
            if (!(requestToken instanceof RequestToken token)) {
                LOGGER.log(Level.WARN, "Twitter login without a pending request token");
                return false;
            }
            AccessToken accessToken = newAuthorization().getOAuthAccessToken(token, verifier);
            long twitterUserId = accessToken.getUserId();
            Twitter twitter = Twitter.newBuilder()
                    .oAuthConsumer(CLIENT_ID, CLIENT_SECRET)
                    .oAuthAccessToken(accessToken)
                    .build();

            if (!AuthUtil.isUserLogged()) {
                // Saves the new user data in the DB
                User utente;
                TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_TWITTER_ID, User.class);
                q.setParameter("twitterId", twitterUserId);
                if (q.getResultList().isEmpty()) {
                    // The user isn't in the system
                    utente = new User();
                    twitter4j.v1.User user = twitter.v1().users().showUser(twitterUserId);
                    utente.setTwitterId(twitterUserId);
                    utente.setTwitterToken(accessToken.getToken());
                    utente.setTwitterTokenSecret(accessToken.getTokenSecret());
                    StringTokenizer stok = new StringTokenizer(user.getName());
                    utente.setFirstName(stok.nextToken());
                    if (stok.hasMoreTokens()) {
                        utente.setLastName(stok.nextToken());
                    } else {
                        utente.setLastName("");
                    }
                    while (stok.hasMoreTokens()) {
                        utente.setLastName(utente.getLastName() + " " + stok.nextToken());

                    }
                    LOGGER.log(Level.INFO, utente::getLastName);
                    utente.setAvatar(user.getProfileImageURLHttps());
                    // MANCA EMAIL
                    utente.setEmail(utente.getFirstName().toLowerCase() + "." + utente.getLastName().toLowerCase().replaceAll("\\s", ".") + "@twitter.com");
                    // MANCA BIRTH DATE
                    setHashedPassword(utente);

                    Setting setting = new Setting();
                    setting.setTimeZone(TimeZone.getTimeZone(user.getTimeZone()));
                    utente.setSetting(setting);

                    LOGGER.log(Level.INFO, utente::toString);

                    em.persist(utente);
                    em.flush();
                    em.refresh(utente);
                } else {
                    // The user is already in the system
                    LOGGER.log(Level.INFO, "User already registred with twitter");

                    utente = q.getResultList().get(0);
                    if (utente.getTwitterToken().equals(accessToken.getToken())) {
                        LOGGER.log(Level.INFO, "Twitter token not changed");
                    } else {
                        LOGGER.log(Level.INFO, "Twitter token updated");
                    }
                    if (utente.getTwitterTokenSecret().equals(accessToken
                            .getTokenSecret())) {
                        LOGGER.log(Level.INFO, "TwitterToken secret not updated");
                    } else {
                        LOGGER.log(Level.INFO, "TwitterToken secret updated");
                        utente.setTwitterToken(accessToken.getToken());
                        utente.setTwitterTokenSecret(accessToken
                                .getTokenSecret());
                        utente = em.merge(utente);
                        em.flush();
                    }

                }

                AuthUtil.makeUserSession(utente.getId());

            } else {
                // User already in the system
                User utente = em.find(User.class,
                        AuthUtil.getUserID());

                TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_TWITTER_ID, User.class);
                q.setParameter("twitterId", twitterUserId);

                if (q.getResultList().isEmpty()) {
                    // The user account twitter isn't already present in the db so set the new Twitter data
                    utente.setTwitterId(twitterUserId);
                    utente.setTwitterToken(accessToken.getToken());
                    utente.setTwitterTokenSecret(accessToken.getTokenSecret());
                    utente = em.merge(utente);
                    em.flush();
                } else {

                    // The user account is already in the system
                    LOGGER.log(Level.INFO, "User already registered in the system");
                    User utenteVecchio = q.getResultList().get(0);
                    if (!Objects.equals(utente.getId(), utenteVecchio.getId())) {
                        // Need to merge the two account
                        HandleUserImpl.mergeUserAccount(utente,
                                utenteVecchio);

                        // set the new Twitter data
                        utente.setTwitterId(twitterUserId);
                        utente.setTwitterToken(accessToken.getToken());
                        utente.setTwitterTokenSecret(accessToken
                                .getTokenSecret());

                        utente = em.merge(utente);
                        em.flush();

                        // Transfer all the settings
                        HandleUserImpl.mergeOldUserNewUser(em, utente,
                                utenteVecchio);

                        em.remove(utenteVecchio);
                        em.flush();
                    }
                }

            }

        } catch (TwitterException e) {
            LOGGER.log(Level.INFO, e);
            return false;
        }
        return true;
    }

    private void setHashedPassword(User utente) {
        try {
            utente.setPassword(PasswordHash.createHash(utente.getFirstName() + "." + utente.getLastName()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.log(Level.FATAL, ex, ex);
        }
    }

    @Override
    public boolean isTwitterCollegato() {
        return isTwitterCollegato(AuthUtil.getUserID());
    }

    @Override
    public boolean isTwitterCollegato(long idUtente) {
        User utente = em.find(User.class, idUtente);
        if (utente == null) {
            return false;
        }
        return utente.getTwitterId() != null;

    }

}
