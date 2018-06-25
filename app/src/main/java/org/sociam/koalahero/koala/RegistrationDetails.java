package org.sociam.koalahero.koala;

public class RegistrationDetails {
    public String email;
    public String password;

    public RegistrationDetails() {
        this.email = "";
        this.password = "";
    }

    public RegistrationDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
