package app.hstore

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.HstoreMapType

@ToString
@GrailsCompileStatic
class TestHstoreMap {

    String name
    Integer luckyNumber

    Map testAttributes

    static constraints = {
        name(nullable: true)
        luckyNumber(nullable: true)
    }

    static mapping = {
        testAttributes(type: HstoreMapType)
    }
}
