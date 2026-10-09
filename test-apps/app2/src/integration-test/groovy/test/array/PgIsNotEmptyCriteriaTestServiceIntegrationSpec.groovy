package test.array

import app.criteria.array.Like
import app.criteria.array.PgArrayTestSearchService
import app.criteria.array.User
import spock.lang.Specification

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgIsNotEmptyCriteriaTestServiceIntegrationSpec extends Specification {

    @Autowired
    PgArrayTestSearchService pgArrayTestSearchService

    def setup() {
        User.executeUpdate('delete from User')
        Like.executeUpdate('delete from Like')
    }

    void 'search for empty integer arrays'() {
        setup:
            new Like(favoriteNumbers: [3, 7, 20]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteNumbers', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
    }

    void 'search for empty long arrays'() {
        setup:
            new Like(favoriteLongNumbers: [3L, 7L, 20L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteLongNumbers', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
    }

    void 'search for empty float arrays'() {
        setup:
            new Like(favoriteFloatNumbers: [3f, 7f, 20f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteFloatNumbers', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
    }

    void 'search for empty double arrays'() {
        setup:
            new Like(favoriteDoubleNumbers: [3d, 7d, 20d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteDoubleNumbers', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
    }

    void 'search for empty string arrays'() {
        setup:
            new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: []).save(flush: true, failOnError: true)
            new Like(favoriteMovies: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteMovies', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
    }

    void 'search for empty UUID arrays'() {
        setup:
        new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
        new Like(favoriteMovieUUIDs: []).save(flush: true, failOnError: true)
        new Like(favoriteMovieUUIDs: []).save(flush: true, failOnError: true)

        when:
        def result = pgArrayTestSearchService.search('favoriteMovieUUIDs', 'pgArrayIsNotEmpty')

        then:
        result.size() == 1
    }

    void 'search for empty enum arrays'() {
        setup:
            new Like(favoriteJuices: [Like.Juice.APPLE, Like.Juice.TOMATO]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: []).save(flush: true, failOnError: true)
            new Like(favoriteJuices: []).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteJuices', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
    }

    void 'search in an array of strings with join with another domain class'() {
        setup:
            def user1 = new User(name: 'John', like: new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            def user2 = new User(name: 'Peter', like: new Like(favoriteMovies: [])).save(flush: true, failOnError: true)
            def user3 = new User(name: 'Mary', like: new Like(favoriteMovies: [])).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.searchWithJoin('favoriteMovies', 'pgArrayIsNotEmpty')

        then:
            result.size() == 1
            result.contains(user1)
    }
}
