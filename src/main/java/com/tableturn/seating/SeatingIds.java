package com.tableturn.seating;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A seating's public id - S001, S204 - made from its database id, which is never shown itself.*/
public final class SeatingIds {

private static final Pattern PUBLIC = Pattern.compile("S(\\d+)");
    private SeatingIds() {
    }

    public static String format(long id) {
        return String.format("S%03d", id);
    }


    /** The database id behind a public id, or empty when it is not one. */
    public static Optional<Long> parse(String publicId) {
Matcher m = PUBLIC.matcher(publicId);
return m.matches() ? Optional.of(Long.parseLong(m.group(1))) : Optional.empty();
}
}
