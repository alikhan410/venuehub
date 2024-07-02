package com.venuehub.authservice.response;

import com.venuehub.authservice.dto.PublicKeyDto;

public record JwkSetResponse(PublicKeyDto[] keys) {
}
