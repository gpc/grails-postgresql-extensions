package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Path
import org.hibernate.query.criteria.HibernateCriteriaBuilder as JpaCriteriaBuilder

import grails.orm.HibernateCriteriaBuilder
import org.grails.datastore.mapping.query.api.Criteria

import gpc.pgext.hibernate.criterion.PgCriterion
import gpc.pgext.hibernate.utils.JsonUtils

import static gpc.pgext.hibernate.criterion.PgCriterion.sql
import static gpc.pgext.hibernate.criterion.PgCriterion.text
import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyName

@CompileStatic
class JsonCriterias {

    /**
     * Creates a "json has field value" Criterion based on the specified property name and value
     * @param propertyName The property name (json field)
     * @param jsonAttribute The json attribute
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgJsonHasFieldValue(HibernateCriteriaBuilder self, String propertyName, String jsonAttribute, Object propertyValue) {
        addToCriteria(self, propertyName, jsonExpression(calculatePropertyName(self, propertyName), '->>', jsonAttribute, '=', propertyValue))
    }

    /**
     * Creates a "json contains another json" Criterion based on the specified property name and value
     * @param propertyName The property name (jsonb field)
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgJsonbContains(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, jsonbOperator(calculatePropertyName(self, propertyName), propertyValue, '@>'))
    }

    /**
     * Creates a "json is contained in another json" Criterion based on the specified property name and value
     * @param propertyName The property name (jsonb field)
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgJsonbIsContained(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, jsonbOperator(calculatePropertyName(self, propertyName), propertyValue, '<@'))
    }

    /**
     * Creates a "json <condition> on field value" Criterion based on the specified property name and value
     * @param propertyName The property name (json field)
     * @param jsonAttribute The json attribute
     * @param jsonOp The json operator (->>, #>, ...)
     * @param propertyValue The property value
     * @param sqlOp The sql operator (=, <>, ilike, ...)
     * @return The criteria
     */
    static Criteria pgJson(HibernateCriteriaBuilder self, String propertyName, String jsonOp, String jsonAttribute, String sqlOp, Object propertyValue) {
        addToCriteria(self, propertyName, jsonExpression(calculatePropertyName(self, propertyName), jsonOp, jsonAttribute, sqlOp, propertyValue))
    }

    private static PgCriterion jsonExpression(String propertyName, String jsonOp, String jsonAttribute, String sqlOp, Object value) {
        String attribute = jsonAttribute.replace("'", "''")
        new PgCriterion(propertyName, { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            sql(cb, "?$jsonOp'$attribute' $sqlOp ?", property, text(cb, value))
        } as PgCriterion.PredicateFactory)
    }

    private static PgCriterion jsonbOperator(String propertyName, Object value, String op) {
        String json = JsonUtils.toJson(value)
        new PgCriterion(propertyName, { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            sql(cb, "? $op CAST(? AS jsonb)", property, cb.value(json))
        } as PgCriterion.PredicateFactory)
    }
}
