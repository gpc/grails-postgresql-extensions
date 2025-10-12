package test.json

import app.criteria.json.PgJsonbTestSearchService
import app.json.TestMapJsonb
import spock.lang.Specification
import spock.lang.Unroll

import org.springframework.beans.factory.annotation.Autowired

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgJsonbValuesIntegrationSpec extends Specification {

    @Autowired
    PgJsonbTestSearchService pgJsonbTestSearchService

    @Unroll
    void 'Test equals finding value: #value with condition is ilike (jsonb)'() {
        setup:
            new TestMapJsonb(data: [name: 'Iván', lastName: 'López']).save(flush: true)
            new TestMapJsonb(data: [name: 'Alonso', lastName: 'Torres']).save(flush: true)
            new TestMapJsonb(data: [name: 'Iván', lastName: 'Pérez']).save(flush: true)

        when:
        def result = pgJsonbTestSearchService.search('pgJson', 'data', '->>', 'name', 'ilike', value)

        then:
            result.size() == size
            result.every { it.data.name.matches "^(?i)${value.replace('%', '.*')}\$" }

        where:
            value  || size
            '%iv%' || 2
            'John' || 0
    }

    @Unroll
    void 'Test equals finding value: #value with condition equals (jsonb)'() {
        setup:
            new TestMapJsonb(data: [name: 'Iván', lastName: 'López']).save(flush: true)
            new TestMapJsonb(data: [name: 'Alonso', lastName: 'Torres']).save(flush: true)
            new TestMapJsonb(data: [name: 'Iván', lastName: 'Pérez']).save(flush: true)

        when:
        def result = pgJsonbTestSearchService.search('pgJson', 'data', '->>', 'name', '=', value)

        then:
            result.size() == size
            result.every { it.data.name == value }

        where:
            value  || size
            'Iván' || 2
            'John' || 0
    }

    @Unroll
    void 'Test equals finding value: #value with condition does not equal (jsonb)'() {
        setup:
            new TestMapJsonb(data: [name: 'Iván', lastName: 'López']).save(flush: true)
            new TestMapJsonb(data: [name: 'Alonso', lastName: 'Torres']).save(flush: true)
            new TestMapJsonb(data: [name: 'Iván', lastName: 'Pérez']).save(flush: true)

        when:
        def result = pgJsonbTestSearchService.search('pgJson', 'data', '->>', 'name', '<>', value)

        then:
            result.size() == size
            result.every { it.data.name != value }

        where:
            value  || size
            'Iván' || 1
            'John' || 3
    }
}
