
package com.methaltech.application.data;

import java.util.Locale;

public final class CoaEnumMapper {
    private CoaEnumMapper() {}

    public static String normalize(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        s = s.replace("&", " ");
        s = s.replaceAll("[^A-Za-z0-9]+", "_"); // spaces, hyphens, slashes → _
        s = s.replaceAll("_+", "_");
        s = s.replaceAll("^_+|_+$", "");
        return s;
    }

    public static <E extends Enum<E>> E parseEnumOrNull(Class<E> type, String raw) {
        String key = normalize(raw);
        if (key == null) return null;
        return Enum.valueOf(type, key); // throws IllegalArgumentException if mismatch
    }
}

