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

import it.polimi.meteocal.entities.User;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author fede
 */
@RunWith(Arquillian.class)
public class HandleEventImplIT {

    @EJB
    HandleEvent handleEvent;

    @PersistenceContext
    EntityManager em;

    /**
     *
     * @return
     */
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(HandleEvent.class)
                .addClass(HandleEventImpl.class)
                .addClass(HandleUser.class)
                .addClass(HandleUserImpl.class)
                .addClass(HandleForecast.class)
                .addClass(HandleForecastImpl.class)
                .addPackage(User.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     *
     */
    @Test
    public void UserManagerShouldBeInjected() {
        assertNotNull(handleEvent);
    }

    /**
     *
     */
    @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }

//    @Test
//        public void newUserCorrectInsert() {
//        RegisteredUser newUser = new RegisteredUser();
//        newUser.setEmail("prova@prova.it");
//        Calendar newCalendar = new Calendar();
//        newCalendar.setOwner(newUser);
//        ruf.save(newUser,newCalendar);
//                verify(ruf.em,times(1)).persist(newUser);
//        assertThat(newUser.getEmail(),is("prova@prova.it"));
//        }
}
