package app.json

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.JsonbMapType

@ToString
@GrailsCompileStatic
class TestMapJsonb {

    Map data

    static constraints = {
        data(nullable: true)
    }

    static mapping = {
        data(type: JsonbMapType)
    }
}
