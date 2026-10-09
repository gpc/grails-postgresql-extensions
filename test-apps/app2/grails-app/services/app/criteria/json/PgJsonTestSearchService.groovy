package app.criteria.json

import app.json.TestMapJson

class PgJsonTestSearchService {

    List<TestMapJson> search(String criteriaName, String field, String jsonAttribute, Object value) {
        TestMapJson.withCriteria {
            "$criteriaName"(field, jsonAttribute, value.toString())
        } as List<TestMapJson>
    }

    List<TestMapJson> search(String criteriaName, String field, String jsonOp, String jsonAttribute, String sqlOp, Object value) {
        TestMapJson.withCriteria {
            "$criteriaName"(field, jsonOp, jsonAttribute, sqlOp, value.toString())
        } as List<TestMapJson>
    }
}
