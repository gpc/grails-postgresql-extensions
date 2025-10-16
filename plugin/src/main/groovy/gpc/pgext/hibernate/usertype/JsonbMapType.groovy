package gpc.pgext.hibernate.usertype

import groovy.transform.CompileStatic

@CompileStatic
class JsonbMapType extends JsonMapType {

    static int SQLTYPE = 90022

    @Override
    int[] sqlTypes() {
        SQLTYPE as int[]
    }
}
