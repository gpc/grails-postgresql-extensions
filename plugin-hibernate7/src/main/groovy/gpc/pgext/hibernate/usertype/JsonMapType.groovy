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
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.usertype.UserType
import org.postgresql.util.PGobject

@CompileStatic
class JsonMapType implements UserType<Map> {

    static int SQLTYPE = 90021

    private final Type userType = Map
    private final Gson gson = new GsonBuilder().serializeNulls().create()

    @Override
    int getSqlType() {
        SQLTYPE
    }

    @Override
    Class<Map> returnedClass() {
        Map
    }

    @Override
    boolean equals(Map x, Map y) throws HibernateException {
        Objects.equals(x, y)
    }

    @Override
    int hashCode(Map x) throws HibernateException {
        x ? x.hashCode() : 0
    }

    @Override
    Map nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        PGobject o = rs.getObject(position) as PGobject
        String jsonString = o?.value
        gson.fromJson(jsonString, userType) as Map
    }

    @Override
    void nullSafeSet(PreparedStatement st, Map value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER)
        } else {
            st.setObject(index, gson.toJson(value, userType), Types.OTHER)
        }
    }

    @Override
    Map deepCopy(Map value) throws HibernateException {
        value == null ? null : new HashMap(value)
    }

    @Override
    boolean isMutable() {
        true
    }

    @Override
    Serializable disassemble(Map value) throws HibernateException {
        gson.toJson(value, userType)
    }

    @Override
    Map assemble(Serializable cached, Object owner) throws HibernateException {
        gson.fromJson((String) cached, userType) as Map
    }

    @Override
    Map replace(Map original, Map target, Object owner) throws HibernateException {
        original
    }
}
