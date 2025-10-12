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

    PgArrayExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName
        this.value = value
        this.op = op
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        def arrayType = checkAndGetArrayType(criteria, criteriaQuery)
        def postgresArrayType = PgArrayUtils.getNativeSqlType(arrayType.getTypeClass()) + '[]'
        criteriaQuery.findColumns(propertyName, criteria)
                .collect {"$it $op CAST(? as $postgresArrayType)" }
                .join(' and ')
    }

    @Override
    TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        def arrayType = checkAndGetArrayType(criteria, criteriaQuery)
        def arrValue = arrayType.typeClass.isEnum() ?
                PgArrayUtils.getValueAsArrayOfType(value, Integer, mapValueToEnumOrdinal()) :
                PgArrayUtils.getValueAsArrayOfType(value, arrayType.typeClass)
        criteriaQuery.getTypedValue(criteria, propertyName, arrValue) as TypedValue[]
    }

    private PgArrayUtils.MapFunction mapValueToEnumOrdinal() {
        return { Object o ->
            try {
                return (o as Enum).ordinal()
            } catch (ClassCastException e) {
                throw new HibernateException("Unable to cast object $o to Enum", e)
            }
        } as PgArrayUtils.MapFunction
    }

    private ArrayType checkAndGetArrayType(Criteria criteria, CriteriaQuery criteriaQuery) {
        def propertyType = criteriaQuery.getType(criteria, propertyName)
        if (!(propertyType instanceof CustomType) || !((propertyType as CustomType).userType instanceof ArrayType)) {
            throw new HibernateException("Property is not an instance of the postgres type ArrayType. Type is: $propertyType.class")
        }
        (propertyType as CustomType).userType as ArrayType
    }
}
