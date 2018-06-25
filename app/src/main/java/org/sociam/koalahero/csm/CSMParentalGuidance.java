package org.sociam.koalahero.csm;

public class CSMParentalGuidance {

    public CSMParentalGuidance() {
        this.playability = new Guidance();
        this.violence = new Guidance();
        this.sex  = new Guidance();
        this.language = new Guidance();
        this.consumerism = new Guidance();
        this.drugs = new Guidance();
        this.educational = new Guidance();
    }

    public Guidance playability;
    public Guidance violence;
    public Guidance sex;
    public Guidance language;
    public Guidance consumerism;
    public Guidance drugs;
    public Guidance educational;

    public static class Guidance {
        public Guidance() {
            this.rating = -1;
            this.description = "";
        }
        public int rating;
        public String description;
    }
}
