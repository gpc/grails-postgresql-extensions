package app.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class TestFloat {

    Float[] floatNumbers

    static mapping = {
        floatNumbers(type: ArrayType, params: [type: Float])
    }

    static constraints = {
        floatNumbers(nullable: true)
    }
}
