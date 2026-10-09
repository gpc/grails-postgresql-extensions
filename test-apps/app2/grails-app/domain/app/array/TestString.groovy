package app.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class TestString {

    String[] stringArray

    static mapping = {
        stringArray(type: ArrayType, params: [type: String])
    }

    static constraints = {
        stringArray(nullable: true)
    }
}
