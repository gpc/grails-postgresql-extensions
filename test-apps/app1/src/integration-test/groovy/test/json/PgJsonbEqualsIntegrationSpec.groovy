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
class PgJsonbEqualsIntegrationSpec extends Specification {

    @Autowired
    PgJsonbTestSearchService pgJsonbTestSearchService

    @Unroll
    void 'Test equals finding value: #value (jsonb)'() {
        setup:
            new TestMapJsonb(data: [name: 'Iván', lastName: 'López']).save(flush: true)
            new TestMapJsonb(data: [name: 'Alonso', lastName: 'Torres']).save(flush: true)
            new TestMapJsonb(data: [name: 'Iván', lastName: 'Pérez']).save(flush: true)

        when:
            def result = pgJsonbTestSearchService.search('pgJsonHasFieldValue', 'data', 'name', value)

        then:
            result.size() == size
            result.every { it.data.name == value }

        where:
            value  || size
            'Iván' || 2
            'John' || 0
    }
}
