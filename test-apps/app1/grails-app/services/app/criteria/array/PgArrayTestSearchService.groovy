package app.criteria.array

class PgArrayTestSearchService {

    List<Like> search(String field, String criteriaName, Object value) {
        Like.withCriteria {
            "$criteriaName"(field, value)
        } as List<Like>
    }

    List<Like> search(String field, String criteriaName) {
        Like.withCriteria {
            "$criteriaName"(field)
        } as List<Like>
    }

    List<User> searchWithJoin(String field, String criteriaName, Object value) {
        User.withCriteria {
            createAlias('like', 'l')
            "$criteriaName"('l.' + field, value)
        } as List<User>
    }

    List<User> searchWithJoin(String field, String criteriaName) {
        User.withCriteria {
            createAlias('like', 'l')
            "$criteriaName"('l.' + field)
        } as List<User>
    }

    List<User> searchWithJoinByStringOrInteger(Map params, String criteriaName) {
        User.withCriteria {
            createAlias('like', 'l')
            or {
                params.each { k, v ->
                    "$criteriaName"('l.' + k, v)
                }
            }
        } as List<User>
    }

    List<User> searchWithJoinAnd(Map params, String criteriaName) {
        User.withCriteria {
            createAlias('like', 'l')
            and {
                params.each { k, v ->
                    "$criteriaName"('l.' + k, v)
                }
            }
        } as List<User>
    }
}
