package gpc.pgext.hibernate.usertype

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

import groovy.transform.CompileStatic

import org.hibernate.HibernateException
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.usertype.UserType

@CompileStatic
class HstoreMapType implements UserType<Map> {

    static int SQLTYPE = 90011

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
        if (x == null) {
            return y == null
        }
        x == y
    }

    @Override
    int hashCode(Map x) throws HibernateException {
        x ? x.hashCode() : 0
    }

    @Override
    Map nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        HstoreHelper.toMap(rs.getString(position))
    }

    @Override
    void nullSafeSet(PreparedStatement ps, Map value, int index, WrapperOptions options) throws SQLException {
        ps.setObject(
                index,
                HstoreHelper.toString(value),
                Types.OTHER
        )
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
        value as Serializable
    }

    @Override
    Map assemble(Serializable cached, Object owner) throws HibernateException {
        cached as Map
    }

    @Override
    Map replace(Map original, Map target, Object owner) throws HibernateException {
        original
    }
}
