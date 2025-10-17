package gpc.pgext.hibernate.utils

import java.lang.reflect.Array

import groovy.transform.CompileStatic

import org.hibernate.HibernateException

/**
 * Utils for the different criteria queries.
 */
@CompileStatic
class PgArrayUtils {

    private static final Map<Class<?>, String> CLASS_TO_TYPE_NAME = [
        (Integer): 'int4',
        (Long)   : 'int8',
        (String) : 'varchar',
        (Float)  : 'float4',
        (Double) : 'float8',
        (UUID)   : 'uuid',
    ] as Map<Class<?>, String>

    /**
     * Returns a new array wrapping the parameter value. The type of the array
     * will be the type passed as parameter
     *
     * @param targetValue The value we want to wrap as an array
     * @param expectedType The expected type of the returned array
     * @param mapFunction If non-null, it will transform each object in the array to a given object.
     * @return an array wrapping the parameter value
     */
    static Object[] getValueAsArrayOfType(Object targetValue, Class expectedType, MapFunction mapFunction) {
        if (targetValue instanceof Object[]) {
            return (Object[]) targetValue
        }

        def items = (targetValue instanceof Collection) ? (targetValue as List) : [targetValue]
        def converted = items.collect { o ->
            if (expectedType.isInstance(o)) {
                return o
            }
            if (mapFunction) {
                return mapFunction.map(o)
            }
            throw new HibernateException("criteria doesn't support values of type: ${o?.class?.name}. Try: $expectedType or List<$expectedType> instead")
        }

        converted.toArray((Object[]) Array.newInstance(expectedType, converted.size()))
    }

    /**
     * Overloaded version of getValueAsArrayOfType that doesn't use a mapFunction
     */
    static Object[] getValueAsArrayOfType(Object targetValue, Class<?> expectedType) {
        getValueAsArrayOfType(targetValue, expectedType, null)
    }

    /**
     * Takes an Object and transforms it into a new value.
     */
    interface MapFunction {
        /**
         * Transforms an object into some new value.
         *
         * @param o the object
         * @return some new value
         */
        Object map(Object o)
    }

    static String getNativeSqlType(Class<?> clazz) {
        String typeName = CLASS_TO_TYPE_NAME.get(clazz)
        if (typeName != null) {
            return typeName
        }
        if (clazz.isEnum()) {
            return 'int'
        }
        throw new RuntimeException("Type class not valid: $clazz")
    }
}
