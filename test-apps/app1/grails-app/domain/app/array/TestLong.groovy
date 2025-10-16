package app.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class TestLong {

    Long[] longNumbers

    static mapping = {
        longNumbers(type: ArrayType, params: [type: Long])
    }

    static constraints = {
        longNumbers(nullable: true)
    }
}
