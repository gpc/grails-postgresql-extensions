package test.array

import app.criteria.array.Like
import app.criteria.array.PgArrayTestSearchService
import app.criteria.array.User
import spock.lang.Specification
import spock.lang.Unroll

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgIsEmptyOrContainsCriteriaTestServiceIntegrationSpec extends Specification {

    @Autowired PgArrayTestSearchService pgArrayTestSearchService

    def setup() {
        User.executeUpdate('delete from User')
        Like.executeUpdate('delete from Like')
    }

    @Unroll
    void 'search #number in an array of integers'() {
        setup:
            def l1 = new Like(favoriteNumbers: [3, 7]).save(flush: true, failOnError: true)
            def l2 = new Like(favoriteNumbers: []).save(flush: true, failOnError: true)
            def l3 = new Like(favoriteNumbers: [3, 4]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteNumbers', 'pgArrayIsEmptyOrContains', number)

        then:
            result.size() == resultSize

        where:
            number              | resultSize
            []                  | 1
            [3]                 | 2
            [4]                 | 1
            [3, 4]              | 1
            [] as Integer[]     | 1
            [3] as Integer[]    | 2
            [3, 4] as Integer[] | 1
    }

    @Unroll
    void 'search #number in an array of longs'() {
        setup:
            new Like(favoriteLongNumbers: [12383L, 2392348L, 3498239L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [12383L, 98978L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [12383L]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteLongNumbers', 'pgArrayIsEmptyOrContains', number)

        then:
            result.size() == resultSize

        where:
            number                     | resultSize
            [12383L, 98978L]           | 1
            [12383L]                   | 3
            []                         | 1
            [12383L, 98978L] as Long[] | 1
            [12383L] as Long[]         | 3
            [] as Long[]               | 1
    }

    @Unroll
    void 'search #number in an array of floats'() {
        setup:
            new Like(favoriteFloatNumbers: [12383f, 2392348f, 3498239f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [12383f, 98978f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [12383f]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteFloatNumbers', 'pgArrayIsEmptyOrContains', number)

        then:
            result.size() == resultSize

        where:
            number                      | resultSize
            [12383f, 98978f]            | 1
            [12383f]                    | 3
            []                          | 1
            [12383f, 98978f] as Float[] | 1
            [12383f] as Float[]         | 3
            [] as Float[]               | 1
    }

    @Unroll
    void 'search #number in an array of doubles'() {
        setup:
            new Like(favoriteDoubleNumbers: [12383d, 2392348d, 3498239d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [12383d, 98978d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: []).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [12383d]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteDoubleNumbers', 'pgArrayIsEmptyOrContains', number)

        then:
            result.size() == resultSize

        where:
            number                       | resultSize
            [12383d, 98978d]             | 1
            [12383d]                     | 3
            []                           | 1
            [12383d, 98978d] as Double[] | 1
            [12383d] as Double[]         | 3
            [] as Double[]               | 1
    }

    @Unroll
    void 'search #movie in an array of strings'() {
        setup:
            new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: []).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Romeo & Juliet', 'Casablanca', 'Star Wars']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings']).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteMovies', 'pgArrayIsEmptyOrContains', movie)

        then:
            result.size() == resultSize

        where:
            movie                                       | resultSize
            ['The Matrix']                              | 1
            ['Star Wars', 'Romeo & Juliet']             | 1
            ['The Lord of the Rings']                   | 2
            []                                          | 1
            ['Star Wars', 'Romeo & Juliet'] as String[] | 1
            ['The Lord of the Rings'] as String[]       | 2
            [] as String[]                              | 1
    }

    @Unroll
    void 'search #movie in an array of UUIDs'() {
        setup:
        new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
        new Like(favoriteMovieUUIDs: []).save(flush: true, failOnError: true)
        new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['Romeo & Juliet', 'Casablanca', 'Star Wars'])).save(flush: true, failOnError: true)
        new Like(favoriteMovieUUIDs: UuidBuilder.createUUIDs(['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings'])).save(flush: true, failOnError: true)

        when:
        def result = pgArrayTestSearchService.search('favoriteMovieUUIDs', 'pgArrayIsEmptyOrContains', movie)

        then:
        result.size() == resultSize

        where:
        movie                                                              | resultSize
        UuidBuilder.createUUIDs(['The Matrix'])                            | 1
        UuidBuilder.createUUIDs(['Star Wars', 'Romeo & Juliet'])           | 1
        UuidBuilder.createUUIDs(['The Lord of the Rings'])                 | 2
        []                                                                 | 1
        UuidBuilder.createUUIDs(['Star Wars', 'Romeo & Juliet']) as UUID[] | 1
        UuidBuilder.createUUIDs(['The Lord of the Rings']) as UUID[]       | 2
        [] as UUID[]                                                       | 1
    }

    @Unroll
    void 'search #juice in an array of enums'() {
        setup:
            new Like(favoriteJuices: [Like.Juice.ORANGE, Like.Juice.GRAPE]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.PINEAPPLE, Like.Juice.GRAPE]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: []).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.ORANGE]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteJuices', 'pgArrayIsEmptyOrContains', juice)

        then:
            result.size() == resultSize

        where:
            juice                                    | resultSize
            [Like.Juice.ORANGE]                      | 2
            [Like.Juice.GRAPE, Like.Juice.PINEAPPLE] | 1
            []                                       | 1
    }

    @Unroll
    void 'search in an array of strings with join with another domain class'() {
        setup:
            def user1 = new User(name: 'John', like: new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            def user2 = new User(name: 'Peter', like: new Like(favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user3 = new User(name: 'Mary', like: new Like(favoriteMovies: [])).save(flush: true, failOnError: true)
            def user4 = new User(name: 'Jonhny', like: new Like(favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'Spiderman'])).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.searchWithJoin('favoriteMovies', 'pgArrayIsEmptyOrContains', movie)

        then:
            result.size() == resultSize

        where:
            movie                         | resultSize
            ['The Matrix']                | 1
            ['Blade Runner', 'Spiderman'] | 2
            []                            | 1
    }
}
