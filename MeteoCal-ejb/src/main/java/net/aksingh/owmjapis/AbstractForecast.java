/*
 * Copyright (c) 2013-2014 Ashutosh Kumar Singh <me@aksingh.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.aksingh.owmjapis;

import org.json.JSONObject;

/**
 * <p>
 * Provides default behaviours and implementations for: 1.
 * {@link net.aksingh.owmjapis.HourlyForecast} 2.
 * {@link net.aksingh.owmjapis.DailyForecast} It defines common methods like
 * <code>has</code>, <code>get</code> and some others.
 * </p>
 *
 * @author Ashutosh Kumar Singh
 * @version 2014/12/27
 * @since 2.5.0.3
 */
public abstract class AbstractForecast extends AbstractResponse {
    /*
     JSON Keys
     */

    static final String JSON_FORECAST_LIST = "list";

    /*
     Instance variables
     */
    private final double message;
    private final City city;
    private final int forecastCount;

    /*
     Constructors
     */
    AbstractForecast() {
        super();

        this.message = Double.NaN;
        this.forecastCount = 0;
        this.city = null;
    }

    AbstractForecast(JSONObject jsonObj) {
        super(jsonObj);

        final String jsonMessage = "message";
        final String jsonCity = "city";
        final String jsonForecastCount = "cnt";

        this.message = (jsonObj != null) ? jsonObj.optDouble(jsonMessage, Double.NaN) : Double.NaN;

        this.city = (jsonObj != null) ? new City(jsonObj.optJSONObject(jsonCity)) : null;

        this.forecastCount = (jsonObj != null) ? jsonObj.optInt(jsonForecastCount, 0) : 0;
    }

    /**
     * @return <code>true</code> if message is available, otherwise
     * <code>false</code>.
     */
    public boolean hasMessage() {
        return !Double.isNaN(this.message);
    }

    /**
     * @return <code>true</code> if count of forecasts is available, otherwise
     * <code>false</code>.
     */
    public boolean hasForecastCount() {
        return this.forecastCount != 0;
    }

    /**
     * @return <code>true</code> if message is available, otherwise
     * <code>false</code>.
     */
    public boolean hasCityInstance() {
        return this.city != null;
    }

    /**
     * @return Message if available, otherwise <code>Double.NaN</code>.
     */
    public double getMessage() {
        return this.message;
    }

    /**
     * @return Count of forecasts if available, otherwise <code>0</code>.
     */
    public int getForecastCount() {
        return this.forecastCount;
    }

    /**
     * @return City's instance if available, otherwise <code>null</code>.
     */
    public City getCityInstance() {
        return this.city;
    }

    /**
     * <p>
     * Provides default behaviours for City
     * </p>
     *
     * @author Ashutosh Kumar Singh
     */
    public static class City {

        private static final String JSON_CITY_ID = "id";
        private static final String JSON_CITY_NAME = "name";
        private static final String JSON_CITY_COUNTRY_CODE = "country";
        private static final String JSON_CITY_POPULATION = "population";
        private static final String JSON_CITY_COORD = "coord";

        private final long cityID;
        private final String cityName;
        private final String countryCode;
        private final long population;

        private final Coord coord;

        City() {
            this.cityID = Long.MIN_VALUE;
            this.cityName = null;
            this.countryCode = null;
            this.population = Long.MIN_VALUE;

            this.coord = new Coord();
        }

        City(JSONObject jsonObj) {
            this.cityID = (jsonObj != null) ? jsonObj.optLong(City.JSON_CITY_ID, Long.MIN_VALUE) : Long.MIN_VALUE;
            this.cityName = (jsonObj != null) ? jsonObj.optString(City.JSON_CITY_NAME, null) : null;
            this.countryCode = (jsonObj != null) ? jsonObj.optString(City.JSON_CITY_COUNTRY_CODE, null) : null;
            this.population = (jsonObj != null) ? jsonObj.optLong(City.JSON_CITY_POPULATION, Long.MIN_VALUE) : Long.MIN_VALUE;

            JSONObject jsonObjCoord = (jsonObj != null) ? jsonObj.optJSONObject(City.JSON_CITY_COORD) : null;
            this.coord = (jsonObjCoord != null) ? new Coord(jsonObjCoord) : null;
        }

        /**
         *
         * @return
         */
        public boolean hasCityCode() {
            return this.cityID != Long.MIN_VALUE;
        }

        /**
         *
         * @return
         */
        public boolean hasCityName() {
            return this.cityName != null;
        }

        /**
         *
         * @return
         */
        public boolean hasCountryCode() {
            return this.countryCode != null;
        }

        /**
         *
         * @return
         */
        public boolean hasCityPopulation() {
            return this.population != Long.MIN_VALUE;
        }

        /**
         * @return <code>true</code> if Coord instance is available, otherwise
         * <code>false</code>.
         */
        public boolean hasCoordInstance() {
            return coord != null;
        }

        /**
         *
         * @return
         */
        public long getCityCode() {
            return this.cityID;
        }

        /**
         *
         * @return
         */
        public String getCityName() {
            return this.cityName;
        }

        /**
         *
         * @return
         */
        public String getCountryCode() {
            return this.countryCode;
        }

        /**
         *
         * @return
         */
        public long getCityPopulation() {
            return this.population;
        }

        /**
         * @return Coord instance if available, otherwise <code>null</code>.
         */
        public Coord getCoordInstance() {
            return this.coord;
        }

        /**
         * 
         */
        public static class Coord extends AbstractWeather.Coord {

            Coord() {
                super();
            }

            Coord(JSONObject jsonObj) {
                super(jsonObj);
            }
        }
    }

    /**
     * <p>
     * Parses forecast data (one element in the forecastList) and provides
     * methods to get/access the same information. This class provides
     * <code>has</code> and <code>get</code> methods to access the information.
     * </p>
     * <p>
     * <code>has</code> methods can be used to check if the data exists, i.e.,
     * if the data was available (successfully downloaded) and was parsed
     * correctly. <code>get</code> methods can be used to access the data, if
     * the data exists, otherwise <code>get</code> methods will give value as
     * per following basis: Boolean: <code>false</code> Integral: Minimum value
     * (MIN_VALUE) Floating point: Not a number (NaN) Others: <code>null</code>
     * </p>
     *
     * @author Ashutosh Kumar Singh
     * @version 2014/12/27
     * @since 2.5.0.3
     */
    public abstract static class Forecast extends AbstractWeather {

        Forecast() {
            super();
        }

        Forecast(JSONObject jsonObj) {
            super(jsonObj);
        }
    }
}
