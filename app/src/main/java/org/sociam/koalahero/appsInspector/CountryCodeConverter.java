package org.sociam.koalahero.appsInspector;

import android.os.LocaleList;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Taken From.
// https://blog.oio.de/2010/12/31/mapping-iso2-and-iso3-country-codes-with-java/

public class CountryCodeConverter {

    public CountryCodeConverter() {
        initCountryCodeMapping();
    }

    private Map<String, Locale> localeMap;

    private void initCountryCodeMapping() {
        String[] countries = Locale.getISOCountries();
        localeMap = new HashMap<>(countries.length);
        for (String country : countries) {
            Locale locale = new Locale("", country);
            localeMap.put(locale.getISO3Country().toUpperCase(), locale);
        }
    }

    public String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
        Locale code = localeMap.get(iso3CountryCode);
        if(code == null) {
            return "";
        }
        return code.getCountry();
    }

    public String iso2CountryCodeToIso3CountryCode(String iso2CountryCode){
        Locale locale = new Locale("", iso2CountryCode);
        return locale.getISO3Country();
    }
}

