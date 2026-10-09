package gpc.pgext.hibernate.criterion.hstore

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Criterion
import org.hibernate.engine.spi.TypedValue
import org.hibernate.type.StringType

@CompileStatic
class PgHstoreValueFunction implements Criterion {

    private static final long serialVersionUID = 2872183637309166619L

    protected final String propertyName
    protected final Object value
    protected final String function

    PgHstoreValueFunction(String propertyName, Object value, String function) {
        this.propertyName = propertyName
        this.value = value
        this.function = function
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        criteriaQuery.findColumns(propertyName, criteria)
                .collect { "$function($it, ?)" }
                .join(' and ')
    }

    @Override
    TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        new TypedValue(new StringType(), value) as TypedValue[]
    }
}
