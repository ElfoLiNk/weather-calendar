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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <p>
 * Parses daily forecast data and provides methods to get/access the same
 * information. This class provides <code>has</code> and <code>get</code>
 * methods to access the information.
 * </p>
 * <p>
 * <code>has</code> methods can be used to check if the data exists, i.e., if
 * the data was available (successfully downloaded) and was parsed correctly.
 * <code>get</code> methods can be used to access the data, if the data exists,
 * otherwise <code>get</code> methods will give value as per following basis:
 * Boolean: <code>false</code> Integral: Minimum value (MIN_VALUE) Floating
 * point: Not a number (NaN) Others: <code>null</code>
 * </p>
 *
 * @author Ashutosh Kumar Singh
 * @version 2014/12/27
 * @see <a href="http://openweathermap.org/forecast">OWM's Weather Forecast
 * API</a>
 * @since 2.5.0.3
 */
public class DailyForecast extends AbstractForecast {
    /*
     Instance variables
     */

    private final List<Forecast> forecastList;

    /*
     Constructors
     */
    DailyForecast(JSONObject jsonObj) {
        super(jsonObj);

        JSONArray dataArray = (jsonObj != null) ? jsonObj.optJSONArray(DailyForecast.JSON_FORECAST_LIST) : new JSONArray();
        this.forecastList = (dataArray != null) ? new ArrayList<>(dataArray.length()) : Collections.emptyList();
        if (this.forecastList != Collections.<Forecast>emptyList()) {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject forecastObj = dataArray.optJSONObject(i);
                if (forecastObj != null) {
                    this.forecastList.add(new Forecast(forecastObj));
                }
            }
        }
    }

    /**
     * @param index Index of Forecast instance in the list.
     * @return Forecast instance if available, otherwise <code>null</code>.
     */
    public Forecast getForecastInstance(int index) {
        return this.forecastList.get(index);
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
     */
    public static class Forecast extends AbstractForecast.Forecast {
        /*
         JSON Keys
         */

        /**
         *
         */
        

        public static final String JSON_TEMP = "temp";

        private static final String JSON_FORECAST_PRESSURE = "pressure";
        private static final String JSON_FORECAST_HUMIDITY = "humidity";
        private static final String JSON_FORECAST_WIND_SPEED = "speed";
        private static final String JSON_FORECAST_WIND_DEGREE = "deg";
        private static final String JSON_FORECAST_CLOUDS = "clouds";
        private static final String JSON_FORECAST_RAIN = "rain";
        private static final String JSON_FORECAST_SNOW = "snow";

        /*
         Instance Variables
         */
        private final float pressure;
        private final float humidity;
        private final float windSpeed;
        private final float windDegree;
        private final float cloudsPercent;
        private final float rain;
        private final float snow;

        private final Temperature temp;

        /*
         Constructors
         */
        Forecast() {
            super();

            this.pressure = Float.NaN;
            this.humidity = Float.NaN;
            this.windSpeed = Float.NaN;
            this.windDegree = Float.NaN;
            this.cloudsPercent = Float.NaN;
            this.rain = Float.NaN;
            this.snow = Float.NaN;

            this.temp = new Temperature();
        }

        Forecast(JSONObject jsonObj) {
            super(jsonObj);

            JSONObject jsonObjTemp = (jsonObj != null) ? jsonObj.optJSONObject(Forecast.JSON_TEMP) : null;
            this.temp = (jsonObjTemp != null) ? new Temperature(jsonObjTemp) : new Temperature();

            this.humidity = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_HUMIDITY, Double.NaN) : Float.NaN;
            this.pressure = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_PRESSURE, Double.NaN) : Float.NaN;
            this.windSpeed = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_WIND_SPEED, Double.NaN) : Float.NaN;
            this.windDegree = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_WIND_DEGREE, Double.NaN) : Float.NaN;
            this.cloudsPercent = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_CLOUDS, Double.NaN) : Float.NaN;
            this.rain = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_RAIN, Double.NaN) : Float.NaN;
            this.snow = (jsonObj != null) ? (float) jsonObj.optDouble(Forecast.JSON_FORECAST_SNOW, Double.NaN) : Float.NaN;
        }

        /**
         *
         * @return
         */
        public boolean hasHumidity() {
            return !Float.isNaN(this.humidity);
        }

        /**
         *
         * @return
         */
        public boolean hasPressure() {
            return !Float.isNaN(this.pressure);
        }

        /**
         *
         * @return
         */
        public boolean hasWindSpeed() {
            return !Float.isNaN(this.windSpeed);
        }

        /**
         *
         * @return
         */
        public boolean hasWindDegree() {
            return !Float.isNaN(this.windDegree);
        }

        /**
         *
         * @return
         */
        public boolean hasPercentageOfClouds() {
            return !Float.isNaN(this.cloudsPercent);
        }

        /**
         *
         * @return
         */
        public boolean hasRain() {
            return !Float.isNaN(this.rain);
        }

        /**
         *
         * @return
         */
        public boolean hasSnow() {
            return !Float.isNaN(this.snow);
        }

        /**
         *
         * @return
         */
        public float getHumidity() {
            return this.humidity;
        }

        /**
         *
         * @return
         */
        public float getPressure() {
            return this.pressure;
        }

        /**
         *
         * @return
         */
        public float getWindSpeed() {
            return this.windSpeed;
        }

        /**
         *
         * @return
         */
        public float getWindDegree() {
            return this.windDegree;
        }

        /**
         *
         * @return
         */
        public float getPercentageOfClouds() {
            return this.cloudsPercent;
        }

        /**
         *
         * @return
         */
        public float getRain() {
            return this.rain;
        }

        /**
         *
         * @return
         */
        public float getSnow() {
            return this.snow;
        }

        /**
         *
         * @return
         */
        public Temperature getTemperatureInstance() {
            return this.temp;
        }

        /**
         * <p>
         * Parses temperature data and provides methods to get/access the same
         * information. This class provides <code>has</code> and
         * <code>get</code> methods to access the information.
         * </p>
         * <p>
         * <code>has</code> methods can be used to check if the data exists,
         * i.e., if the data was available (successfully downloaded) and was
         * parsed correctly. <code>get</code> methods can be used to access the
         * data, if the data exists, otherwise <code>get</code> methods will
         * give value as per following basis: Boolean: <code>false</code>
         * Integral: Minimum value (MIN_VALUE) Floating point: Not a number
         * (NaN) Others: <code>null</code>
         * </p>
         */
        public static class Temperature {

            /**
             *
             */
            public static final String JSON_TEMP_DAY = "day";

            /**
             *
             */
            public static final String JSON_TEMP_MIN = "min";

            /**
             *
             */
            public static final String JSON_TEMP_MAX = "max";

            /**
             *
             */
            public static final String JSON_TEMP_NIGHT = "night";

            /**
             *
             */
            public static final String JSON_TEMP_EVENING = "eve";

            /**
             *
             */
            public static final String JSON_TEMP_MORNING = "morn";

            private final float dayTemp;
            private final float minTemp;
            private final float maxTemp;
            private final float nightTemp;
            private final float eveTemp;
            private final float mornTemp;

            Temperature() {
                this.dayTemp = Float.NaN;
                this.minTemp = Float.NaN;
                this.maxTemp = Float.NaN;
                this.nightTemp = Float.NaN;
                this.eveTemp = Float.NaN;
                this.mornTemp = Float.NaN;
            }

            Temperature(JSONObject jsonObj) {
                this.dayTemp = (jsonObj != null) ? (float) jsonObj.optDouble(Temperature.JSON_TEMP_DAY, Double.NaN) : Float.NaN;
                this.minTemp = (jsonObj != null) ? (float) jsonObj.optDouble(Temperature.JSON_TEMP_MIN, Double.NaN) : Float.NaN;
                this.maxTemp = (jsonObj != null) ? (float) jsonObj.optDouble(Temperature.JSON_TEMP_MAX, Double.NaN) : Float.NaN;
                this.nightTemp = (jsonObj != null) ? (float) jsonObj.optDouble(Temperature.JSON_TEMP_NIGHT, Double.NaN) : Float.NaN;
                this.eveTemp = (jsonObj != null) ? (float) jsonObj.optDouble(Temperature.JSON_TEMP_EVENING, Double.NaN) : Float.NaN;
                this.mornTemp = (jsonObj != null) ? (float) jsonObj.optDouble(Temperature.JSON_TEMP_MORNING, Double.NaN) : Float.NaN;
            }

            /**
             *
             * @return
             */
            public boolean hasDayTemperature() {
                return !Float.isNaN(this.dayTemp);
            }

            /**
             *
             * @return
             */
            public boolean hasMinimumTemperature() {
                return !Float.isNaN(this.minTemp);
            }

            /**
             *
             * @return
             */
            public boolean hasMaximumTemperature() {
                return !Float.isNaN(this.maxTemp);
            }

            /**
             *
             * @return
             */
            public boolean hasNightTemperature() {
                return !Float.isNaN(this.nightTemp);
            }

            /**
             *
             * @return
             */
            public boolean hasEveningTemperature() {
                return !Float.isNaN(this.eveTemp);
            }

            /**
             *
             * @return
             */
            public boolean hasMorningTemperature() {
                return !Float.isNaN(this.mornTemp);
            }

            /**
             *
             * @return
             */
            public float getDayTemperature() {
                return this.dayTemp;
            }

            /**
             *
             * @return
             */
            public float getMinimumTemperature() {
                return this.minTemp;
            }

            /**
             *
             * @return
             */
            public float getMaximumTemperature() {
                return this.maxTemp;
            }

            /**
             *
             * @return
             */
            public float getNightTemperature() {
                return this.nightTemp;
            }

            /**
             *
             * @return
             */
            public float getEveningTemperature() {
                return this.eveTemp;
            }

            /**
             *
             * @return
             */
            public float getMorningTemperature() {
                return this.mornTemp;
            }
        }
    }
}
