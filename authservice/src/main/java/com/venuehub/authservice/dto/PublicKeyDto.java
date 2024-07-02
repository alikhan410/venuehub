package com.venuehub.authservice.dto;

public record PublicKeyDto(String kty, String e, String kid, String n) {
}
