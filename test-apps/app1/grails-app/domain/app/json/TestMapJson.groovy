package app.json

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.JsonMapType

@ToString
@GrailsCompileStatic
class TestMapJson {

    Map data

    static constraints = {
        data(nullable: true)
    }

    static mapping = {
        data(type: JsonMapType)
    }
}
