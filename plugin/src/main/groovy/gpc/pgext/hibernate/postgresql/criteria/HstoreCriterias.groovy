package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import org.hibernate.criterion.Criterion

import grails.orm.HibernateCriteriaBuilder

import gpc.pgext.hibernate.criterion.hstore.PgHstoreILikeValueFunction
import gpc.pgext.hibernate.criterion.hstore.PgHstoreOperatorExpression
import gpc.pgext.hibernate.criterion.hstore.PgHstoreValueFunction

import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria

@CompileStatic
class HstoreCriterias {

    static Criterion pgHstoreContainsKey(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgHstoreContainsKey', PgHstoreValueFunction, propertyName, propertyValue, 'exist')
    }

    static Criterion pgHstoreContains(HibernateCriteriaBuilder self, String propertyName, Map<String, String> values) {
        addToCriteria(self, 'pgHstoreContains', PgHstoreOperatorExpression, propertyName, values, '@>')
    }

    static Criterion pgHstoreIsContained(HibernateCriteriaBuilder self, String propertyName, Map<String, String> values) {
        addToCriteria(self, 'pgHstoreIsContained', PgHstoreOperatorExpression, propertyName, values, '<@')
    }

    static Criterion pgHstoreILikeValue(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgHstoreILikeValue', PgHstoreILikeValueFunction, propertyName, propertyValue)
    }
}
