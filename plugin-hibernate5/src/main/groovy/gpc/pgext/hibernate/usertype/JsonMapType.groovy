package gpc.pgext.hibernate.usertype

import java.lang.reflect.Type
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

import groovy.transform.CompileStatic

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType
import org.postgresql.util.PGobject

@CompileStatic
class JsonMapType implements UserType {

    static int SQLTYPE = 90021

    private final Type userType = Map
    private final Gson gson = new GsonBuilder().serializeNulls().create()

    @Override
    int[] sqlTypes() {
        SQLTYPE as int[]
    }

    @Override
    Class<?> returnedClass() {
        userType.getClass()
    }

    @Override
    boolean equals(Object x, Object y) throws HibernateException {
        Objects.equals(x, y)
    }

    @Override
    int hashCode(Object x) throws HibernateException {
        x ? x.hashCode() : 0
    }

    @Override
    Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        PGobject o = rs.getObject(names[0]) as PGobject
        String jsonString = o?.value
        gson.fromJson(jsonString, userType)
    }

    @Override
    void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER)
        } else {
            st.setObject(index, gson.toJson(value, userType), Types.OTHER)
        }
    }

    @Override
    Object deepCopy(Object value) throws HibernateException {
        if (!value && value != null && value instanceof Map) {
            return new HashMap()
        }
        if (!value) {
            return null
        }
        new HashMap(value as Map)
    }

    @Override
    boolean isMutable() {
        true
    }

    @Override
    Serializable disassemble(Object value) throws HibernateException {
        gson.toJson(value, userType)
    }

    @Override
    Object assemble(Serializable cached, Object owner) throws HibernateException {
        gson.fromJson((String) cached, userType)
    }

    @Override
    Object replace(Object original, Object target, Object owner) throws HibernateException {
        original
    }
}
