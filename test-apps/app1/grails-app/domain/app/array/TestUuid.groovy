package app.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class TestUuid {

    UUID[] uuidArray

    static mapping = {
        uuidArray(type: ArrayType, params: [type: UUID])
    }

    static constraints = {
        uuidArray(nullable: true)
    }
}
