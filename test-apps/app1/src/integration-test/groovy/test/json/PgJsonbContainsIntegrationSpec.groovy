package test.json

import app.criteria.json.PgJsonbTestSearchService
import app.json.TestMapJsonb
import spock.lang.Specification

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgJsonbContainsIntegrationSpec extends Specification {

    @Autowired
    PgJsonbTestSearchService pgJsonbTestSearchService

    def setup() {
        TestMapJsonb.executeUpdate('delete from TestMapJsonb')
    }

    void 'Test only one value'() {
        given:
            def obj1 = new TestMapJsonb(data: [a: 'foo', b: '1']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [b: 1, d: '2']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [a: 'test']).save(flush: true, failOnError: true)
            def obj4 = new TestMapJsonb(data: [b: '1', a: 'foo', c: 'test',]).save(flush: true, failOnError: true)

        when:
            def result = pgJsonbTestSearchService.search('pgJsonbContains', 'data', map)

        then:
            result.size() == 2
            result.contains(obj1)
            result.contains(obj4)

        where:
            map = [b: '1']
    }

    void 'Test multiple values'() {
        given:
            def obj1 = new TestMapJsonb(data: [a: 'foo', b: '1']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [b: '2']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [a: 'test']).save(flush: true, failOnError: true)
            def obj4 = new TestMapJsonb(data: [a: 'foo', b: '1', c: 'test',]).save(flush: true, failOnError: true)

        when:
            def result = pgJsonbTestSearchService.search('pgJsonbContains', 'data', map)

        then:
            result.size() == 2
            result.contains(obj1)
            result.contains(obj4)

        where:
            map = [a: 'foo', b: '1']
    }

    void 'No matches with the same combination key/value'() {
        given:
            new TestMapJsonb(data: [a: 'foo', b: '1']).save(flush: true, failOnError: true)

        when:
            def result = pgJsonbTestSearchService.search('pgJsonbContains', 'data', map)

        then:
            result.size() == 0

        where:
            map = [b: 3]
    }

    void 'All matched with empty map'() {
        given:
            new TestMapJsonb(data: [a: 'foo', b: '1']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [b: '2']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [a: 'test']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [a: 'foo', b: '1', c: 'test',]).save(flush: true, failOnError: true)

        when:
            def result = pgJsonbTestSearchService.search('pgJsonbContains', 'data', map)

        then:
            result.size() == 4
        where:
            map = [:]
    }

    void 'None matched with null'() {
        given:
            new TestMapJsonb(data: [a: 'foo', b: '1']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [b: '2']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [a: 'test']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [a: 'foo', b: '1', c: 'test',]).save(flush: true, failOnError: true)

        when:
            def result = pgJsonbTestSearchService.search('pgJsonbContains', 'data', map)

        then:
            result.size() == 0

        where:
            map = null
    }
}
