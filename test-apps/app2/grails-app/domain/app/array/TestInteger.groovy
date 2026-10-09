package app.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class TestInteger {

    Integer[] integerNumbers

    static mapping = {
        integerNumbers(type: ArrayType, params: [type: Integer])
    }

    static constraints = {
        integerNumbers(nullable: true)
    }
}
