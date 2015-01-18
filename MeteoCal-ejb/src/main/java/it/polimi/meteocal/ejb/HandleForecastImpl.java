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

import com.neovisionaries.i18n.CountryCode;
import it.polimi.meteocal.dto.ForecastDTO;
import it.polimi.meteocal.dto.WeatherDTO;
import it.polimi.meteocal.entities.Event;
import it.polimi.meteocal.entities.Forecast;
import it.polimi.meteocal.entities.Location;
import it.polimi.meteocal.entities.Weather;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.HourlyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Session Bean implementation class HandleForecastImpl
 */
@Stateless
public class HandleForecastImpl implements HandleForecast {

    private static final Logger LOGGER = LogManager.getLogger(HandleForecastImpl.class.getName());
    @PersistenceContext
            EntityManager em;

    @Override
    public ForecastDTO getForecast(String location, Date date) {
        if (location != null) {
            // CHECK DB INFORMATION AND FIND NEAREST FORECAST
            TypedQuery<Forecast> query = em.createNamedQuery(Forecast.FIND_BY_LOCATION,
                    Forecast.class);
            query.setParameter("location", location);
            ForecastDTO forecast = nearestForecast(date, query.getResultList());
            if (forecast == null) {
                // ADD THE FORECAST INFORMATION FROM THE OWM API
                Calendar fiveDays = Calendar.getInstance();
                fiveDays.add(Calendar.DATE, 5);
                if (date.after(fiveDays.getTime())) {
                    addDailyForecasts(location);
                } else {
                    addHourlyForecasts(location);
                }
                if (!query.getResultList().isEmpty()) {
                    forecast = nearestForecast(date, query.getResultList());
                }
            }
            return forecast;
        }
        return null;
    }

    @Override
    public List<ForecastDTO> getForecasts(String location) {
        TypedQuery<Forecast> query = em.createNamedQuery(Forecast.FIND_BY_LOCATION,
                Forecast.class);
        query.setParameter("location", location);

        List<ForecastDTO> forecasts = new ArrayList<>();
        if (!query.getResultList().isEmpty()) {
            for (Forecast forecastEntity : query.getResultList()) {
                ForecastDTO forecast = mapForecastToForecastDTO(forecastEntity);
                LOGGER.log(Level.INFO, forecast.toString());
                forecasts.add(forecast);
            }

        } else {
            addHourlyForecasts(location);
            addDailyForecasts(location);
            if (!query.getResultList().isEmpty()) {
                for (Forecast forecastEntity : query.getResultList()) {
                    ForecastDTO forecast = mapForecastToForecastDTO(forecastEntity);
                    LOGGER.log(Level.INFO, forecast.toString());
                    forecasts.add(forecast);
                }

            }
        }

        return forecasts;
    }

    @Override
    public void addHourlyForecasts(String location) {
        OpenWeatherMap owm = new OpenWeatherMap("92a7c7673050d5da5c850a8c1cb995b7");
        try {
            HourlyForecast hf = owm.hourlyForecastByCityName(location);
            if (hf.isValid()) {
                for (int i = 0; i < hf.getForecastCount(); i++) {
                    HourlyForecast.Forecast forecast = hf.getForecastInstance(i);
                    if (forecast.hasWeatherInstance()) {
                        for (int j = 0; j < forecast.getWeatherCount(); j++) {
                            HourlyForecast.Forecast.Weather weather = forecast.getWeatherInstance(j);
                            Forecast entityForecast = new Forecast();
                            if (hf.hasCityInstance()) {
                                if (hf.getCityInstance().getCountryCode().length() <= 2) {
                                    entityForecast.setLocation(hf.getCityInstance().getCityName() + ", " + hf.getCityInstance().getCountryCode());
                                } else {
                                    String countryCode = CountryCode.findByName(hf.getCityInstance().getCountryCode()).get(0).name();
                                    if (countryCode != null) {
                                        entityForecast.setLocation(hf.getCityInstance().getCityName() + ", " + countryCode);
                                    }
                                }
                                entityForecast.setLatitude(hf.getCityInstance().getCoordInstance().getLatitude());
                                entityForecast.setLongitude(hf.getCityInstance().getCoordInstance().getLongitude());
                            }
                            entityForecast.setCreationDate(java.util.Calendar.getInstance());
                            java.util.Calendar date = Calendar.getInstance();
                            date.setTime(forecast.getDateTime());
                            entityForecast.setForecastDate(date);
                            Weather entityWeather = new Weather();
                            entityWeather.setWeatherConditionCode(String.valueOf(weather.getWeatherCode()));
                            entityWeather.setDescription(weather.getWeatherDescription());
                            entityWeather.setIcon(weather.getWeatherIconName());
                            if (forecast.hasMainInstance()) {
                                entityWeather.setTemperature((float) Math.round((forecast.getMainInstance().getTemperature() - 32) / (float) 1.8));
                            }
                            entityForecast.setWeather(entityWeather);
                            em.persist(entityForecast);
                            em.flush();
                            LOGGER.log(Level.INFO, entityForecast.toString());
                        }
                    }
                }
            } else {
                LOGGER.log(Level.WARN, "No Hourly Forecast Data Available for: " + location);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, ex);
        }

    }

    @Override
    public void addDailyForecasts(String location) {
        OpenWeatherMap owm = new OpenWeatherMap("92a7c7673050d5da5c850a8c1cb995b7");
        try {
            DailyForecast df = owm.dailyForecastByCityName(location, Byte.parseByte("16"));
            if (df.isValid()) {
                for (int i = 0; i < df.getForecastCount(); i++) {
                    DailyForecast.Forecast forecast = df.getForecastInstance(i);
                    if (forecast.hasWeatherInstance()) {
                        for (int j = 0; j < forecast.getWeatherCount(); j++) {
                            HourlyForecast.Forecast.Weather weather = forecast.getWeatherInstance(j);
                            Forecast entityForecast = new Forecast();
                            if (df.hasCityInstance()) {
                                if (df.getCityInstance().getCountryCode().length() <= 2) {
                                    entityForecast.setLocation(df.getCityInstance().getCityName() + ", " + df.getCityInstance().getCountryCode());
                                } else {
                                    String countryCode = CountryCode.findByName(df.getCityInstance().getCountryCode()).get(0).name();
                                    if (countryCode != null) {
                                        entityForecast.setLocation(df.getCityInstance().getCityName() + ", " + countryCode);
                                    }
                                }
                                entityForecast.setLatitude(df.getCityInstance().getCoordInstance().getLatitude());
                                entityForecast.setLongitude(df.getCityInstance().getCoordInstance().getLongitude());
                            }
                            entityForecast.setCreationDate(java.util.Calendar.getInstance());
                            java.util.Calendar date = Calendar.getInstance();
                            date.setTime(forecast.getDateTime());
                            entityForecast.setForecastDate(date);
                            Weather entityWeather = new Weather();
                            entityWeather.setWeatherConditionCode(String.valueOf(weather.getWeatherCode()));
                            entityWeather.setDescription(weather.getWeatherDescription());
                            entityWeather.setIcon(weather.getWeatherIconName());
                            entityWeather.setTemperature((float) Math.round((((forecast.getTemperatureInstance().getMaximumTemperature() + forecast.getTemperatureInstance().getMinimumTemperature()) / 2) - 32) / (float) 1.8));
                            entityForecast.setWeather(entityWeather);
                            em.persist(entityForecast);
                            em.flush();
                            LOGGER.log(Level.INFO, entityForecast.toString());
                        }
                    }
                }
            } else {
                LOGGER.log(Level.WARN, "No Daily Forecast Data Available for: " + location);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, ex);
        }

    }

    @Override
    public ForecastDTO getForecast(long forecastID) {
        Forecast forecastEntity = em.find(Forecast.class, forecastID);
        ForecastDTO forecast = null;
        if (forecastEntity != null) {
            forecast = mapForecastToForecastDTO(forecastEntity);
            LOGGER.log(Level.INFO, forecast.toString());
        }
        return forecast;
    }

    @Override
    public void removeOldForecast() {
        java.util.Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        TypedQuery<Forecast> query = em.createNamedQuery(Forecast.FIND_OLD_FORECAST,
                Forecast.class);
        query.setParameter("today", today);
        List<Event> oldForecastEvent = new ArrayList<>();
        for (Forecast oldForecast : query.getResultList()) {
            LOGGER.log(Level.INFO, "REMOVED: " + oldForecast.toString());
            TypedQuery<Event> q = em.createNamedQuery(Event.FIND_BY_FORECAST,
                    Event.class);
            q.setParameter("forecast", oldForecast);
            oldForecastEvent = q.getResultList();
            for (Event event : oldForecastEvent) {
                if (event.getStartDate().after(today)) {
                    event.setForecast(null);
                    em.merge(event);
                }

            }
            if (oldForecastEvent.isEmpty()) {
                em.remove(oldForecast);
                em.flush();
            }
        }
        // UPDATE EVENT FORECAST
        for (Event event : oldForecastEvent) {
            ForecastDTO forecastDTO = getForecast(event.getLocation(), event.getStartDate().getTime());
            if (forecastDTO != null) {
                event.setForecast(em.find(Forecast.class, forecastDTO.getId()));
            } else {
                event.setForecast(null);
                // FORCE CHECK OWM
                Calendar fiveDays = Calendar.getInstance();
                fiveDays.add(Calendar.DATE, 5);
                if (event.getStartDate().after(fiveDays.getTime())) {
                    addDailyForecasts(event.getLocation());
                } else {
                    addHourlyForecasts(event.getLocation());
                }

            }
            em.merge(event);
            em.flush();
        }

    }

    /**
     * Method that map the Forecast class to ForecastDTO class
     *
     * @param forecastEntity the needed to map class
     * @return ForecastDTO class
     */
    private ForecastDTO mapForecastToForecastDTO(Forecast forecastEntity) {

        ForecastDTO forecast = new ForecastDTO();
        if (forecastEntity != null) {
            // SETUP FORECAST
            forecast.setId(forecastEntity.getId());
            forecast.setCreationDate(forecastEntity.getCreationDate());
            forecast.setDate(forecastEntity.getForecastDate());
            forecast.setLatitude(forecastEntity.getLatitude());
            forecast.setLongitude(forecastEntity.getLongitude());
            forecast.setLocation(forecastEntity.getLocation());
            // SETUP WEATHER
            WeatherDTO weather = new WeatherDTO();
            if (forecastEntity.getWeather() != null) {
                weather.setId(forecastEntity.getWeather().getId());
                weather.setDescription(forecastEntity.getWeather().getDescription());
                weather.setIcon(forecastEntity.getWeather().getIcon());
                weather.setTemperature(forecastEntity.getWeather().getTemperature());
                weather.setWeatherConditionCode(forecastEntity.getWeather().getWeatherConditionCode());
                forecast.setWeather(weather);
            }
            return forecast;
        }
        return null;

    }

    @Override
    public void setLocations() {
        List<String> locations = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new URL("http://openweathermap.org/help/city_list.txt").openStream();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.ERROR, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, ex);
        }
        CsvPreference pref = new CsvPreference.Builder('\"', '\t', "\n").build();
        ICsvMapReader reader = new CsvMapReader(new InputStreamReader(inputStream), pref);

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> result;
        try {
            while ((result = reader.read(new String[]{"id", "name", "lat", "lon", "countryCode"})) != null) {
                list.add(result);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, ex);
        }

        for (Map<String, String> elem : list) {
            Location location = new Location();
            try {
                locations.add(elem.get("name"));

                location.setId(Long.valueOf(elem.get("id")));
                location.setName(elem.get("name"));
                location.setLatitude(Float.valueOf(elem.get("lat")));
                location.setLongitude(Float.valueOf(elem.get("lon")));
                location.setCountryCode(elem.get("countryCode"));
            } catch (NumberFormatException e) {
                LOGGER.log(Level.ERROR, e);
            }

            em.persist(location);
            em.flush();
        }
    }

    @Override
    public List<String> searchLocation(String queryString) {
        TypedQuery<Location> query = em.createNamedQuery(Location.FIND_BY_SEARCHQUERY, Location.class);
        query.setParameter("query", queryString + "%");

        Set<String> fooSet = new LinkedHashSet<>();
        if (!query.getResultList().isEmpty()) {

            for (Location location : query.getResultList()) {
                fooSet.add(location.getName() + ", " + location.getCountryCode());
            }
        }

        return new ArrayList<>(fooSet);
    }

    private ForecastDTO nearestForecast(Date date, List<Forecast> availableForecasts) {
        // FORECAST INFORMATION FIND IN THE DB
        Forecast forecastEntity = null;
        int hourDay = 24;
        for (Forecast forecastNearest : availableForecasts) {
            // FIND THE NEAREST FORECAST INFORMATION
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(date);
            if (forecastNearest.getForecastDate().get(Calendar.DATE) == eventDate.get(Calendar.DATE) && Math.abs(forecastNearest.getForecastDate().get(Calendar.HOUR_OF_DAY) - eventDate.get(Calendar.HOUR_OF_DAY)) < hourDay) {
                hourDay = Math.abs(forecastNearest.getForecastDate().get(Calendar.HOUR_OF_DAY) - eventDate.get(Calendar.HOUR_OF_DAY));
                forecastEntity = forecastNearest;
            }
        }
        ForecastDTO forecast = mapForecastToForecastDTO(forecastEntity);
        if (forecast != null) {
            LOGGER.log(Level.INFO, forecast.toString());
        }
        return forecast;
    }

}
