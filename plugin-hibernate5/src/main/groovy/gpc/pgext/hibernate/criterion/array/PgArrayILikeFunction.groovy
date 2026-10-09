package gpc.pgext.hibernate.criterion.array

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Criterion
import org.hibernate.engine.spi.TypedValue
import org.hibernate.type.StringType

@CompileStatic
class PgArrayILikeFunction implements Criterion {

    private static final long serialVersionUID = 7475136611436979257L

    private final String propertyName
    private final String value

    PgArrayILikeFunction(String propertyName, String value) {
        this.propertyName = propertyName
        this.value = value
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        criteriaQuery.findColumns(propertyName, criteria)
                .collect { "text($it) ilike ?" }
                .join(' and ')
    }

    @Override
    TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        new TypedValue(new StringType(), value) as TypedValue[]
    }
}
