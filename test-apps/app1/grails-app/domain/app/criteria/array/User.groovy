package app.criteria.array

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class User {

    String name
    Like like

    static mapping = {
        table('pg_extensions_user')
    }

    String toString() {
        name
    }
}
