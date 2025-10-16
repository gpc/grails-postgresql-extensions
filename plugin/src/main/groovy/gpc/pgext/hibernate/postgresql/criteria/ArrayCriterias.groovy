package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import org.hibernate.criterion.Criterion

import grails.orm.HibernateCriteriaBuilder

import gpc.pgext.hibernate.criterion.array.PgArrayExpression
import gpc.pgext.hibernate.criterion.array.PgArrayILikeFunction
import gpc.pgext.hibernate.criterion.array.PgEmptinessExpression

import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyName
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyValue
import static gpc.pgext.hibernate.utils.CriteriaUtils.throwRuntimeException
import static gpc.pgext.hibernate.utils.CriteriaUtils.validateExpression
import static gpc.pgext.hibernate.utils.CriteriaUtils.validateSimpleExpression

@CompileStatic
class ArrayCriterias {

    /**
     * Creates a "contains in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayContains(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgArrayContains', PgArrayExpression, propertyName, propertyValue, '@>')
    }

    /**
     * Creates a "is contained by in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayIsContainedBy(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgArrayIsContainedBy', PgArrayExpression, propertyName, propertyValue, '<@')
    }

    /**
     * Creates a "overlap in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayOverlaps(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgArrayOverlaps', PgArrayExpression, propertyName, propertyValue, '&&')
    }

    /**
     * Creates an "is empty array" Criterion based on the specified property name
     * @param propertyName The property name
     * @return A Criterion instance
     */
    static Criterion pgArrayIsEmpty(HibernateCriteriaBuilder self, String propertyName) {
        if (!validateSimpleExpression(self)) {
            throwRuntimeException(
                    self,
                    "Call to [pgArrayIsEmpty] with propertyName [$propertyName] not allowed here."
            )
        }
        addToCriteria(
                self,
                new PgEmptinessExpression(
                        calculatePropertyName(self, propertyName),
                        '='
                )
        )
    }

    /**
     * Creates an "is not empty array" Criterion based on the specified property name
     * @param propertyName The property name
     * @return A Criterion instance
     */
    static Criterion pgArrayIsNotEmpty(HibernateCriteriaBuilder self, String propertyName) {
        if (!validateSimpleExpression(self)) {
            throwRuntimeException(
                    self,
                    "Call to [pgArrayIsNotEmpty] with propertyName [$propertyName] not allowed here."
            )
        }
        addToCriteria(
                self,
                new PgEmptinessExpression(
                        calculatePropertyName(self, propertyName),
                        '<>'
                )
        )
    }

    /**
     * Creates a "contains in native array" or "is empty native array" Criterion based on the specified property name and value
     * If the propertyValue is empty, the 'contains' operator is used and if the propertyValue is not empty, the 'isEmpty'
     * operator is used.
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayIsEmptyOrContains(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        validateExpression(self, 'pgArrayIsEmptyOrContains', propertyName, propertyValue)
        def name = calculatePropertyName(self, propertyName)
        def value = calculatePropertyValue(self, propertyValue)
        value ?
            addToCriteria(self, new PgArrayExpression(name, value, '@>')) :
            addToCriteria(self, new PgEmptinessExpression(name, '='))
    }

    /**
     * Creates a "equals in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayEquals(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgArrayEquals', PgArrayExpression, propertyName, propertyValue, '=')
    }


    /**
     * Creates a "not equals in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayNotEquals(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, 'pgArrayNotEquals', PgArrayExpression, propertyName, propertyValue, '<>')
    }

    /**
     * Creates a "ilike in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return A Criterion instance
     */
    static Criterion pgArrayILike(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        validateExpression(self, 'pgArrayLike', propertyName, propertyValue)
        addToCriteria(
                self,
                new PgArrayILikeFunction(
                        calculatePropertyName(self, propertyName),
                        calculatePropertyValue(self, propertyValue) as String
                )
        )
    }
}
