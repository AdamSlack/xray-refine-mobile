package org.sociam.koalahero.xray;

import java.util.ArrayList;
import org.apache.commons.lang3.text.WordUtils;

public enum AppGenre {
    UNKNOWN,
    EVENTS,
    COMICS,
    PRODUCTIVITY,
    WEATHER,
    SHOPPING,
    GAME_MUSIC,
    FINANCE,
    DATING,
    PARENTING,
    MEDICAL,
    MUSIC_AND_AUDIO,
    PERSONALIZATION,
    BUSINESS,
    GAME_CARD,
    GAME_SIMULATION,
    MAPS_AND_NAVIGATION,
    GAME_ARCADE,
    AUTO_AND_VEHICLES,
    GAME_RACING,
    ENTERTAINMENT,
    LIFESTYLE,
    SPORTS,
    GAME_ADVENTURE,
    VIDEO_PLAYERS,
    GAME_BOARD,
    TOOLS,
    GAME_SPORTS,
    GAME_TRIVIA,
    GAME_CASUAL,
    COMMUNICATION,
    FOOD_AND_DRINK,
    LIBRARIES_AND_DEMO,
    BOOKS_AND_REFERENCE,
    GAME_ROLE_PLAYING,
    HOUSE_AND_HOME,
    ART_AND_DESIGN,
    GAME_CASINO,
    HEALTH_AND_FITNESS,
    EDUCATION,
    TRAVEL_AND_LOCAL,
    BEAUTY,
    GAME_ACTION,
    PHOTOGRAPHY,
    GAME_PUZZLE,
    GAME_STRATEGY,
    GAME_WORD,
    NEWS_AND_MAGAZINES,
    SOCIAL,
    GAME_EDUCATIONAL;

    public String toLabel() {
        String str = this.toString();

        if(str.startsWith("GAME_")) {
            str = str.replace("GAME_", "");
            str = str + "_GAME";
        }

        str = str.replace("_", " ");
        str = WordUtils.capitalizeFully(str);
        str = str.replace("And", "and");

        return str;
    }
}
