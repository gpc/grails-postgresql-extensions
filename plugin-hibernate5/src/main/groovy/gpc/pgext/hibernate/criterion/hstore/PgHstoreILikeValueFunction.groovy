package gpc.pgext.hibernate.criterion.hstore

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery

@CompileStatic
class PgHstoreILikeValueFunction extends PgHstoreValueFunction {

    PgHstoreILikeValueFunction(String propertyName, Object value) {
        super(propertyName, value, '')
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        criteriaQuery.findColumns(propertyName, criteria)
                .collect { "text(avals($it)) ilike ?" }
                .join(' and ')
    }
}
