package test.array

import app.criteria.array.Like
import app.criteria.array.PgArrayTestSearchService
import app.criteria.array.User
import org.hibernate.HibernateException
import spock.lang.Specification
import spock.lang.Unroll

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgNotEqualsCriteriaTestServiceIntegrationSpec extends Specification {

    @Autowired
    PgArrayTestSearchService pgArrayTestSearchService

    def setup() {
        User.executeUpdate('delete from User')
        Like.executeUpdate('delete from Like')
    }

    @Unroll
    void 'check equals for #number in an array of integers'() {
        setup:
            new Like(favoriteNumbers: [3, 7, 20]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: [17]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: [9, 4, 20]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteNumbers', 'pgArrayNotEquals', number)

        then:
            result.size() == resultSize

        where:
            number          | resultSize
            3               | 4
            17              | 3
            [3, 7, 20]      | 3
            [7, 3, 20]      | 4
            []              | 3
            17 as Integer[] | 3
            [] as Integer[] | 3
    }

    @Unroll
    void 'check equals for #number in an array of longs'() {
        setup:
            new Like(favoriteLongNumbers: [1L, 23L, 34L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [7L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [1L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteLongNumbers', 'pgArrayNotEquals', number)

        then:
            result.size() == resultSize

        where:
            number         | resultSize
            1L             | 3
            [1L, 23L, 34L] | 3
            [1L, 34L, 23L] | 4
            []             | 3
            7L as Long[]   | 3
    }

    @Unroll
    void 'check equals for #number in an array of floats'() {
        setup:
            new Like(favoriteFloatNumbers: [12f, 23f, 34f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [12f, 98f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [12f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteFloatNumbers', 'pgArrayNotEquals', number)

        then:
            result.size() == resultSize

        where:
            number        | resultSize
            12f           | 3
            [12f, 98f]    | 3
            [98f, 12f]    | 4
            [12f]         | 3
            []            | 3
            [] as Float[] | 3
    }

    @Unroll
    void 'check equals for #number in an array of doubles'() {
        setup:
            new Like(favoriteDoubleNumbers: [12d, 23d, 34d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [12d, 98d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [12d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteDoubleNumbers', 'pgArrayNotEquals', number)

        then:
            result.size() == resultSize

        where:
            number            | resultSize
            12d               | 3
            [12d]             | 3
            [12d, 98d]        | 3
            [98d, 12d]        | 4
            []                | 3
            [12d] as Double[] | 3
    }

    @Unroll
    void 'check equals for #movie in an array of strings'() {
        setup:
            new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Star Wars']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteMovies', 'pgArrayNotEquals', movie)

        then:
            result.size() == resultSize

        where:
            movie                                      | resultSize
            'Star Wars'                                | 3
            ['Star Wars']                              | 3
            ['Star Wars'] as String[]                  | 3
            'The Usual Suspects'                       | 4
            ['Spiderman', 'Blade Runner', 'Star Wars'] | 3
            ['Spiderman', 'Star Wars', 'Blade Runner'] | 4
            []                                         | 3
    }

    @Unroll
    void 'check equals for #movie in an array of uuids'() {
        setup:
            new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['Star Wars'])).save(flush: true, failOnError: true)
            new Like(favoriteMovieUUIDs: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteMovieUUIDs', 'pgArrayNotEquals', movie)

        then:
            result.size() == resultSize

        where:
            movie                                                               | resultSize
            UuidBuilder.createUUID('Star Wars')                                 | 3
            UuidBuilder.createUUIDs(['Star Wars'])                              | 3
            UuidBuilder.createUUIDs(['Star Wars']) as UUID[]                    | 3
            UuidBuilder.createUUID('The Usual Suspects')                        | 4
            UuidBuilder.createUUIDs(['Spiderman', 'Blade Runner', 'Star Wars']) | 3
            UuidBuilder.createUUIDs(['Spiderman', 'Star Wars', 'Blade Runner']) | 4
            []                                                                  | 3
    }

    @Unroll
    void 'check equals for #juice in an array of enums'() {
        setup:
            new Like(favoriteJuices: [Like.Juice.ORANGE, Like.Juice.GRAPE]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.PINEAPPLE]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.ORANGE, Like.Juice.TOMATO, Like.Juice.CARROT]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteJuices', 'pgArrayNotEquals', juice)

        then:
            result.size() == resultSize

        where:
            juice                                 | resultSize
            Like.Juice.PINEAPPLE                  | 3
            [Like.Juice.ORANGE, Like.Juice.GRAPE] | 3
            [Like.Juice.GRAPE, Like.Juice.ORANGE] | 4
            []                                    | 3
    }

    void 'search in an array of strings with join with another domain class'() {
        setup:
            def user1 = new User(name: 'John', like: new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            def user2 = new User(name: 'Peter', like: new Like(favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user3 = new User(name: 'Mary', like: new Like(favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user4 = new User(name: 'Jonhny', like: new Like(favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings'])).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.searchWithJoin('favoriteMovies', 'pgArrayNotEquals', movie)

        then:
            result.size() == 2
            result.contains(user1)
            result.contains(user4)

        where:
            movie = ['Spiderman', 'Blade Runner', 'Star Wars']
    }

    void 'search in an array of strings with join with another domain class and or statement'() {
        setup:
            def user1 = new User(name: 'John', like: new Like(favoriteNumbers: [3, 7], favoriteMovies: ['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            def user2 = new User(name: 'Peter', like: new Like(favoriteNumbers: [5, 17, 9, 6], favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user3 = new User(name: 'Mary', like: new Like(favoriteNumbers: [5, 17, 9, 6], favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user4 = new User(name: 'Jonhny', like: new Like(favoriteNumbers: [9, 4], favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings'])).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.searchWithJoinByStringOrInteger('pgArrayNotEquals', favoriteMovies: movie, favoriteNumbers: number)

        then:
            result.size() == 2
            result.contains(user1)
            result.contains(user4)

        where:
            movie = ['Spiderman', 'Blade Runner', 'Star Wars']
            number = [5, 17, 9, 6]
    }

    void 'search an invalid list inside the array of integers'() {
        when:
            pgArrayTestSearchService.search('favoriteNumbers', 'pgArrayNotEquals', number)

        then:
            thrown HibernateException

        where:
            number << [['Test'], [1, 'Test'], [1L], [1, 1L]]
    }

    void 'search an invalid list inside the array of long'() {
        when:
            pgArrayTestSearchService.search('favoriteLongNumbers', 'pgArrayNotEquals', number)

        then:
            thrown HibernateException

        where:
            number << [['Test'], [1L, 'Test'], [1], [1L, 1]]
    }

    void 'search an invalid list inside the array of float'() {
        when:
            pgArrayTestSearchService.search('favoriteFloatNumbers', 'pgArrayNotEquals', number)

        then:
            thrown HibernateException

        where:
            number << [['Test'], [1f, 'Test'], [1], [1f, 1]]
    }

    void 'search an invalid list inside the array of double'() {
        when:
            pgArrayTestSearchService.search('favoriteDoubleNumbers', 'pgArrayNotEquals', number)

        then:
            thrown HibernateException

        where:
            number << [['Test'], [1d, 'Test'], [1], [1d, 1]]
    }

    void 'search an invalid list inside the array of string'() {
        when:
            pgArrayTestSearchService.search('favoriteMovies', 'pgArrayNotEquals', movie)

        then:
            thrown HibernateException

        where:
            movie << [[1], ['Test', 1], [1L], ['Test', 1L], [UUID.randomUUID()]]
    }

    void 'search an invalid list inside the array of UUID'() {
        when:
            pgArrayTestSearchService.search('favoriteMovieUUIDs', 'pgArrayNotEquals', movie)

        then:
            thrown HibernateException

        where:
            movie << [[1], ['Test', UUID.randomUUID()], [1L], [UUID.randomUUID(), 1L]]
    }

    void 'search an invalid list inside the array of enum'() {
        when:
            pgArrayTestSearchService.search('favoriteJuices', 'pgArrayNotEquals', juice)

        then:
            thrown HibernateException

        where:
            juice << [['Test'], [Like.Juice.ORANGE, 'Test'], [1L], [Like.Juice.APPLE, 1L]]
    }
}
