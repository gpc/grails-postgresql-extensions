package app.criteria.array

import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

import gpc.pgext.hibernate.usertype.ArrayType

@ToString
@GrailsCompileStatic
class Like {

    static belongsTo = User

    Integer[] favoriteNumbers = []
    Long[] favoriteLongNumbers = []
    String[] favoriteMovies = []
    Juice[] favoriteJuices = []
    Double[] favoriteDoubleNumbers = []
    Float[] favoriteFloatNumbers = []
    UUID[] favoriteMovieUUIDs = []

    static enum Juice {
        ORANGE(0),
        APPLE(1),
        GRAPE(2),
        PINEAPPLE(3),
        TOMATO(4),
        GRAPEFRUIT(5),
        CRANBERRY(6),
        CARROT(7),
        LEMON(8)

        private final int id

        Juice(int id) { this.id = id }
    }

    static mapping = {
        table('pg_extensions_like')
        favoriteNumbers(type: ArrayType, params: [type: Integer])
        favoriteMovies(type: ArrayType, params: [type: String])
        favoriteLongNumbers(type: ArrayType, params: [type: Long])
        favoriteJuices(type: ArrayType, params: [type: Juice])
        favoriteFloatNumbers(type: ArrayType, params: [type: Float])
        favoriteDoubleNumbers(type: ArrayType, params: [type: Double])
        favoriteMovieUUIDs(type: ArrayType, params: [type: UUID])
    }

}
