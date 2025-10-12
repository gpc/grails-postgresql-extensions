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
class PgOverlapsCriteriaTestServiceIntegrationSpec extends Specification {

    @Autowired
    PgArrayTestSearchService pgArrayTestSearchService

    def setup() {
        User.executeUpdate('delete from User')
        Like.executeUpdate('delete from Like')
    }

    @Unroll
    void 'overlaps #number in an array of integers'() {
        setup:
            new Like(favoriteNumbers: [3, 7, 20]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: [5, 17, 9, 6, 20]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: [3, 4, 20]).save(flush: true, failOnError: true)
            new Like(favoriteNumbers: [9, 4, 20]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteNumbers', 'pgArrayOverlaps', number)

        then:
            result.size() == resultSize

        where:
            number               | resultSize
            3                    | 2
            17                   | 1
            9                    | 2
            4                    | 2
            1                    | 0
            20                   | 4
            [3, 4]               | 3
            [3, 4, 7]            | 3
            [4]                  | 2
            [3, 20]              | 4
            []                   | 0
            [4] as Integer[]     | 2
            [3, 20] as Integer[] | 4
            [] as Integer[]      | 0
    }

    @Unroll
    void 'overlaps #number in an array of longs'() {
        setup:
            new Like(favoriteLongNumbers: [1L, 23L, 34L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [1L, 7L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [-9L, 16L, 7L]).save(flush: true, failOnError: true)
            new Like(favoriteLongNumbers: [1L]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteLongNumbers', 'pgArrayOverlaps', number)

        then:
            result.size() == resultSize

        where:
            number             | resultSize
            1L                 | 3
            7L                 | 2
            -9L                | 1
            100L               | 0
            [1L, 7L]           | 4
            [1L]               | 3
            []                 | 0
            [1L, 7L] as Long[] | 4
            [1L] as Long[]     | 3
            [] as Long[]       | 0
    }

    @Unroll
    void 'overlaps #number in an array of floats'() {
        setup:
            new Like(favoriteFloatNumbers: [1f, 23f, 34f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [1f, 7f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [-9f, 16f, 7f]).save(flush: true, failOnError: true)
            new Like(favoriteFloatNumbers: [1f]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteFloatNumbers', 'pgArrayOverlaps', number)

        then:
            result.size() == resultSize

        where:
            number              | resultSize
            1f                  | 3
            7f                  | 2
            -9f                 | 1
            100f                | 0
            [1f, 7f]            | 4
            [1f]                | 3
            []                  | 0
            [1f, 7f] as Float[] | 4
            [1f] as Float[]     | 3
            [] as Float[]       | 0
    }

    @Unroll
    void 'overlaps #number in an array of double'() {
        setup:
            new Like(favoriteDoubleNumbers: [1d, 23d, 34d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [1d, 7d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [-9d, 16d, 7d]).save(flush: true, failOnError: true)
            new Like(favoriteDoubleNumbers: [1d]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteDoubleNumbers', 'pgArrayOverlaps', number)

        then:
            result.size() == resultSize

        where:
            number               | resultSize
            1d                   | 3
            7d                   | 2
            -9d                  | 1
            100d                 | 0
            [1d, 7d]             | 4
            [1d]                 | 3
            []                   | 0
            [1d, 7d] as Double[] | 4
            [1d] as Double[]     | 3
            [] as Double[]       | 0
    }

    @Unroll
    void 'search #movie in an array of strings'() {
        setup:
            new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Romeo & Juliet', 'Casablanca', 'Star Wars']).save(flush: true, failOnError: true)
            new Like(favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings']).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteMovies', 'pgArrayOverlaps', movie)

        then:
            result.size() == resultSize

        where:
            movie                                       | resultSize
            'The Matrix'                                | 1
            'The Lord of the Rings'                     | 2
            'Blade Runner'                              | 2
            'Star Wars'                                 | 2
            'The Usual Suspects'                        | 0
            ['Star Wars', 'Romeo & Juliet']             | 3
            ['The Lord of the Rings']                   | 2
            []                                          | 0
            ['Star Wars', 'Romeo & Juliet'] as String[] | 3
            ['The Lord of the Rings'] as String[]       | 2
            [] as String[]                              | 0
    }

    @Unroll
    void 'search #juice in an array of enums'() {
        setup:
            new Like(favoriteJuices: [Like.Juice.ORANGE, Like.Juice.GRAPE]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.PINEAPPLE, Like.Juice.GRAPE, Like.Juice.CARROT, Like.Juice.CRANBERRY]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.APPLE, Like.Juice.TOMATO, Like.Juice.CARROT]).save(flush: true, failOnError: true)
            new Like(favoriteJuices: [Like.Juice.ORANGE, Like.Juice.TOMATO, Like.Juice.CARROT]).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.search('favoriteJuices', 'pgArrayOverlaps', juice)

        then:
            result.size() == resultSize

        where:
            juice                                    | resultSize
            Like.Juice.CRANBERRY                     | 1
            Like.Juice.ORANGE                        | 2
            Like.Juice.LEMON                         | 0
            Like.Juice.APPLE                         | 1
            Like.Juice.GRAPE                         | 2
            Like.Juice.PINEAPPLE                     | 1
            Like.Juice.TOMATO                        | 2
            Like.Juice.CARROT                        | 3
            Like.Juice.GRAPEFRUIT                    | 0
            [Like.Juice.ORANGE, Like.Juice.GRAPE]    | 3
            [Like.Juice.GRAPE, Like.Juice.PINEAPPLE] | 2
            [Like.Juice.CARROT]                      | 3
            [Like.Juice.CARROT, Like.Juice.TOMATO]   | 3
            []                                       | 0
    }

    void 'search in an array of strings with join with another domain class'() {
        setup:
            def user1 = new User(name: 'John', like: new Like(favoriteMovies: ['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            def user2 = new User(name: 'Peter', like: new Like(favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user3 = new User(name: 'Mary', like: new Like(favoriteMovies: ['Romeo & Juliet', 'Casablanca', 'Star Wars'])).save(flush: true, failOnError: true)
            def user4 = new User(name: 'Jonhny', like: new Like(favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings'])).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.searchWithJoin('favoriteMovies', 'pgArrayOverlaps', movie)

        then:
            result.size() == 3
            result.contains(user2)
            result.contains(user3)
            result.contains(user4)

        where:
            movie = ['Star Wars', 'Romeo & Juliet']
    }

    void 'search in an array of strings with join with another domain class and or statement'() {
        setup:
            def user1 = new User(name: 'John', like: new Like(favoriteNumbers: [3, 7], favoriteMovies: ['The Matrix', 'The Lord of the Rings'])).save(flush: true, failOnError: true)
            def user2 = new User(name: 'Peter', like: new Like(favoriteNumbers: [5, 17, 9, 6], favoriteMovies: ['Spiderman', 'Blade Runner', 'Star Wars'])).save(flush: true, failOnError: true)
            def user3 = new User(name: 'Mary', like: new Like(favoriteNumbers: [3, 4], favoriteMovies: ['Romeo & Juliet', 'Casablanca', 'Star Wars'])).save(flush: true, failOnError: true)
            def user4 = new User(name: 'Jonhny', like: new Like(favoriteNumbers: [9, 4], favoriteMovies: ['Romeo & Juliet', 'Blade Runner', 'The Lord of the Rings'])).save(flush: true, failOnError: true)

        when:
            def result = pgArrayTestSearchService.searchWithJoinAnd('pgArrayOverlaps', favoriteMovies: movie, favoriteNumbers: number)

        then:
            result.size() == 2
            result.contains(user2)
            result.contains(user4)

        where:
            movie = ['Star Wars', 'Romeo & Juliet']
            number = [9]
    }

    @Unroll
    void 'search a invalid list inside the array of integers'() {
        when:
            pgArrayTestSearchService.search('favoriteNumbers', 'pgArrayOverlaps', number)

        then:
            thrown HibernateException

        where:
            number << [['Test'], [1, 'Test'], [1L], [1, 1L]]
    }

    @Unroll
    void 'search a invalid list inside the array of long'() {
        when:
            pgArrayTestSearchService.search('favoriteLongNumbers', 'pgArrayOverlaps', number)

        then:
            thrown HibernateException

        where:
            number << [['Test'], [1L, 'Test'], [1], [1L, 1]]
    }

    @Unroll
    void 'search a invalid list inside the array of string'() {
        when:
            pgArrayTestSearchService.search('favoriteMovies', 'pgArrayOverlaps', movie)

        then:
            thrown HibernateException

        where:
            movie << [[1], ['Test', 1], [1L], ['Test', 1L]]
    }

    @Unroll
    void 'search an invalid list inside the array of UUID'() {
        when:
            pgArrayTestSearchService.search('favoriteMovieUUIDs', 'pgArrayOverlaps', movie)

        then:
            thrown HibernateException

        where:
            movie << [[1], ['Test', UUID.randomUUID()], [1L], [UUID.randomUUID(), 1L]]
    }

    @Unroll
    void 'search an invalid list inside the array of enum'() {
        when:
            pgArrayTestSearchService.search('favoriteJuices', 'pgArrayOverlaps', juice)

        then:
            thrown HibernateException

        where:
            juice << [['Test'], [Like.Juice.ORANGE, 'Test'], [1L], [Like.Juice.APPLE, 1L]]
    }
}
