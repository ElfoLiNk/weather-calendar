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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * <p>
 * Lets you access data from OpenWeatherMap.org using its Web APIs. Henceforth,
 * it's shortened as OWM.org to ease commenting.
 * </p>
 * <p>
 * <b>Sample code:</b><br>
 * <code>OpenWeatherMap.org owm = new OpenWeatherMap("your-api-key");</code><br>
 * <code>OpenWeatherMap.org owm = new OpenWeatherMap(your-units, "your-api-key");</code><br>
 * <code>OpenWeatherMap.org owm = new OpenWeatherMap(your-units, your-language, "your-api-key");</code>
 * </p>
 *
 * @author Ashutosh Kumar Singh <me@aksingh.net>
 * @version 12/19/2014
 * @see <a href="http://openweathermap.org/">OpenWeatherMap.org</a>
 * @see <a href="http://openweathermap.org/api">OpenWeatherMap.org API</a>
 * @since 2.5.0.1
 */
public class OpenWeatherMap {
    /*
     URLs and parameters for OWM.org
     */

    private static final String URL_API = "http://api.openweathermap.org/data/2.5/";
    private static final String URL_CURRENT = "weather?";
    private static final String URL_HOURLY_FORECAST = "forecast?";
    private static final String URL_DAILY_FORECAST = "forecast/daily?";

    private static final String PARAM_COUNT = "cnt=";
    private static final String PARAM_CITY_NAME = "q=";
    private static final String PARAM_CITY_ID = "id=";
    private static final String PARAM_LATITUDE = "lat=";
    private static final String PARAM_LONGITUDE = "lon=";
    private static final String PARAM_MODE = "mode=";
    private static final String PARAM_UNITS = "units=";
    private static final String PARAM_APPID = "appId=";
    private static final String PARAM_LANG = "unit=";

    /*
     Instance Variables
     */
    private final OWMAddress owmAddress;
    private final OWMResponse owmResponse;

    private static final Logger LOGGER = LogManager.getLogger(OpenWeatherMap.class.getName());

    /**
     * Constructor
     *
     * @param apiKey API key from OWM.org
     * @see <a href="http://openweathermap.org/appid">OWM.org API Key</a>
     */
    public OpenWeatherMap(String apiKey) {
        this(Units.IMPERIAL, Language.ENGLISH, apiKey);
    }

    /**
     * Constructor
     *
     * @param units Any constant from Units
     * @param apiKey API key from OWM.org
     * @see net.aksingh.owmjapis.OpenWeatherMap.Units
     * @see <a href="http://openweathermap.org/appid">OWM.org API Key</a>
     */
    public OpenWeatherMap(Units units, String apiKey) {
        this(units, Language.ENGLISH, apiKey);
    }

    /**
     * Constructor
     *
     * @param units Any constant from Units
     * @param lang Any constant from Language
     * @param apiKey API key from OWM.org
     * @see net.aksingh.owmjapis.OpenWeatherMap.Units
     * @see net.aksingh.owmjapis.OpenWeatherMap.Language
     * @see <a href="http://openweathermap.org/current#multi">OWM.org's
     * Multilingual support</a>
     * @see <a href="http://openweathermap.org/appid">OWM.org's API Key</a>
     */
    public OpenWeatherMap(Units units, Language lang, String apiKey) {
        this.owmAddress = new OWMAddress(units, lang, apiKey);
        this.owmResponse = new OWMResponse(owmAddress);
    }

    /*
     Getters
     */
    public OWMAddress getOwmAddressInstance() {
        return owmAddress;
    }

    public String getApiKey() {
        return owmAddress.getAppId();
    }

    public Units getUnits() {
        return owmAddress.getUnits();
    }

    public String getMode() {
        return owmAddress.getMode();
    }

    public Language getLang() {
        return owmAddress.getLang();
    }

    /*
     Setters
     */
    /**
     * Set units for getting data from OWM.org
     *
     * @param units Any constant from Units
     * @see net.aksingh.owmjapis.OpenWeatherMap.Units
     */
    public void setUnits(Units units) {
        owmAddress.setUnits(units);
    }

    /**
     * Set API key for getting data from OWM.org
     *
     * @param appId API key from OWM.org
     * @see <a href="http://openweathermap.org/appid">OWM.org's API Key</a>
     */
    public void setApiKey(String appId) {
        owmAddress.setAppId(appId);
    }

    /**
     * Set language for getting data from OWM.org
     *
     * @param lang Any constant from Language
     * @see net.aksingh.owmjapis.OpenWeatherMap.Language
     * @see <a href="http://openweathermap.org/current#multi">OWM.org's
     * Multilingual support</a>
     */
    public void setLang(Language lang) {
        owmAddress.setLang(lang);
    }

    public CurrentWeather currentWeatherByCityName(String cityName)
            throws IOException {
        String response = owmResponse.currentWeatherByCityName(cityName);
        return this.currentWeatherFromRawResponse(response);
    }

    public CurrentWeather currentWeatherByCityName(String cityName, String countryCode)
            throws IOException {
        String response = owmResponse.currentWeatherByCityName(cityName, countryCode);
        return this.currentWeatherFromRawResponse(response);
    }

    public CurrentWeather currentWeatherByCityCode(long cityCode) {
        String response = owmResponse.currentWeatherByCityCode(cityCode);
        return this.currentWeatherFromRawResponse(response);
    }

    public CurrentWeather currentWeatherByCoordinates(float latitude, float longitude) {
        String response = owmResponse.currentWeatherByCoordinates(latitude, longitude);
        return this.currentWeatherFromRawResponse(response);
    }

    public CurrentWeather currentWeatherFromRawResponse(String response) {
        JSONObject jsonObj = (response != null) ? new JSONObject(response) : null;
        return new CurrentWeather(jsonObj);
    }

    public HourlyForecast hourlyForecastByCityName(String cityName)
            throws IOException {
        String response = owmResponse.hourlyForecastByCityName(cityName);
        return this.hourlyForecastFromRawResponse(response);
    }

    public HourlyForecast hourlyForecastByCityName(String cityName, String countryCode)
            throws IOException {
        String response = owmResponse.hourlyForecastByCityName(cityName, countryCode);
        return this.hourlyForecastFromRawResponse(response);
    }

    public HourlyForecast hourlyForecastByCityCode(long cityCode) {
        String response = owmResponse.hourlyForecastByCityCode(cityCode);
        return this.hourlyForecastFromRawResponse(response);
    }

    public HourlyForecast hourlyForecastByCoordinates(float latitude, float longitude) {
        String response = owmResponse.hourlyForecastByCoordinates(latitude, longitude);
        return this.hourlyForecastFromRawResponse(response);
    }

    public HourlyForecast hourlyForecastFromRawResponse(String response) {
        JSONObject jsonObj = (response != null) ? new JSONObject(response) : null;
        return new HourlyForecast(jsonObj);
    }

    public DailyForecast dailyForecastByCityName(String cityName, byte count)
            throws IOException {
        String response = owmResponse.dailyForecastByCityName(cityName, count);
        return this.dailyForecastFromRawResponse(response);
    }

    public DailyForecast dailyForecastByCityName(String cityName, String countryCode, byte count)
            throws IOException {
        String response = owmResponse.dailyForecastByCityName(cityName, countryCode, count);
        return this.dailyForecastFromRawResponse(response);
    }

    public DailyForecast dailyForecastByCityCode(long cityCode, byte count) {
        String response = owmResponse.dailyForecastByCityCode(cityCode, count);
        return this.dailyForecastFromRawResponse(response);
    }

    public DailyForecast dailyForecastByCoordinates(float latitude, float longitude, byte count) {
        String response = owmResponse.dailyForecastByCoordinates(latitude, longitude, count);
        return this.dailyForecastFromRawResponse(response);
    }

    public DailyForecast dailyForecastFromRawResponse(String response) {
        JSONObject jsonObj = (response != null) ? new JSONObject(response) : null;
        return new DailyForecast(jsonObj);
    }

    /**
     * Units that can be set for getting data from OWM.org
     *
     * @since 2.5.0.3
     */
    public static enum Units {

        METRIC("metric"),
        IMPERIAL("imperial");

        private final String unit;

        Units(String unit) {
            this.unit = unit;
        }
    }

    /**
     * Languages that can be set for getting data from OWM.org
     *
     * @since 2.5.0.3
     */
    public static enum Language {

        ENGLISH("en"),
        RUSSIAN("ru"),
        ITALIAN("it"),
        SPANISH("es"),
        UKRAINIAN("uk"),
        GERMAN("de"),
        PORTUGUESE("pt"),
        ROMANIAN("ro"),
        POLISH("pl"),
        FINNISH("fi"),
        DUTCH("nl"),
        FRENCH("FR"),
        BULGARIAN("bg"),
        SWEDISH("sv"),
        CHINESE_TRADITIONAL("zh_tw"),
        CHINESE_SIMPLIFIED("zh"),
        TURKISH("tr"),
        CROATIAN("hr"),
        CATALAN("ca");

        private final String lang;

        Language(String lang) {
            this.lang = lang;
        }
    }

    /**
     * Generates addresses for accessing the information from OWM.org
     *
     * @since 2.5.0.3
     */
    public static class OWMAddress {

        private static final String MODE = "json";
        private static final String ENCODING = "UTF-8";

        private String mode;
        private Units units;
        private String appId;
        private Language lang;

        /*
         Constructors
         */
        private OWMAddress(String appId) {
            this(Units.IMPERIAL, Language.ENGLISH, appId);
        }

        private OWMAddress(Units units, String appId) {
            this(units, Language.ENGLISH, appId);
        }

        private OWMAddress(Units units, Language lang, String appId) {
            this.mode = MODE;
            this.units = units;
            this.lang = lang;
            this.appId = appId;
        }

        /*
         Getters
         */
        private String getAppId() {
            return this.appId;
        }

        private Units getUnits() {
            return this.units;
        }

        private String getMode() {
            return this.mode;
        }

        private Language getLang() {
            return this.lang;
        }

        /*
         Setters
         */
        private void setUnits(Units units) {
            this.units = units;
        }

        private void setAppId(String appId) {
            this.appId = appId;
        }

        private void setLang(Language lang) {
            this.lang = lang;
        }

        /*
         Addresses for current weather
         */
        public String currentWeatherByCityName(String cityName) throws UnsupportedEncodingException {
            return new StringBuilder()
                    .append(URL_API).append(URL_CURRENT)
                    .append(PARAM_CITY_NAME).append(URLEncoder.encode(cityName, ENCODING)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        public String currentWeatherByCityName(String cityName, String countryCode) throws UnsupportedEncodingException {
            return currentWeatherByCityName(new StringBuilder()
                    .append(cityName).append(",").append(countryCode)
                    .toString());
        }

        public String currentWeatherByCityCode(long cityCode) {
            return new StringBuilder()
                    .append(URL_API).append(URL_CURRENT)
                    .append(PARAM_CITY_ID).append(Long.toString(cityCode)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        public String currentWeatherByCoordinates(float latitude, float longitude) {
            return new StringBuilder()
                    .append(URL_API).append(URL_CURRENT)
                    .append(PARAM_LATITUDE).append(Float.toString(latitude)).append("&")
                    .append(PARAM_LONGITUDE).append(Float.toString(longitude)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        /*
         Addresses for hourly forecasts
         */
        public String hourlyForecastByCityName(String cityName) throws UnsupportedEncodingException {
            return new StringBuilder()
                    .append(URL_API).append(URL_HOURLY_FORECAST)
                    .append(PARAM_CITY_NAME).append(URLEncoder.encode(cityName, ENCODING)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        public String hourlyForecastByCityName(String cityName, String countryCode) throws UnsupportedEncodingException {
            return hourlyForecastByCityName(new StringBuilder()
                    .append(cityName).append(",").append(countryCode)
                    .toString());
        }

        public String hourlyForecastByCityCode(long cityCode) {
            return new StringBuilder()
                    .append(URL_API).append(URL_HOURLY_FORECAST)
                    .append(PARAM_CITY_ID).append(Long.toString(cityCode)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        public String hourlyForecastByCoordinates(float latitude, float longitude) {
            return new StringBuilder()
                    .append(URL_API).append(URL_HOURLY_FORECAST)
                    .append(PARAM_LATITUDE).append(Float.toString(latitude)).append("&")
                    .append(PARAM_LONGITUDE).append(Float.toString(longitude)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        /*
         Addresses for daily forecasts
         */
        public String dailyForecastByCityName(String cityName, byte count) throws UnsupportedEncodingException {
            return new StringBuilder()
                    .append(URL_API).append(URL_DAILY_FORECAST)
                    .append(PARAM_CITY_NAME).append(URLEncoder.encode(cityName, ENCODING)).append("&")
                    .append(PARAM_COUNT).append(Byte.toString(count)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        public String dailyForecastByCityName(String cityName, String countryCode, byte count) throws UnsupportedEncodingException {
            return dailyForecastByCityName(new StringBuilder()
                    .append(cityName).append(",").append(countryCode)
                    .toString(), count);
        }

        public String dailyForecastByCityCode(long cityCode, byte count) {
            return new StringBuilder()
                    .append(URL_API).append(URL_DAILY_FORECAST)
                    .append(PARAM_CITY_ID).append(Long.toString(cityCode)).append("&")
                    .append(PARAM_COUNT).append(Byte.toString(count)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }

        public String dailyForecastByCoordinates(float latitude, float longitude, byte count) {
            return new StringBuilder()
                    .append(URL_API).append(URL_DAILY_FORECAST)
                    .append(PARAM_LATITUDE).append(Float.toString(latitude)).append("&")
                    .append(PARAM_LONGITUDE).append(Float.toString(longitude)).append("&")
                    .append(PARAM_COUNT).append(Byte.toString(count)).append("&")
                    .append(PARAM_MODE).append(this.mode).append("&")
                    .append(PARAM_UNITS).append(this.units).append("&")
                    .append(PARAM_LANG).append(this.lang).append("&")
                    .append(PARAM_APPID).append(this.appId)
                    .toString();
        }
    }

    /**
     * Requests OWM.org for data and provides back the incoming response.
     *
     * @since 2.5.0.3
     */
    private static class OWMResponse {

        private final OWMAddress owmAddress;

        private OWMResponse(OWMAddress owmAddress) {
            this.owmAddress = owmAddress;
        }

        /*
         Responses for current weather
         */
        public String currentWeatherByCityName(String cityName) throws UnsupportedEncodingException {
            String address = owmAddress.currentWeatherByCityName(cityName);
            return httpGET(address);
        }

        public String currentWeatherByCityName(String cityName, String countryCode) throws UnsupportedEncodingException {
            String address = owmAddress.currentWeatherByCityName(cityName, countryCode);
            return httpGET(address);
        }

        public String currentWeatherByCityCode(long cityCode) {
            String address = owmAddress.currentWeatherByCityCode(cityCode);
            return httpGET(address);
        }

        public String currentWeatherByCoordinates(float latitude, float longitude) {
            String address = owmAddress.currentWeatherByCoordinates(latitude, longitude);
            return httpGET(address);
        }

        /*
         Responses for daily forecasts
         */
        public String hourlyForecastByCityName(String cityName) throws UnsupportedEncodingException {
            String address = owmAddress.hourlyForecastByCityName(cityName);
            return httpGET(address);
        }

        public String hourlyForecastByCityName(String cityName, String countryCode) throws UnsupportedEncodingException {
            String address = owmAddress.hourlyForecastByCityName(cityName, countryCode);
            return httpGET(address);
        }

        public String hourlyForecastByCityCode(long cityCode) {
            String address = owmAddress.hourlyForecastByCityCode(cityCode);
            return httpGET(address);
        }

        public String hourlyForecastByCoordinates(float latitude, float longitude) {
            String address = owmAddress.hourlyForecastByCoordinates(latitude, longitude);
            return httpGET(address);
        }

        /*
         Responses for daily forecasts
         */
        public String dailyForecastByCityName(String cityName, byte count) throws UnsupportedEncodingException {
            String address = owmAddress.dailyForecastByCityName(cityName, count);
            return httpGET(address);
        }

        public String dailyForecastByCityName(String cityName, String countryCode, byte count) throws UnsupportedEncodingException {
            String address = owmAddress.dailyForecastByCityName(cityName, countryCode, count);
            return httpGET(address);
        }

        public String dailyForecastByCityCode(long cityCode, byte count) {
            String address = owmAddress.dailyForecastByCityCode(cityCode, count);
            return httpGET(address);
        }

        public String dailyForecastByCoordinates(float latitude, float longitude, byte count) {
            String address = owmAddress.dailyForecastByCoordinates(latitude, longitude, count);
            return httpGET(address);
        }

        /**
         * Implements HTTP's GET method
         *
         * @param requestAddress Address to be loaded
         * @return Response if successful, else <code>null</code>
         * @see
         * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">HTTP
         * - (9.3) GET</a>
         */
        private String httpGET(String requestAddress) {
            URL request;
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            String s;
            String response = null;

            try {
                request = new URL(requestAddress);
                connection = (HttpURLConnection) request.openConnection();

                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(false);
                //allow both GZip and Deflate (ZLib) encodings
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.connect();
                //obtain the encoding returned by the server
                String encoding = connection.getContentEncoding();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    try {
                        if (encoding != null && "gzip".equalsIgnoreCase(encoding)) {
                            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
                        } else if (encoding != null && "deflate".equalsIgnoreCase(encoding)) {
                            reader = new BufferedReader(new InputStreamReader(new InflaterInputStream(connection.getInputStream(), new Inflater(true))));
                        } else {
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        }
                        while ((s = reader.readLine()) != null) {
                            response = s;
                        }

                    } catch (IOException e) {
                        LOGGER.log(Level.ERROR, e);
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                LOGGER.log(Level.ERROR, e);
                            }
                        }
                    }

                } else {
                    try {
                        reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        while ((s = reader.readLine()) != null) {
                            response = s;
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.ERROR, e);
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                LOGGER.log(Level.ERROR, e);
                            }
                        }
                    }

                    // if response is bad
                    LOGGER.log(Level.ERROR, "Bad Response: " + response);
                    return null;
                }
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e);
                response = null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

            }

            return response;
        }
    }
}
