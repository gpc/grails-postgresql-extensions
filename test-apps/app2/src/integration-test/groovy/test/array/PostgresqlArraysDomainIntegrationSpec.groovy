package test.array

import app.array.TestDouble
import app.array.TestEnum
import app.array.TestFloat
import app.array.TestInteger
import app.array.TestLong
import app.array.TestString
import app.array.TestUuid
import spock.lang.Specification
import spock.lang.Unroll

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PostgresqlArraysDomainIntegrationSpec extends Specification {

    def setup() {
        TestInteger.executeUpdate('delete from TestInteger')
        TestLong.executeUpdate('delete from TestLong')
        TestFloat.executeUpdate('delete from TestFloat')
        TestDouble.executeUpdate('delete from TestDouble')
        TestUuid.executeUpdate('delete from TestUuid')
        TestString.executeUpdate('delete from TestString')
        TestEnum.executeUpdate('delete from TestEnum')
    }

    @Unroll
    void 'save a domain class with an integer array value #numbers'() {
        setup:
            def testInt = new TestInteger(integerNumbers: numbers)

        when:
            testInt.save(flush: true, failOnError: true)

        then:
            testInt.hasErrors() == false
            testInt.integerNumbers?.length == numbers?.size()

        where:
            numbers << [null, [], [5], [3, -1], [-9, 4, -123, 0]]
    }

    @Unroll
    void 'save a domain class with a long array value #numbers'() {
        setup:
            def testLong = new TestLong(longNumbers: numbers)

        when:
            testLong.save(flush: true, failOnError: true)

        then:
            testLong.hasErrors() == false
            testLong.longNumbers?.length == numbers?.size()

        where:
            numbers << [null, [], [5L], [3L, -1L], [-9L, 4L, -123L, 0L]]
    }

    @Unroll
    void 'save a domain class with a Float array value #numbers'() {
        setup:
            def testFloat = new TestFloat(floatNumbers: numbers)

        when:
            testFloat.save(flush: true, failOnError: true)

        then:
            testFloat.hasErrors() == false
            testFloat.floatNumbers?.length == numbers?.size()

        where:
            numbers << [null, [], [5f], [3f, -1f], [-9f, 4f, -123f, 0f]]
    }

    @Unroll
    void 'save a domain class with a Double array value #numbers'() {
        setup:
            def testDouble = new TestDouble(doubleNumbers: numbers)

        when:
            testDouble.save(flush: true, failOnError: true)

        then:
            testDouble.hasErrors() == false
            testDouble.doubleNumbers?.length == numbers?.size()

        where:
            numbers << [null, [], [5d], [3d, -1d], [-9d, 4d, -123d, 0d]]
    }

    @Unroll
    void 'save a domain class with an string array value #strings'() {
        setup:
            def testString = new TestString(stringArray: strings)

        when:
            testString.save(flush: true, failOnError: true)

        then:
            testString.hasErrors() == false
            testString.stringArray?.length == strings?.size()

        where:
            strings << [null, [], ['string 1'], ['string 1', 'string 2'], ['string 1', 'string 2', 'string 3']]
    }

    @Unroll
    void 'save a domain class with an UUID array value #uuids'() {
        setup:
            def testUuid = new TestUuid(uuidArray: uuids)

        when:
            testUuid.save(flush: true, failOnError: true)

        then:
            testUuid.hasErrors() == false
            testUuid.uuidArray?.length == uuids?.size()

        where:
            uuids << [null, [], [UUID.randomUUID()], [UUID.randomUUID(), UUID.randomUUID()], [UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()]]
    }

    @Unroll
    void 'save a domain class with an enum array value #days'() {
        when:
            // Domain saving and retrieving should be in different sessions. Only in that case Hibernate will invoke
            // nullSafeGet on the corresponding user type and will not use current session's cache.
            def id = TestEnum.withNewTransaction {
                new TestEnum(days: days).save(flush: true, failOnError: true).id
            }

        then:
            TestEnum.get(id).days as List == days

        where:
            days << [null, [], [TestEnum.Day.MONDAY], [TestEnum.Day.SUNDAY, TestEnum.Day.SATURDAY], [TestEnum.Day.WEDNESDAY, TestEnum.Day.THURSDAY, TestEnum.Day.TUESDAY]]
    }
}
