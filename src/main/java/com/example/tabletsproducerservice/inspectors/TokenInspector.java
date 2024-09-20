package com.example.tabletsproducerservice.inspectors;

import com.example.tabletsproducerservice.constants.Errors;
import io.jsonwebtoken.Jwts;
import java.util.UUID;

public class TokenInspector extends DataValidateInspector {
    protected TokenInspector() {}

    /*
    принимает токен и возвращает JWT значение
    */
    protected final synchronized UUID decode (
            final String token
    ) {
        return UUID.fromString(
                Jwts
                        .parser()
                        .setSigningKey(
                                String.join(
                                        EMPTY,
                                        checkContextOrReturnDefaultValue(
                                                "variables.JWT_VARIABLES.ISSUER",
                                                Errors.DATA_NOT_FOUND.translate( "en" )
                                        ),
                                        checkContextOrReturnDefaultValue(
                                                "variables.JWT_VARIABLES.SECRET",
                                                Errors.DATA_NOT_FOUND.translate( "en" )
                                        )
                                )
                        ).build()
                        .parseClaimsJws( token )
                        .getPayload()
                        .getId()
        );
    }
}
