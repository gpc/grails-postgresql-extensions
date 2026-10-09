package test.hstore

import app.criteria.hstore.PgHstoreTestSearchService
import app.hstore.TestHstoreMap
import spock.lang.Specification

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgHstoreILikeValueFunctionIntegrationSpec extends Specification {

    @Autowired
    PgHstoreTestSearchService pgHstoreTestSearchService

    def setup() {
        TestHstoreMap.executeUpdate('delete from TestHstoreMap')
    }

    void 'Test find hstore that ilikes value'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'test', b: '3']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreILikeValue', '%test%')

        then:
            result.size() == 3
            result.find { it.name == 'test1' }
            !result.find { it.name == 'test2' }
            result.find { it.name == 'test3' }
            result.find { it.name == 'test4' }
    }

    void 'Test find hstore that no ilikes value'() {
        setup:
            new TestHstoreMap(name: 'test1', testAttributes: [a: 'test', b: '1']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test2', testAttributes: [b: '2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test3', testAttributes: [a: 'test2']).save(flush: true, failOnError: true)
            new TestHstoreMap(name: 'test4', testAttributes: [c: 'Xa', b: '3']).save(flush: true, failOnError: true)

        when:
            def result = pgHstoreTestSearchService.search('testAttributes', 'pgHstoreILikeValue', '%X')

        then:
            result.size() == 0
    }
}
