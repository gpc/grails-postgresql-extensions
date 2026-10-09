package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.hibernate.HibernateException
import org.hibernate.query.criteria.HibernateCriteriaBuilder as JpaCriteriaBuilder

import grails.orm.HibernateCriteriaBuilder
import org.grails.datastore.mapping.query.api.Criteria

import gpc.pgext.hibernate.criterion.PgCriterion
import gpc.pgext.hibernate.utils.PgArrayUtils

import static gpc.pgext.hibernate.criterion.PgCriterion.sql
import static gpc.pgext.hibernate.criterion.PgCriterion.text
import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria
import static gpc.pgext.hibernate.utils.CriteriaUtils.calculatePropertyName

@CompileStatic
class ArrayCriterias {

    private static final PgArrayUtils.MapFunction MAP_TO_ENUM = new PgArrayUtils.MapFunction() {
        @Override
        Object map(Object o) {
            if (o instanceof Enum) {
                return ((Enum) o).ordinal()
            }
            throw new HibernateException("Unable to cast object $o to Enum")
        }
    }

    /**
     * Creates a "contains in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayContains(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, arrayExpression(calculatePropertyName(self, propertyName), propertyValue, '@>'))
    }

    /**
     * Creates a "is contained by in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayIsContainedBy(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, arrayExpression(calculatePropertyName(self, propertyName), propertyValue, '<@'))
    }

    /**
     * Creates a "overlap in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayOverlaps(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, arrayExpression(calculatePropertyName(self, propertyName), propertyValue, '&&'))
    }

    /**
     * Creates an "is empty array" Criterion based on the specified property name
     * @param propertyName The property name
     * @return The criteria
     */
    static Criteria pgArrayIsEmpty(HibernateCriteriaBuilder self, String propertyName) {
        addToCriteria(self, propertyName, emptinessExpression(calculatePropertyName(self, propertyName), '='))
    }

    /**
     * Creates an "is not empty array" Criterion based on the specified property name
     * @param propertyName The property name
     * @return The criteria
     */
    static Criteria pgArrayIsNotEmpty(HibernateCriteriaBuilder self, String propertyName) {
        addToCriteria(self, propertyName, emptinessExpression(calculatePropertyName(self, propertyName), '<>'))
    }

    /**
     * Creates a "contains in native array" or "is empty native array" Criterion based on the specified property name and value
     * If the propertyValue is empty, the 'contains' operator is used and if the propertyValue is not empty, the 'isEmpty'
     * operator is used.
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayIsEmptyOrContains(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        def name = calculatePropertyName(self, propertyName)
        propertyValue ?
            addToCriteria(self, propertyName, arrayExpression(name, propertyValue, '@>')) :
            addToCriteria(self, propertyName, emptinessExpression(name, '='))
    }

    /**
     * Creates a "equals in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayEquals(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, arrayExpression(calculatePropertyName(self, propertyName), propertyValue, '='))
    }

    /**
     * Creates a "not equals in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayNotEquals(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        addToCriteria(self, propertyName, arrayExpression(calculatePropertyName(self, propertyName), propertyValue, '<>'))
    }

    /**
     * Creates a "ilike in native array" Criterion based on the specified property name and value
     * @param propertyName The property name
     * @param propertyValue The property value
     * @return The criteria
     */
    static Criteria pgArrayILike(HibernateCriteriaBuilder self, String propertyName, Object propertyValue) {
        String value = (String) propertyValue
        addToCriteria(self, propertyName, new PgCriterion(calculatePropertyName(self, propertyName), { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            sql(cb, 'text(?) ilike ?', property, text(cb, value))
        } as PgCriterion.PredicateFactory))
    }

    private static PgCriterion arrayExpression(String propertyName, Object value, String op) {
        new PgCriterion(propertyName, { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            Class<?> typeClass = arrayTypeClass(property)
            Object[] arrayValue = toArrayOfType(value, typeClass)
            sql(cb, "? $op CAST(? as ${PgArrayUtils.getNativeSqlType(typeClass)}[])", property, cb.value(arrayValue))
        } as PgCriterion.PredicateFactory)
    }

    private static PgCriterion emptinessExpression(String propertyName, String op) {
        new PgCriterion(propertyName, { AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            arrayTypeClass(property)
            sql(cb, "? $op '{}'", property)
        } as PgCriterion.PredicateFactory)
    }

    private static Object[] toArrayOfType(Object value, Class<?> typeClass) {
        try {
            if (typeClass.isEnum()) {
                // Enums are stored by their id, so arrays of enums are converted too
                def values = value instanceof Object[] ? ((Object[]) value).toList() : value
                return PgArrayUtils.getValueAsArrayOfType(values, Integer, MAP_TO_ENUM)
            }
            PgArrayUtils.getValueAsArrayOfType(value, typeClass)
        } catch (IllegalArgumentException e) {
            throw new HibernateException(e.message, e)
        }
    }

    private static Class<?> arrayTypeClass(Path<?> property) {
        Class<?> javaType = property.javaType
        if (!javaType?.isArray()) {
            throw new HibernateException("Property is not an instance of the postgres type ArrayType. Type is: $javaType")
        }
        javaType.componentType
    }
}
