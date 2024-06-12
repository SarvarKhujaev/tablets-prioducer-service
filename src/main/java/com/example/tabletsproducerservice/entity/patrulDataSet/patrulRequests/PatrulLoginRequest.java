package com.example.tabletsproducerservice.entity.patrulDataSet.patrulRequests;

public final class PatrulLoginRequest {
    public String getSimCardNumber() {
        return this.simCardNumber;
    }

    public void setSimCardNumber( final String simCardNumber ) {
        this.simCardNumber = simCardNumber;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword( final String password ) {
        this.password = password;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin( final String login ) {
        this.login = login;
    }

    private String simCardNumber;
    private String password;
    private String login;

    public PatrulLoginRequest () {}
}
