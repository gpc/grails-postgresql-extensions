package gpc.pgext.hibernate.usertype

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

import groovy.transform.CompileStatic

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType

@CompileStatic
class HstoreMapType implements UserType {

    static int SQLTYPE = 90011

    int[] sqlTypes() {
        SQLTYPE as int[]
    }

    Class<?> returnedClass() {
        Map
    }

    boolean equals(Object x, Object y) throws HibernateException {
        if (x == null) {
            return y == null
        }
        (x as Map) == (y as Map)
    }

    int hashCode(Object x) throws HibernateException {
        x ? x.hashCode() : 0
    }

    Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        def col = names[0]
        def val = rs.getString(col)
        HstoreHelper.toMap(val)
    }

    void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        ps.setObject(
                index,
                HstoreHelper.toString(value as Map),
                Types.OTHER
        )
    }

    Object deepCopy(Object value) throws HibernateException {
        value == null ? null : new HashMap(value as Map)
    }

    boolean isMutable() {
        true
    }

    Serializable disassemble(Object value) throws HibernateException {
        value as Serializable
    }

    Object assemble(Serializable cached, Object owner) throws HibernateException {
        cached
    }

    Object replace(Object original, Object target, Object owner) throws HibernateException {
        original
    }
}
