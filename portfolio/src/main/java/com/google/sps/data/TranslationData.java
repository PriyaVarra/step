package com.google.sps.data;

import java.util.HashMap;

/**
 * Class for storing translation data for a country compatible with information
 * in "WEB-INF/countrylanguages.json". The JSON object is an array where each 
 * element is an object in the form {"country": countryName, "lat": latitude, 
 * "lng": longitude, "translations": {languageOneInfo: "", languageTwoInfo: "", ...}}
 * where country name is the name of the country in English, latitude and longitude 
 * correspond to the geographic center of the country, and for each major language 
 * spoken in the country, under "translations", there is a key, value pair where the 
 * key is a string in the form "languageIsoCode languageEnglishName" and the value is
 * an empty string. When text is sent to the servlet for translation, the value is
 * updated with the translation of the text in the language corresponding to the key.
 */
  
public final class TranslationData {

  private final String country;
  private final double lat;
  private final double lng;
  private HashMap<String, String> translations;

  /** Populates data for translation marker. */
  public TranslationData(String country, double lat, double lng) {
    this.country = country;
    this.lat = lat;
    this.lng = lng;
    this.translations = new HashMap<String, String>();
  }

  /** Returns name of country. */
  public String getCountry() {
    return country;
  }

  /** Returns latitude for geographic center of country. */
  public double getLat() {
    return lat;
  }

  /** Returns longitude of geographic center of country. */
  public double getLng() {
    return lng;
  }

  /** Returns map of language, translated text pairs for country. */
  public HashMap<String, String> getTranslations() {
    return translations;
  }
}
