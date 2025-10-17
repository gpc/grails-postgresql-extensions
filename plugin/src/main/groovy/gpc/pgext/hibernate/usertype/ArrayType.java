package gpc.pgext.hibernate.usertype;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import gpc.pgext.hibernate.utils.PgArrayUtils;

public class ArrayType implements UserType, ParameterizedType {

    public static final int INTEGER_ARRAY = 90001;
    public static final int LONG_ARRAY = 90002;
    public static final int STRING_ARRAY = 90003;
    public static final int ENUM_INTEGER_ARRAY = 90004;
    public static final int FLOAT_ARRAY = 90005;
    public static final int DOUBLE_ARRAY = 90006;
    public static final int UUID_ARRAY = 90007;

    private static final Map<Class<?>, Integer> CLASS_TO_SQL_CODE = new HashMap<>();

    static {
        CLASS_TO_SQL_CODE.put(Integer.class, INTEGER_ARRAY);
        CLASS_TO_SQL_CODE.put(Long.class, LONG_ARRAY);
        CLASS_TO_SQL_CODE.put(String.class, STRING_ARRAY);
        CLASS_TO_SQL_CODE.put(Float.class, FLOAT_ARRAY);
        CLASS_TO_SQL_CODE.put(Double.class, DOUBLE_ARRAY);
        CLASS_TO_SQL_CODE.put(UUID.class, UUID_ARRAY);
    }

    private Class<?> typeClass;
    private BidiEnumMap bidiMap;

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }

        if (x == null || y == null) {
            return false;
        }

        if (x instanceof Object[] && y instanceof Object[]) {
            return Arrays.deepEquals((Object[]) x, (Object[]) y);
        }

        if (x instanceof byte[] && y instanceof byte[]) {
            return Arrays.equals((byte[]) x, (byte[]) y);
        }

        if (x instanceof short[] && y instanceof short[]) {
            return Arrays.equals((short[]) x, (short[]) y);
        }

        if (x instanceof int[] && y instanceof int[]) {
            return Arrays.equals((int[]) x, (int[]) y);
        }

        if (x instanceof long[] && y instanceof long[]) {
            return Arrays.equals((long[]) x, (long[]) y);
        }

        if (x instanceof char[] && y instanceof char[]) {
            return Arrays.equals((char[]) x, (char[]) y);
        }

        if (x instanceof float[] && y instanceof float[]) {
            return Arrays.equals((float[]) x, (float[]) y);
        }

        if (x instanceof double[] && y instanceof double[]) {
            return Arrays.equals((double[]) x, (double[]) y);
        }

        if (x instanceof boolean[] && y instanceof boolean[]) {
            return Arrays.equals((boolean[]) x, (boolean[]) y);
        }

        return x.equals(y);
    }

    @Override
    public int hashCode(Object value) throws HibernateException {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        typeClass = (Class<?>) parameters.get("type");
        if (typeClass == null) {
            throw new RuntimeException("The user type needs to be configured with the type. None provided");
        }
    }

    @Override
    public Class<?> returnedClass() {
        return java.lang.reflect.Array.newInstance(typeClass, 0).getClass();
    }

    @Override
    public int[] sqlTypes() {
        Integer type = CLASS_TO_SQL_CODE.get(typeClass);
        if (type != null) {
            return new int[]{type};
        }
        if (typeClass.isEnum()) {
            return new int[]{ENUM_INTEGER_ARRAY};
        }
        throw new RuntimeException("The type " + typeClass + " is not a valid type");
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object result = null;
        Class<?> typeArrayClass = java.lang.reflect.Array.newInstance(typeClass, 0).getClass();
        Array sqlArray = rs.getArray(names[0]);
        if (!rs.wasNull()) {
            Object array = sqlArray.getArray();
            if (typeClass.isEnum()) {
                int length = array != null ? java.lang.reflect.Array.getLength(array) : 0;
                result = java.lang.reflect.Array.newInstance(typeClass, length);
                for (int i = 0; i < length; i++) {
                    java.lang.reflect.Array.set(result, i, idToEnum((Integer) java.lang.reflect.Array.get(array, i)));
                }
            } else {
                result = typeArrayClass.cast(array);
            }
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }

        Object[] valueToSet = (Object[]) value;
        Class<?> typeArrayClass = java.lang.reflect.Array.newInstance(typeClass, 0).getClass();

        if (typeClass.isEnum()) {
            typeArrayClass = Integer[].class;
            Integer[] converted = new Integer[valueToSet.length];

            for (int i = 0; i < valueToSet.length; i++) {
                if (valueToSet[i] instanceof Integer) {
                    converted[i] = (Integer) valueToSet[i];
                } else {
                    converted[i] = enumToId(valueToSet[i]);
                }
            }
            valueToSet = converted;
        }

        java.sql.Array array = st.getConnection().createArrayOf(PgArrayUtils.getNativeSqlType(typeClass), (Object[]) typeArrayClass.cast(valueToSet));
        st.setArray(index, array);
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    private void ensureBidiMapInitialized() throws HibernateException {
        try {
            if (bidiMap == null) {
                bidiMap = new BidiEnumMap((Class<? extends Enum>) typeClass);
            }
        } catch (Exception e) {
            throw new HibernateException("Unable to create bidirectional enum map for " + typeClass, e);
        }
    }

    private Object idToEnum(int id) throws HibernateException {
        ensureBidiMapInitialized();
        return bidiMap.getEnumValue(id);
    }

    private int enumToId(Object enumValue) throws HibernateException {
        ensureBidiMapInitialized();
        return bidiMap.getKey(enumValue);
    }
}
