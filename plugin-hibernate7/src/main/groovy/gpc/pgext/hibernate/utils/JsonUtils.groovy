package gpc.pgext.hibernate.utils

import groovy.transform.CompileStatic

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Converts values to json. Keeps Gson out of the signatures of the criteria extension classes, which Groovy
 * loads when it compiles the application, so Gson does not have to be on its compile classpath.
 */
@CompileStatic
class JsonUtils {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create()

    static String toJson(Object value) {
        GSON.toJson(value)
    }
}
