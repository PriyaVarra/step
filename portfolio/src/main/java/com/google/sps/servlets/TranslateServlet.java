// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.sps.data.TranslationData;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that translates text for languages in a majority of countries around the world. */
@WebServlet("/translate")
public class TranslateServlet extends HttpServlet {
 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter("text");
 
    InputStream inputStream = 
        getServletContext().getResourceAsStream("/WEB-INF/countrylanguages.json");

    Gson gson = new Gson();
    TranslationData[] translationsData = 
        gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), TranslationData[].class);

    translate(translationsData, text);

    // Write updated translation data to url
    String json = gson.toJson(translationsData);

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }

  /** Translates text to language identified by isoCode and stores result for all countries */
  private void translate(TranslationData[] translationsData, String text) {
    HashMap<String, String> translationCache = new HashMap<String, String>();

    Translate translate = TranslateOptions.getDefaultInstance().getService();

    for (TranslationData translationData : translationsData) {
      HashMap<String, String> translations = translationData.getTranslations();
      
      for (String languageKey : translations.keySet()) {
        // languageKey is in form "isoCode, englishName". Below statement extracts isoCode
        String isoCode = languageKey.split(",")[0];
        
        // If language hasn't been translated to yet, use Cloud Translation API
        if (!translationCache.containsKey(isoCode)) {
          Translation translation =
              translate.translate(text, Translate.TranslateOption.targetLanguage(isoCode));
          translationCache.put(isoCode, translation.getTranslatedText());
        } 
        
      translations.replace(languageKey, translationCache.get(isoCode)); 
      }
    } 
  }
 
}
