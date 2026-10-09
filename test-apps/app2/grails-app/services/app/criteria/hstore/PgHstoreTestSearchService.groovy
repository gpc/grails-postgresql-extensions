package app.criteria.hstore

import app.hstore.TestHstoreMap

class PgHstoreTestSearchService {

    List<TestHstoreMap> search(String field, String criteriaName, Object value) {
        TestHstoreMap.withCriteria {
            "$criteriaName"(field, value)
        } as List<TestHstoreMap>
    }
}
