package gpc.pgext.hibernate.usertype

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class BidiEnumMap implements Serializable {

    static final String ENUM_ID_ACCESSOR = 'getId'

    private static final long serialVersionUID = 3325751131102095834L

    private final Map enumToKey
    private final Map keytoEnum

    BidiEnumMap(Class<? extends Enum> enumClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        log.debug('Building Bidirectional Enum Map...')

        def enumToKey = new EnumMap(enumClass)
        def keytoEnum = new HashMap()

        def idAccessor = getIdAccessor(enumClass)
        def values = getEnumValues(enumClass)

        for (Enum value : values) {
            def id = idAccessor.invoke(value)
            enumToKey.put(value, id)
            if (keytoEnum.containsKey(id)) {
                log.warn('Duplicate Enum ID [{}] detected for Enum [{}]!', id, enumClass.name)
            }
            keytoEnum.put(id, value)
        }

        this.enumToKey = Collections.unmodifiableMap(enumToKey)
        this.keytoEnum = Collections.unmodifiableMap(keytoEnum)
    }

    private static <E extends Enum> E[] getEnumValues(Class<E> enumClass) {
        enumClass.enumConstants
    }

    private static <E extends Enum> Method getIdAccessor(Class<E> enumClass) {
        def idMethod = enumClass.methods.find { it.name == ENUM_ID_ACCESSOR }
        if (!idMethod) {
            idMethod = enumClass.getMethod('ordinal')
        }
        return idMethod
    }

    Object getEnumValue(int id) {
        keytoEnum.get(id)
    }

    int getKey(Object enumValue) {
        enumToKey.get(enumValue) as Integer
    }
}
