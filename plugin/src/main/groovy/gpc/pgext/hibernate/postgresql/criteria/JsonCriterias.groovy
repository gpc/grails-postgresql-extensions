package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import org.hibernate.criterion.Criterion

import grails.orm.HibernateCriteriaBuilder

import gpc.pgext.hibernate.criterion.json.PgJsonExpression
import gpc.pgext.hibernate.criterion.json.PgJsonbOperator

import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyName
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyValue
import static gpc.pgext.hibernate.utils.CriteriaUtils.throwRuntimeException
import static gpc.pgext.hibernate.utils.CriteriaUtils.validateSimpleExpression

@CompileStatic
class JsonCriterias {

    /**
     * Creates a "json has field value" Criterion based on the specified property name and value
     * @param propertyName The property name (json field)
     * @param jsonAttribute The json attribute
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgJsonHasFieldValue(HibernateCriteriaBuilder self, String propertyName, String jsonAttribute, Object propertyValue) {
        if (!validateSimpleExpression(self)) {
            throwRuntimeException(
                    self,
                    "Call to [pgJsonHasFieldValue] with propertyName [$propertyName], jsonAttribute [$jsonAttribute] and value [$propertyValue] is not allowed here."
            )
        }
        addToCriteria(
                self,
                new PgJsonExpression(
                        calculatePropertyName(self, propertyName),
                        '->>',
                        jsonAttribute,
                        '=',
                        calculatePropertyValue(self, propertyValue) as String
                )
        )
    }

    /**
     * Creates a "json contains another json" Criterion based on the specified property name and value
     * @param propertyName The property name (jsonb field)
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgJsonbContains(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgJsonbContains', PgJsonbOperator, propertyName, propertyValue, '@>')
    }

    /**
     * Creates a "json is contained in another json" Criterion based on the specified property name and value
     * @param propertyName The property name (jsonb field)
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgJsonbIsContained(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgJsonbIsContained', PgJsonbOperator, propertyName, propertyValue, '<@')
    }

    /**
     * Creates a "json <condition> on field value" Criterion based on the specified property name and value
     * @param propertyName The property name (json field)
     * @param jsonAttribute The json attribute
     * @param jsonOp The json operator (->>, #>, ...)
     * @param propertyValue The property value
     * @param sqlOp The sql operator (=, <>, ilike, ...)
     * @return A Criterion instance
     */
    static Criterion pgJson(HibernateCriteriaBuilder self, String propertyName, String jsonOp, String jsonAttribute, String sqlOp, Object propertyValue) {
        if (!validateSimpleExpression(self)) {
            throwRuntimeException(
                    self,
                    "Call to [pgJson] with propertyName [$propertyName], json operator [$jsonOp], jsonAttribute [$jsonAttribute], sql operator [$sqlOp] and value [$propertyValue] is not allowed here."
            )
        }
        addToCriteria(
                self,
                new PgJsonExpression(
                        calculatePropertyName(self, propertyName),
                        jsonOp,
                        jsonAttribute,
                        sqlOp,
                        calculatePropertyValue(self, propertyValue) as String
                )
        )
    }
}
