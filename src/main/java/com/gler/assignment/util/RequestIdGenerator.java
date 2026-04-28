package com.gler.assignment.util;

import java.util.UUID;

public final class RequestIdGenerator {

    private RequestIdGenerator () {
        throw new UnsupportedOperationException("Cannot be instantiated. Use static methods only.");
    }

    public static String newRequestId(){
        return UUID.randomUUID().toString();
    }

}
