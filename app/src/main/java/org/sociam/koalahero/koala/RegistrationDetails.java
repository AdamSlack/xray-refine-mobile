package org.sociam.koalahero.koala;

public class RegistrationDetails {
    public String study_id;
    public String password;

    public RegistrationDetails() {
        this.study_id = "";
        this.password = "";
    }

    public RegistrationDetails(String email, String password) {
        this.study_id = email;
        this.password = password;
    }
}
