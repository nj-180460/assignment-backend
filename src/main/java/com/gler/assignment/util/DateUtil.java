package com.gler.assignment.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private DateUtil () {
        throw new UnsupportedOperationException("Cannot be instantiated. Use static methods only.");
    }

    public static String generateTimestampFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.n");
        return LocalDateTime.now().format(formatter);
    }

}
