package gpc.pgext.hibernate.usertype

import groovy.transform.CompileStatic

/**
 * Converts Maps to String and vice versa according to hstore syntax.
 */
@CompileStatic
class HstoreHelper {

    private static final String K_V_SEPARATOR = '=>'

    private static String escapeQuotes(Object text) {
        text.toString().replaceAll('"', '\'')
    }

    static String toString(Map<Object, String> m) {
        if (!m) return ''
        m.collect { k, v ->
            "\"${escapeQuotes(k)}\"$K_V_SEPARATOR\"${escapeQuotes(v)}\""
        }.join(', ')
    }

    static String asStatement(Map<String, String> m) {
        if (!m) return ''
        def token = "\"?\"$K_V_SEPARATOR\"?\""
        Collections.nCopies(m.size(), token).join(', ')
    }

    static List<String> asListKeyValue(Map<String, String> m) {
        m ? (m.collectMany { k, v -> [k, v] } as List<String>) : [] as List<String>
    }

    static Map toMap(String s) {
        !s ?
            new HashMap<String, String>() :
            new HstoreParser(s).asMap()
    }
}
