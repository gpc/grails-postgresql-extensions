package gpc.pgext.hibernate.usertype

import spock.lang.Specification
import spock.lang.Unroll

class HstoreParserSpec extends Specification {

    @Unroll
    void 'AsMap with value populated by constructor'() {
        expect:
        def parser = new HstoreParser(input)
        def map = parser.asMap()
        map[expected_key] == expected_value

        where:
        example << escapedCharactersExamples()
        input = example.input
        expected_key = example.expected_key
        expected_value = example.expected_value
    }

    @Unroll
    void 'AsMap with value populated by setValue'() {
        given:
        def parser = new HstoreParser('')
        parser.value = input

        expect:
        def map = parser.asMap()
        map[expected_key] == expected_value

        where:
        example << escapedCharactersExamples()
        input = example.input
        expected_key = example.expected_key
        expected_value = example.expected_value
    }

    def escapedCharactersExamples() {
        return [
                // insert into test(hs) values(hstore(ARRAY['key','"value"']));
                [
                        input         : /"key"=>"\"value\""/,
                        expected_key  : /key/,
                        expected_value: /"value"/
                ],
                // insert into test(hs) values(hstore(ARRAY['\key','value']));
                [
                        input         : /"\\key"=>"value"/,
                        expected_key  : /\key/,
                        expected_value: /value/
                ],
                // insert into test(hs) values(hstore(ARRAY['''key'''','value']))
                [
                        input         : /"'key'"=>"value"/,
                        expected_key  : /'key'/,
                        expected_value: /value/
                ],
                // nested hstore
                [
                        input         : /"key"=>"\"nested_key\"=>\"1.1\""/,
                        expected_key  : /key/,
                        expected_value: /"nested_key"=>"1.1"/
                ]
        ]
    }
}
