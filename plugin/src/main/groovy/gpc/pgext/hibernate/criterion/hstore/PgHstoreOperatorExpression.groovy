package gpc.pgext.hibernate.criterion.hstore

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Criterion
import org.hibernate.engine.spi.TypedValue

import gpc.pgext.hibernate.usertype.HstoreHelper

@CompileStatic
class PgHstoreOperatorExpression implements Criterion {

    private static final long serialVersionUID = 2872183637309166619L

    private final String propertyName
    private final Map<Object, String> value
    private final String operator
    private static final TypedValue[] NO_VALUES = new TypedValue[0]

    PgHstoreOperatorExpression(String propertyName, Object value, String operator) {
        this(propertyName, value as Map<Object, String>, operator)
    }

    PgHstoreOperatorExpression(String propertyName, Map<Object, String> value, String operator) {
        this.propertyName = propertyName
        this.value = value
        this.operator = operator
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        criteriaQuery.findColumns(propertyName, criteria)
                .collect { "$it $operator '${HstoreHelper.toString(value)}'" }
                .join(' and ')
    }

    @Override
    TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        NO_VALUES
    }
}
