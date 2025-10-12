package test.hstore

import app.criteria.hstore.PgHstoreTestSearchService
import app.hstore.TestHstoreMap
import spock.lang.Specification

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgHstoreIsContainedIntegrationSpec extends Specification {

    @Autowired
    PgHstoreTestSearchService pgHstoreTestSearchService

    def setup() {
        TestHstoreMap.executeUpdate('delete from TestHstoreMap')
    }

    void 'No element matches with the empty set'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [d: '10']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreIsContained', map)

        then:
            result.size() == 0

        where:
            map = [:]
    }

    void 'All elements matches'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [d: '10']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreIsContained', map)

        then:
            result.size() == 4
            result.find { it.name == 'test1' }
            result.find { it.name == 'test2' }
            result.find { it.name == 'test3' }
            result.find { it.name == 'test4' }

        where:
            map = [a: 'test', b: '1', c: 'test', d: '10']
    }

    void 'Some elements matches'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [d: '10']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreIsContained', map)

        then:
            result.size() == 3
            result.find { it.name == 'test1' }
            !result.find { it.name == 'test2' }
            result.find { it.name == 'test3' }
            result.find { it.name == 'test4' }

        where:
            map = [a: 'test', b: '1', c: 'test']
    }
}
