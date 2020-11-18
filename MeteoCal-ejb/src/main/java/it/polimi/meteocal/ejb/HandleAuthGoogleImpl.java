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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import it.polimi.meteocal.entities.Setting;
import it.polimi.meteocal.entities.User;
import it.polimi.meteocal.util.AuthUtil;
import it.polimi.meteocal.util.PasswordHash;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class HandleAuthGoogleImpl
 */
@Stateless
public class HandleAuthGoogleImpl implements HandleAuthGoogle {

    private static final String CLIENT_ID =  "< Insert CLIENT ID >";
    private static final String CLIENT_SECRET = "< Insert CLIENT SECRET >";
    private static final String APPLICATION_NAME = "MeteoCal";
    
    private static final Logger LOGGER = LogManager.getLogger(HandleAuthGoogleImpl.class.getName());
    
    /**
     * Global instance of the HTTP transport.
     */
    private HttpTransport httpTransport;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory
            .getDefaultInstance();

    /**
     * Method that return the Plus object that allows the access to the
     * GooglePlus API
     *
     * @param user the user in MeteoCal
     * @return null if there was a problem with the creation of the Plus object
     */
    public static Plus getPlusObject(User user) {
        Plus plus = null;
        final User utente2 = user;
        if (user.getGoogleToken() == null) {
            // Case in wich google plus is not connected
            return null;
        }
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(new JacksonFactory())
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .addRefreshListener(new CredentialRefreshListener() {

                        @Override
                        public void onTokenResponse(Credential credential,
                                TokenResponse tokenResponse) {
                            HandleAuthGoogleImpl.setGoogleToken(credential,
                                    tokenResponse, utente2);
                            // Handle success.
                            LOGGER.log(Level.INFO, "Credential was refreshed successfully.");
                        }

                        @Override
                        public void onTokenErrorResponse(Credential credential,
                                TokenErrorResponse tokenErrorResponse) {
                            // Handle error.
                            LOGGER.log(Level.ERROR, "Credential was not refreshed successfully. "
                                    + "Redirect to error page or login screen.");
                        }
                    })
                    /* You can also add a credential store listener to have credentials stored automatically. .addRefreshListener(new CredentialStoreRefreshListener(userId, credentialStore)) */
                    .build();

            // Set authorized credentials.
            credential.setFromTokenResponse(JSON_FACTORY.fromString(
                    user.getGoogleToken(), GoogleTokenResponse.class));
            /* Though not necessary when first created, you can manually refresh the token, which is needed after 60 minutes. */
            if (credential.getExpiresInSeconds() < 1) {
                boolean ref = credential.refreshToken();
                LOGGER.log(Level.INFO, "Refresh token: " + ref);
                LOGGER.log(Level.INFO, "Access token: " + credential.getAccessToken());
                LOGGER.log(Level.INFO, "Refresh token: " + credential.getRefreshToken());

            }

            // Create a new authorized API client.
            plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.ERROR, e, e);
        }

        return plus;
    }

    /**
     * Method that updates the google token in the DB using the refresh token
     *
     * @param credential the credential for access the google API
     * @param tokenResponse the http response with the token
     * @param user the user in MeteoCal
     * @see Credential
     * @see TokenResponse
     */
    protected static void setGoogleToken(Credential credential, TokenResponse tokenResponse, User user) {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("MeteoCalEJB");
        EntityManager em = emf.createEntityManager();
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            String tokenGoogle = tokenResponse.toString();
            LOGGER.log(Level.INFO, tokenGoogle);
            JsonObject newToken = JsonParser.parseString(tokenGoogle).getAsJsonObject();
            JsonObject oldToken = JsonParser.parseString(user.getGoogleToken())
                    .getAsJsonObject();
            newToken.add("refresh_token", oldToken.get("refresh_token"));
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(true);
            Streams.write(newToken, jsonWriter);
            tokenGoogle = stringWriter.toString();

            LOGGER.log(Level.INFO, tokenGoogle);

            if (tokenGoogle.contains("refresh_token")) {
                LOGGER.log(Level.INFO, "GoogleToken updated");
                user.setGoogleToken(tokenGoogle);
                em.merge(user);
                em.joinTransaction();
                em.flush();
            }
        } catch (GeneralSecurityException | IOException | JsonSyntaxException e) {
            LOGGER.log(Level.ERROR, e, e);
        }
        em.close();
        emf.close();
    }

    @PersistenceContext
    EntityManager em;

    private GoogleAuthorizationCodeFlow flow;

    /**
     * Default constructor.
     */
    public HandleAuthGoogleImpl() {

    }

    @Override
    public boolean doLoginGoogle(String code) {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleTokenResponse tokenResponse = flow
                    .newTokenRequest(code)
                    .setRedirectUri(
                            "http://www.meteocal.tk/MeteoCal-web/loginGoogle.xhtml")
                    .execute();
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(new JacksonFactory())
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .addRefreshListener(new RefreshListener()).build();

            // Set authorized credentials.
            credential.setFromTokenResponse(tokenResponse);
            // Though not necessary when first created, you can manually refresh
            // the
            // token, which is needed after 60 minutes.
            // credential.refreshToken();

            String tokenGoogle = tokenResponse.toString();

            // Insert new data in the DB
            Plus plus = new Plus.Builder(httpTransport, JSON_FACTORY,
                    credential).setApplicationName(APPLICATION_NAME).build();
            Person mePerson = plus.people().get("me").execute();

            if (!AuthUtil.isUserLogged()) {
                // Saves the new data of the user in the DB
                User user;
                TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_GOOGLE_ID, User.class);
                q.setParameter("googleId", mePerson.getId());
                TypedQuery<User> q2 = em.createNamedQuery(User.FIND_BY_EMAIL, User.class);
                if (mePerson.getEmails() != null) {

                    q2.setParameter("email", mePerson.getEmails().get(0).getValue());
                } else {
                    q2.setParameter("email", "");
                }
                if (q.getResultList().isEmpty() && q2.getResultList().isEmpty()) {
                    // The user is not in the system
                    user = new User();
                    user.setGoogleId(mePerson.getId());
                    user.setGoogleToken(tokenGoogle);
                    user.setFirstName(mePerson.getName().getGivenName());
                    user.setLastName(mePerson.getName().getFamilyName());
                    user.setAvatar(mePerson.getImage().getUrl());
                    if (mePerson.getEmails() != null) {
                        user.setEmail(mePerson.getEmails().get(0).getValue());
                    }
                    if (mePerson.getBirthday() != null) {

                        try {
                            user.setDateBirth(new SimpleDateFormat("MM/dd").parse(mePerson.getBirthday()));
                        } catch (ParseException ex) {
                            LOGGER.log(Level.WARN, ex);
                        }

                    }
                    try {
                        user.setPassword(PasswordHash.createHash(user.getFirstName() + "." + user.getLastName()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                        LOGGER.log(Level.FATAL, ex, ex);
                    }

                    LOGGER.log(Level.INFO, user.toString());

                    Setting setting = new Setting();
                    setting.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                    user.setSetting(setting);
                    em.persist(user);
                    em.flush();
                    em.refresh(user);
                } else if (!q.getResultList().isEmpty()) {
                    // The user is already in the system
                    LOGGER.log(Level.INFO, "User already registered with Google");
                    user = q.getResultList().get(0);
                    if (tokenGoogle.contains("refresh_token")) {
                        LOGGER.log(Level.INFO, "GoogleToken updated");
                        user.setGoogleToken(tokenGoogle);
                        em.merge(user);
                        em.flush();
                    }

                } else {

                    LOGGER.log(Level.INFO, "User already registered with classic method");
                    user = q2.getResultList().get(0);
                    //TODO merge informazioni da google mancanti

                    em.merge(user);
                    em.flush();
                }

                // Make the session for the user
                AuthUtil.makeUserSession(user.getId());

            } else {
                // User already registered in the system
                User utente = em.find(User.class,
                        AuthUtil.getUserID());

                TypedQuery<User> q = em.createNamedQuery(User.FIND_BY_GOOGLE_ID, User.class);
                q.setParameter("googleId", mePerson.getId());
                if (q.getResultList().isEmpty()) {
                    // The user account isn't already present in the db so set the new GooglePlus data
                    utente.setGoogleId(mePerson.getId());
                    utente.setGoogleToken(tokenGoogle);
                    em.merge(utente);
                    em.flush();
                } else {

                    // User account already in the system
                    LOGGER.log(Level.INFO, "User already registered with GooglePlus");
                    User oldUser = q.getResultList().get(0);
                    if (!Objects.equals(utente.getId(), oldUser.getId())) {
                        // Need to merge the two account
                        utente = HandleUserImpl.mergeUserAccount(utente,
                                oldUser);

                        // set the new GooglePlus data
                        utente.setGoogleId(mePerson.getId());
                        utente.setGoogleToken(tokenGoogle);

                        em.merge(utente);
                        em.flush();

                        // Transfer all the settings
                        HandleUserImpl
                                .mergeOldUserNewUser(em,
                                        utente, oldUser);

                        em.remove(oldUser);
                        em.flush();
                    }

                }

            }

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            return false;
        } catch (GeneralSecurityException ex) {
            LOGGER.log(Level.ERROR, ex);
        }

        return true;
    }

    @Override
    public boolean isGoogleCollegato() {
        return isGoogleCollegato(AuthUtil.getUserID());
    }

    @Override
    public boolean isGoogleCollegato(long idUtente) {
        User utente = em.find(User.class, idUtente);
        if (utente == null) {
            return false;
        }
        return utente.getGoogleId() != null;
    }

    @Override
    public String getUrlLoginGoogle() {
        final List<String> scope = Arrays.asList(
                "https://www.googleapis.com/auth/plus.login",
                "https://www.googleapis.com/auth/userinfo.email");

        final String redirectUrl = "http://www.meteocal.tk/MeteoCal-web/loginGoogle.xhtml";

        flow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
                new JacksonFactory(), CLIENT_ID, CLIENT_SECRET, scope)
                //.setApprovalPrompt("force")
                // Set the access type to offline so that the token can be
                // refreshed.
                // By default, the library will automatically refresh tokens
                // when it
                // can, but this can be turned off by setting
                // dfp.api.refreshOAuth2Token=false in your ads.properties file.
                .setAccessType("offline")
                .build();

        // This command-line server-side flow example requires the user to open
        // the
        // authentication URL in their browser to complete the process. In most
        // cases, your app will use a browser-based server-side flow and your
        // user will not need to copy and paste the authorization code. In this
        // type of app, you would be able to skip the next 5 lines.
        // You can also look at the client-side and one-time-code flows for
        // other
        // options at https://developers.google.com/+/web/signin/
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUrl).build();

    }

    /**
     * Class Listener needed to refresh the google token
     */
    private class RefreshListener implements CredentialRefreshListener {

        @Override
        public void onTokenResponse(Credential credential,
                TokenResponse tokenResponse) {
            try {
                httpTransport = new NetHttpTransport();
                Plus plus = new Plus.Builder(httpTransport,
                        JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                Person mePerson = plus.people().get("me")
                        .execute();
                User utente;

                TypedQuery<User> q = em
                        .createNamedQuery(User.FIND_BY_GOOGLE_ID,
                                User.class);
                q.setParameter("googleId", mePerson.getId());
                utente = q.getResultList().get(0);
                String tokenGoogle = tokenResponse.toString();

                LOGGER.log(Level.INFO, "GoogleToken updated");
                utente.setGoogleToken(tokenGoogle);
                em.merge(utente);
                em.flush();

            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e, e);
            }

            // Handle success.
            LOGGER.log(Level.INFO, "Credential was refreshed successfully.");
        }

        @Override
        public void onTokenErrorResponse(Credential credential,
                TokenErrorResponse tokenErrorResponse) {
            // Handle error.
            LOGGER.log(Level.ERROR, "Credential was not refreshed successfully. "
                    + "Redirect to error page or login screen.");
        }
    }

}
