package test.hstore

import app.hstore.TestHstoreMap
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Unroll

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PostgresqlHstoreMapDomainIntegrationSpec extends Specification {

    def setup() {
        TestHstoreMap.executeUpdate('delete from TestHstoreMap')
    }

    @Unroll
    void 'save a domain class with a map. key: #data'() {
        setup:
            def testHstoreMap = new TestHstoreMap(testAttributes: data)

        when:
            testHstoreMap.save(flush: true, failOnError: true)

        then:
            !testHstoreMap.hasErrors()
            testHstoreMap.testAttributes != null
            testHstoreMap.testAttributes.size() == data.size()
            testHstoreMap.testAttributes[attribute] == value

        where:
            data                   | attribute | value
            [foo: 'bar']           | 'foo'     | 'bar'
            ['foo,bar': 'baz,qux'] | 'foo,bar' | 'baz,qux'
    }

    @Unroll
    void 'recover a domain class with a map. key: #data'() {
        setup:
            new TestHstoreMap(testAttributes: data).save(flush: true, failOnError: true)

        when:
            def testHstoreMap = TestHstoreMap.findAll().first()

        then:
            !testHstoreMap.hasErrors()
            testHstoreMap.testAttributes != null
            testHstoreMap.testAttributes.size() == data.size()
            testHstoreMap.testAttributes[key] == value

        where:
            data                     | key       | value
            [foo: 'bar', xxx: 'abc'] | 'foo'     | 'bar'
            ['foo,bar': 'baz,qux']   | 'foo,bar' | 'baz,qux'
    }

    @Unroll
    void 'remove a key in a map. key: #data, valueToRemove: #valueToRemove'() {
        setup:
            def testHstoreMap = new TestHstoreMap(testAttributes: data)
            testHstoreMap.save(flush: true, failOnError: true)

        when:
            testHstoreMap.testAttributes.remove(valueToRemove)

        then:
            !testHstoreMap.hasErrors()
            testHstoreMap.testAttributes != null
            testHstoreMap.testAttributes.size() == size

        where:
            data                     | valueToRemove | size
            [foo: 'bar', xxx: 'abc'] | 'xxx'         | 1
            ['foo,bar': 'baz,qux']   | 'foo,bar'     | 0
            [foo: 'bar']             | 'xxx'         | 1
    }

    @Unroll
    void 'save and delete a domain class with a map. key: #data'() {
        setup:
            def testHstoreMap = new TestHstoreMap(testAttributes: data)

        when: 'I save an instance'
            testHstoreMap.save(flush: true, failOnError: true)

        and: 'The instance is saved'
            assert !testHstoreMap.hasErrors()

        and: 'I try to delete it'
            testHstoreMap.delete(flush: true, failOnError: true)

        then: 'It shouldn\'t be present in database anymore'
            TestHstoreMap.count() == 0

        where:
            data                   | attribute | value
            [foo: 'bar']           | 'foo'     | 'bar'
            ['foo,bar': 'baz,qux'] | 'foo,bar' | 'baz,qux'
    }

    void 'save a domain class with a empty map and validate that is not dirty right after retrieval'() {
        setup:
            def testHstoreMap = new TestHstoreMap(testAttributes: [:])

        when: 'I save an instance'
            testHstoreMap.save(flush: true, failOnError: true)

        and: 'The instance is saved'
            assert !testHstoreMap.hasErrors()

        and: 'I retrieve it and check for dirty properties in a new session'
            def retrievedTestHstoreMap = TestHstoreMap.get(testHstoreMap.id)

        then: 'It should not be dirty right after db retrieval'
            !retrievedTestHstoreMap.isDirty()
    }

    @PendingFeature(reason = 'seems dirty check does not work for Grails 3.3 with Hibernate 5.2 and GORM 6.1.9')
    void 'save a domain class, modify it and validate that it is dirty'() {
        setup:
            def testHstoreMap = new TestHstoreMap(testAttributes: [:])

        when: 'I save an instance'
            testHstoreMap.save(flush: true, failOnError: true)

        and: 'The instance is saved'
            assert !testHstoreMap.hasErrors()

        and: 'I retrieve it and modify a property'
            def retrievedTestHstoreMap = TestHstoreMap.get(testHstoreMap.id)
            retrievedTestHstoreMap.testAttributes << [foo: 'bar']

        then: 'It should be dirty'
            retrievedTestHstoreMap.isDirty()
    }
}
