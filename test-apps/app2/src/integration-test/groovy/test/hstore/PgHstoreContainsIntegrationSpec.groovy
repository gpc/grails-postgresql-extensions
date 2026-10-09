package test.hstore

import app.criteria.hstore.PgHstoreTestSearchService
import app.hstore.TestHstoreMap
import spock.lang.Specification

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgHstoreContainsIntegrationSpec extends Specification {

    @Autowired
    PgHstoreTestSearchService pgHstoreTestSearchService

    def setup() {
        TestHstoreMap.executeUpdate('delete from TestHstoreMap')
    }

    void 'Test only one value result 2 different elements'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreContains', map)

        then:
            result.size() == 2
            result.find { it.name == 'test1' }
            !result.find { it.name == 'test2' }
            !result.find { it.name == 'test3' }
            result.find { it.name == 'test4' }

        where:
            map = [b: '1']
    }

    void 'Test two values that matches partialy with one element'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreContains', map)

        then:
            result.size() == 1
            !result.find { it.name == 'test1' }
            !result.find { it.name == 'test2' }
            !result.find { it.name == 'test3' }
            result.find { it.name == 'test4' }

        where:
            map = [b: '1', c: 'test']
    }

    void 'No matches with the same combination key/value'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreContains', map)

        then:
            result.size() == 0

        where:
            map = [b: '3']
    }

    void 'No matches with the same combination but one of the elements matches'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreContains', map)

        then:
            result.size() == 0

        where:
            map = [b: '1', c: 'test-otro']
    }

    void 'When empty map returns all elements'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreContains', map)

        then:
            result.size() == 4

        where:
            map = [:]
    }
}
