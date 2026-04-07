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

import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton EJB that runs periodic maintenance tasks on a schedule,
 * replacing per-request execution from CalendarBean.
 */
@Singleton
@Startup
public class MaintenanceScheduler {

    private static final Logger LOGGER = LogManager.getLogger(MaintenanceScheduler.class.getName());

    @EJB
    HandleUser handleUser;

    @EJB
    HandleForecast handleForecast;

    /**
     * Runs every hour: removes expired notifications and stale forecasts,
     * then refreshes forecast data for upcoming events.
     */
    @Schedule(hour = "*", minute = "0", second = "0", persistent = false)
    public void runMaintenance() {
        LOGGER.log(Level.INFO, "Running scheduled maintenance");
        try {
            handleUser.removeOldNotification();
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "removeOldNotification failed", e);
        }
        try {
            handleForecast.removeOldForecast();
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "removeOldForecast failed", e);
        }
    }
}
