package app.criteria.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

/**
 * Has a to-many association to {@link Like}, to test criteria on an alias of a collection
 */
@ToString
@GrailsCompileStatic
class Team {

    String name

    static hasMany = [likes: Like]

    static mapping = {
        table('pg_extensions_team')
    }
}
