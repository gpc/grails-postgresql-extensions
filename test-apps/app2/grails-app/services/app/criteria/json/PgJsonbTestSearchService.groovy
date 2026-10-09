package app.criteria.json

import app.json.TestMapJsonb

class PgJsonbTestSearchService {

    List<TestMapJsonb> search(String criteriaName, String field, Object value) {
        TestMapJsonb.withCriteria {
            "$criteriaName"(field, value)
        } as List<TestMapJsonb>
    }

    List<TestMapJsonb> search(String criteriaName, String field, String jsonAttribute, Object value) {
        TestMapJsonb.withCriteria {
            "$criteriaName"(field, jsonAttribute, value.toString())
        } as List<TestMapJsonb>
    }

    List<TestMapJsonb> search(String criteriaName, String field, String jsonOp, String jsonAttribute, String sqlOp, Object value) {
        TestMapJsonb.withCriteria {
            "$criteriaName"(field, jsonOp, jsonAttribute, sqlOp, value.toString())
        } as List<TestMapJsonb>
    }
}
