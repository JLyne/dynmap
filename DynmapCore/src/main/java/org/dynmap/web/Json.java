package org.dynmap.web;

import com.google.gson.Gson;

public class Json {
    private static final Gson gson = new Gson();

    public static String stringifyJson(Object o) {
        return gson.toJson(o);
    }
}
