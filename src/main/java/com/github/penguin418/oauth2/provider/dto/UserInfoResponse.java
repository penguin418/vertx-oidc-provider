package com.github.penguin418.oauth2.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserInfoResponse {
    private String sub;
    private String name;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String preferredUsername;
    private String profile;
    private String picture;
    private String website;
    private String email;
    private Boolean emailVerified;
    private String gender;
    private String birthdate;
    private String zoneinfo;
    private String locale;
    private String phoneNumber;
    private Boolean phoneNumberVerified;
    private String address;
    private Long updatedAt;

    protected UserInfoResponse(){}

    public UserInfoResponse(String sub){
        this.sub = sub;
    }

}
