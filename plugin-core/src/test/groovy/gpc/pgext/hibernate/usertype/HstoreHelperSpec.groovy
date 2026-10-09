package gpc.pgext.hibernate.usertype

import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Unroll

class HstoreHelperSpec extends Specification {

    @Unroll
    void 'empty and null map to string'() {
        setup:
            def m = value

        expect:
            HstoreHelper.toString(m) == ''

        where:
            value << [[:], null]
    }

    void 'non empty map to string'() {
        setup:
            def m = [:]
            m.foo = 'bar'

        expect:
            HstoreHelper.toString(m) == '"foo"=>"bar"'
    }

    void 'transform map with double quotes'() {
        setup:
            def m = [:]
            m['Test "thing"'] = 'bar'

        expect:
            HstoreHelper.toString(m) == '"Test \'thing\'"=>"bar"'
    }

    void 'map with two values to string'() {
        setup:
            def m = [:]
            m.foo = 'bar'
            m.xxx = 'Groovy Rocks!'

        expect:
            HstoreHelper.toString(m) == '"foo"=>"bar", "xxx"=>"Groovy Rocks!"'
    }

    @Unroll
    void 'map with different non-string types values to string. value: #value'() {
        setup:
            def m = [:]
            m.prop = value

        expect:
            HstoreHelper.toString(m) == "\"prop\"=>\"$value\""

        where:
            value << [123, true, null, 999L, new Date(), 87987.8976]
    }

    void 'map with key and value that contains a comma'() {
        setup:
            def m = [:]
            m['foo,bar'] = 'baz,qux'

        expect:
            HstoreHelper.toString(m) == '"foo,bar"=>"baz,qux"'
    }

    void 'map with key and value that contains a comma and space'() {
        setup:
            def m = [:]
            m['foo, bar'] = 'baz, qux'

        expect:
            HstoreHelper.toString(m) == '"foo, bar"=>"baz, qux"'
    }

    @Unroll
    void 'Test asStatement. map: #map'() {
        when:
            def result = HstoreHelper.asStatement(map)

        then:
            result == expected

        where:
            map << [null, [:], [a: 'b'], [a: 'b', c: 'd'], [a: 'b', c: 'd', e: 'f'], ['foo,bar': 'baz,qux']]
            expected << ['', '', '"?"=>"?"', '"?"=>"?", "?"=>"?"', '"?"=>"?", "?"=>"?", "?"=>"?"', '"?"=>"?"']
    }

    @Unroll
    void 'Test asListKeyValue. value: #map'() {
        when:
            def result = HstoreHelper.asListKeyValue(map)

        then:
            result == expected

        where:
            map << [null, [:], [a: 'b'], [a: 'b', c: 'd'], [a: 'b', c: 'd', e: 'f'], ['foo,bar': 'baz,qux']]
            expected << [[], [], ['a', 'b'], ['a', 'b', 'c', 'd'], ['a', 'b', 'c', 'd', 'e', 'f'], ['foo,bar', 'baz,qux']]
    }

    @Unroll
    @Issue('https://github.com/gpc/grails-postgresql-extensions/issues/25')
    void 'map with key of type different to String. key: #key'() {
        setup:
            def m = [:]
            m[key] = 'value'

        expect:
            HstoreHelper.toString(m) == "\"$key\"=>\"value\""

        where:
            key << [123, true, 999L, new Date(), 87987.8976]
    }
}
