package org.sociam.koalahero.csm;

import java.util.ArrayList;
import java.util.HashMap;

public class CSMParentalGuidance {

    public HashMap<String, Guidance> guidanceCategories;
    private Guidance playability;
    private Guidance violence;
    private Guidance sex;
    private Guidance language;
    private Guidance consumerism;
    private Guidance drugs;
    private Guidance educational;

    public CSMParentalGuidance() {
        this.guidanceCategories = new HashMap<>();
        this.playability = new Guidance();
        this.violence = new Guidance();
        this.sex  = new Guidance();
        this.language = new Guidance();
        this.consumerism = new Guidance();
        this.drugs = new Guidance();
        this.educational = new Guidance();
    }

    public static class Guidance {
        public Guidance() {
            this.rating = -1;
            this.description = "";
            this.title = "";
        }
        public int rating;
        public String description;
        public String title;
        public GuidanceCategory category;
    }

    public void getGuidanceCategories(HashMap<String, Guidance> guidanceCategories) {
        this.guidanceCategories = guidanceCategories;
    }

    public void setConsumerism(Guidance consumerism) {
        consumerism.title = "Consumerism";
        consumerism.category = GuidanceCategory.CONSUMERISM;
        this.consumerism = consumerism;
        this.guidanceCategories.put(consumerism.title, consumerism);
    }

    public void setDrugs(Guidance drugs) {
        drugs.title = "Drugs";
        drugs.category = GuidanceCategory.DRUGS;
        this.drugs = drugs;
        this.guidanceCategories.put(drugs.title, drugs);
    }

    public void setEducational(Guidance educational) {
        educational.title = "Educational";
        educational.category = GuidanceCategory.EDUCATION;
        this.educational = educational;
        this.guidanceCategories.put(educational.title, educational);
    }

    public void setLanguage(Guidance language) {
        language.title = "Language";
        language.category = GuidanceCategory.LANGUAGE;
        this.language = language;
        this.guidanceCategories.put(language.title, language);
    }

    public void setPlayability(Guidance playability) {
        playability.title = "Playability";
        playability.category = GuidanceCategory.PLAYABILITY;
        this.playability = playability;
        this.guidanceCategories.put(playability.title, playability);
    }

    public void setSex(Guidance sex) {
        sex.title = "Sex";
        sex.category = GuidanceCategory.SEX;
        this.sex = sex;
        this.guidanceCategories.put(sex.title, sex);
    }

    public void setViolence(Guidance violence) {
        violence.title = "Violence";
        violence.category = GuidanceCategory.VIOLENCE;
        this.violence = violence;
        this.guidanceCategories.put(violence.title, violence);
    }
}
