package app.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class TestDouble {

    Double[] doubleNumbers

    static mapping = {
        doubleNumbers(type: ArrayType, params: [type: Double])
    }

    static constraints = {
        doubleNumbers(nullable: true)
    }
}
