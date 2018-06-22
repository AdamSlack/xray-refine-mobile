package org.sociam.koalahero.csm;

public class CSMParentalGuidance {
    public Guidance playability = new Guidance();
    public Guidance violence = new Guidance();
    public Guidance sex = new Guidance();
    public Guidance language = new Guidance();
    public Guidance consumerism = new Guidance();
    public Guidance drugs = new Guidance();
    public Guidance educational = new Guidance();

    public static class Guidance {
        public int rating = 0;
        public String description = "No Rating";
    }
}
