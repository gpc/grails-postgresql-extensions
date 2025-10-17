package gpc.pgext.hibernate.criterion.array

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Criterion
import org.hibernate.engine.spi.TypedValue
import org.hibernate.type.CustomType

import gpc.pgext.hibernate.usertype.ArrayType
import gpc.pgext.hibernate.utils.PgArrayUtils

@CompileStatic
class PgArrayExpression implements Criterion {

    private static final long serialVersionUID = 2872183637309166619L

    private final String propertyName
    private final Object value
    private final String op

    private static final PgArrayUtils.MapFunction MAP_TO_ENUM = new PgArrayUtils.MapFunction() {
        @Override
        Object map(Object o) {
            try {
                return ((Enum) o).ordinal()
            } catch (ClassCastException e) {
                throw new HibernateException("Unable to cast object $o to Enum", e)
            }
        }
    }

    PgArrayExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName
        this.value = value
        this.op = op
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        def arrayType = checkAndGetArrayType(criteria, criteriaQuery, propertyName)
        def postgresArrayType = PgArrayUtils.getNativeSqlType(arrayType.typeClass) + '[]'
        criteriaQuery.findColumns(propertyName, criteria)
                .collect { "$it $op CAST(? as $postgresArrayType)" }
                .join(' and ')
    }

    @Override
    TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        def arrayType = checkAndGetArrayType(criteria, criteriaQuery, propertyName)
        def arrValue = arrayType.typeClass.isEnum() ?
                PgArrayUtils.getValueAsArrayOfType(value, Integer, MAP_TO_ENUM) :
                PgArrayUtils.getValueAsArrayOfType(value, arrayType.typeClass)
        criteriaQuery.getTypedValue(criteria, propertyName, arrValue) as TypedValue[]
    }

    private static ArrayType checkAndGetArrayType(Criteria criteria, CriteriaQuery criteriaQuery, String propertyName) {
        def propertyType = criteriaQuery.getType(criteria, propertyName)
        if (!(propertyType instanceof CustomType) || !(((CustomType) propertyType).userType instanceof ArrayType)) {
            throw new HibernateException("Property is not an instance of the postgres type ArrayType. Type is: $propertyType.class")
        }
        (propertyType as CustomType).userType as ArrayType
    }
}
