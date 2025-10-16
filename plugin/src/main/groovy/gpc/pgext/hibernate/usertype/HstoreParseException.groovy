package gpc.pgext.hibernate.usertype

import groovy.transform.CompileStatic

@CompileStatic
class HstoreParseException extends RuntimeException {

    private static final long serialVersionUID = 3418828452515857160L

    HstoreParseException(String error, int position) {
        super("Error @$position : $error")
    }
}
