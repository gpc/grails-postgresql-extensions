package test.order

import app.json.TestMapJsonb
import spock.lang.Specification

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

import static gpc.pgext.hibernate.order.OrderBySqlFormula.sqlFormula

@Rollback
@Integration
class PgOrderIntegrationSpec extends Specification {

    def pgOrderService

    def setup() {
        TestMapJsonb.executeUpdate('delete from TestMapJsonb')
    }

    void 'Order by a json property'() {
        setup:
            new TestMapJsonb(data: [name: 'Iván', lastName: 'López']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [name: 'Alonso', lastName: 'Torres']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [name: 'Ernesto', lastName: 'Pérez']).save(flush: true, failOnError: true)

        when:
            def result = pgOrderService.orderByJson()

        then:
            result
            result.data.name == ['Iván', 'Ernesto', 'Alonso']
    }

    void 'Order by random'() {
        setup:
            new TestMapJsonb(data: [name: 'Iván', lastName: 'López']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [name: 'Alonso', lastName: 'Torres']).save(flush: true, failOnError: true)
            new TestMapJsonb(data: [name: 'Ernesto', lastName: 'Pérez']).save(flush: true, failOnError: true)

        when:
            def result = pgOrderService.orderByRandom()

        then:
            result
            result.size() == 3
    }

    void 'An order by a sql formula keeps its position among the property orders'() {
        setup:
            def first = save('first', 'A')
            def second = save('second', 'B')
            def third = save('third', 'A')

        when: 'ordered by group and then by id descending'
            def result = TestMapJsonb.withCriteria {
                order(sqlFormula("(data->>'group')"))
                order('id', 'desc')
            }

        then:
            result*.id == [third.id, first.id, second.id]

        when: 'ordered by id descending and then by group'
            result = TestMapJsonb.withCriteria {
                order('id', 'desc')
                order(sqlFormula("(data->>'group')"))
            }

        then:
            result*.id == [third.id, second.id, first.id]
    }

    void 'An order by a sql formula is applied to a paginated list'() {
        setup:
            save('Iván', 'A')
            save('Alonso', 'A')
            save('Ernesto', 'A')

        when:
            def result = TestMapJsonb.createCriteria().list(max: 2, offset: 1) {
                order(sqlFormula("(data->>'name')"))
            }

        then:
            result*.data*.name == ['Ernesto', 'Iván']
            result.totalCount == 3
    }

    void 'An order by a sql formula in an or does not change the result of the or'() {
        setup:
            save('Iván', 'A')
            save('Alonso', 'B')
            save('Ernesto', 'A')

        when:
            def result = TestMapJsonb.withCriteria {
                or {
                    pgJsonHasFieldValue('data', 'group', 'A')
                    order(sqlFormula("(data->>'name')"))
                }
            }

        then:
            result*.data*.name == ['Ernesto', 'Iván']
    }

    private static TestMapJsonb save(String name, String group) {
        new TestMapJsonb(data: [name: name, group: group]).save(flush: true, failOnError: true)
    }
}
