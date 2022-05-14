package com.github.penguin418.oauth2.dcr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Authorization Request<br>
 * RFC 7591 - 2.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientRegistrationRequest {
    /**
     * required for redirect-based flows(authorization_code, implicit)
     */
    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    /**
     * one of the followings
     * - none
     * - client_secret_post
     * - client_secret_basic (default)
     */
    @JsonProperty("token_endpoint_auth_method")
    private String tokenEndpointAuthMethod;

    /**
     * some of the followings
     * - authorization_code (default)
     * - implicit
     * - password
     * - client_credentials
     * - refresh_token
     * - urn:ietf:params:oauth:grant-type:jwt-bearer
     * - urn:ietf:params:oauth:grant-type:saml2-bearer
     */
    @JsonProperty("grant_types")
    private List<String> grantTypes;

    /**
     * some of the followings
     * - code (required for authorization_code grant_type)
     * - token (required for implicit grant_type)
     */
    @JsonProperty("response_types")
    private List<String> responseTypes;

    /**
     * default value is value of client_id
     */
    @JsonProperty("client_name")
    private String clientName;
    /**
     * must have the same host and scheme as the
     * those defined in the array of "redirect_uris"
     */
    @JsonProperty("client_uri")
    private String clientUri;
    /**
     * must have the same host and scheme as the
     * those defined in the array of "redirect_uris"
     */
    @JsonProperty("logo_uri")
    private String logoUri;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("contacts")
    private List<String> contacts;
    /**
     * must have the same host and scheme as the
     * those defined in the array of "redirect_uris"
     */
    @JsonProperty("tos_uri")
    private List<String> tosUri;
    /**
     * must have the same host and scheme as the
     * those defined in the array of "redirect_uris"
     */
    @JsonProperty("policy_uri")
    private String policyUri;

    @JsonProperty("jwks_uri")
    private String jwksUri;

    @JsonProperty("jwks")
    private String jwks;

    /**
     * remain the same across different versions
     */
    @JsonProperty("software_id")
    private String softwareId;

    @JsonProperty("software_version")
    private String softwareVersion;
}
