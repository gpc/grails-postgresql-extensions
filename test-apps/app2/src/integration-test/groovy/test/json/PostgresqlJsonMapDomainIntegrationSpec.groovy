package test.json

import app.json.TestMapJson
import spock.lang.Specification
import spock.lang.Unroll

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PostgresqlJsonMapDomainIntegrationSpec extends Specification {

    def setup() {
        TestMapJson.executeUpdate('delete from TestMapJson')
    }

    @Unroll
    void 'save and read a domain class with a map #map to json'() {
        setup:
            def testMapJson = new TestMapJson(data: map)

        when:
            // Domain saving and retrieving should be in different sessions. Only in that case Hibernate will invoke
            // nullSafeGet on the corresponding user type and will not use current session's cache.
            TestMapJson.withNewTransaction {
                testMapJson.save(flush: true, failOnError: true)
            }

        then:
            testMapJson.hasErrors() == false

        and:
            def obj = testMapJson.get(testMapJson.id)
            obj.data == map

        where:
            map << [null, [:], [name: 'Ivan', age: 34]]
    }

    void 'save and read a domain class with json'() {
        setup:
            def value = [name: 'Ivan', age: 34, hasChildren: true, children: [[name: 'Judith', age: 7], [name: 'Adriana', age: 4]]]
            def testMapJson = new TestMapJson(data: value)

        when:
            testMapJson.save(flush: true, failOnError: true)

        then:
            testMapJson.hasErrors() == false

        and:
            def obj = testMapJson.get(testMapJson.id)
            obj.data.keySet().collect { it.toString() } == ['name', 'age', 'hasChildren', 'children']
            obj.data.children.size() == 2
    }
}