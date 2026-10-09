package gpc.pgext.hibernate.criterion.json

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Criterion
import org.hibernate.engine.spi.TypedValue
import org.hibernate.type.StringType

@CompileStatic
class PgJsonExpression implements Criterion {

    private static final long serialVersionUID = 8372629374639273L

    private final String propertyName
    private final String jsonOp
    private final String jsonAttribute
    private final String sqlOp
    private final Object value

    PgJsonExpression(String propertyName, String jsonOp, String jsonAttribute, String sqlOp, Object value) {
        this.propertyName = propertyName
        this.jsonOp = jsonOp
        this.jsonAttribute = jsonAttribute
        this.sqlOp = sqlOp
        this.value = value
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        criteriaQuery.findColumns(propertyName, criteria)
                .collect { "$it$jsonOp'$jsonAttribute' $sqlOp ?" }
                .join(' and ')
    }

    @Override
    TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        new TypedValue(new StringType(), value) as TypedValue[]
    }

    @Override
    String toString() {
        return "$propertyName $jsonOp '$jsonAttribute' $sqlOp '$value'"
    }
}
