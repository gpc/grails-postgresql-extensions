package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Path
import org.hibernate.query.criteria.HibernateCriteriaBuilder as JpaCriteriaBuilder

import grails.orm.HibernateCriteriaBuilder
import org.grails.datastore.mapping.query.api.Criteria

import gpc.pgext.hibernate.criterion.PgCriterion
import gpc.pgext.hibernate.usertype.HstoreHelper

import static gpc.pgext.hibernate.criterion.PgCriterion.sql
import static gpc.pgext.hibernate.criterion.PgCriterion.text
import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyName

@CompileStatic
class HstoreCriterias {

    static Criteria pgHstoreContainsKey(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, valueFunction(calculatePropertyName(self, propertyName), 'exist(?, ?)', propertyValue))
    }

    static Criteria pgHstoreContains(HibernateCriteriaBuilder self, String propertyName, Map<String, String> values) {
        addToCriteria(self, propertyName, operatorExpression(calculatePropertyName(self, propertyName), values, '@>'))
    }

    static Criteria pgHstoreIsContained(HibernateCriteriaBuilder self, String propertyName, Map<String, String> values) {
        addToCriteria(self, propertyName, operatorExpression(calculatePropertyName(self, propertyName), values, '<@'))
    }

    static Criteria pgHstoreILikeValue(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, valueFunction(calculatePropertyName(self, propertyName), 'text(avals(?)) ilike ?', propertyValue))
    }

    private static PgCriterion valueFunction(String propertyName, String sqlFunction, Object value) {
        new PgCriterion(propertyName, { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            sql(cb, sqlFunction, property, text(cb, value))
        } as PgCriterion.PredicateFactory)
    }

    private static PgCriterion operatorExpression(String propertyName, Map<String, String> values, String operator) {
        String hstore = HstoreHelper.toString(values as Map<Object, String>)
        new PgCriterion(propertyName, { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            sql(cb, "? $operator CAST(? AS hstore)", property, cb.value(hstore))
        } as PgCriterion.PredicateFactory)
    }
}
