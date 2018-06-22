package org.sociam.koalahero.appsInspector;

public class AppsInspector {
    private static AppsInspector INSTANCE;

    private AppsInspector() {

    }

    public static AppsInspector getInstance() {
        if (INSTANCE == null) {
            return new AppsInspector();
        }
        return INSTANCE;
    }

}
