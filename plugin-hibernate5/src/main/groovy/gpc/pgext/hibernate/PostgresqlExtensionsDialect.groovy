package gpc.pgext.hibernate

import java.sql.Types

import groovy.transform.CompileStatic

import org.hibernate.dialect.PostgreSQL95Dialect

import gpc.pgext.hibernate.usertype.ArrayType
import gpc.pgext.hibernate.usertype.HstoreMapType
import gpc.pgext.hibernate.usertype.JsonMapType
import gpc.pgext.hibernate.usertype.JsonbMapType

@CompileStatic
class PostgresqlExtensionsDialect extends PostgreSQL95Dialect {

    private static final String SEQUENCE_PER_TABLE = 'dataSource.postgresql.extensions.sequence_per_table'

    /**
     * Register postgresql types
     */
    PostgresqlExtensionsDialect() {
        super()
        registerColumnType(Types.ARRAY, 'array')
        registerColumnType(ArrayType.LONG_ARRAY, '_int8')
        registerColumnType(ArrayType.INTEGER_ARRAY, '_int4')
        registerColumnType(ArrayType.ENUM_INTEGER_ARRAY, '_int4')
        registerColumnType(ArrayType.STRING_ARRAY, '_varchar')
        registerColumnType(ArrayType.DOUBLE_ARRAY, '_float8')
        registerColumnType(ArrayType.FLOAT_ARRAY, '_float4')
        registerColumnType(ArrayType.UUID_ARRAY, '_uuid')
        registerColumnType(HstoreMapType.SQLTYPE, 'hstore')
        registerColumnType(JsonMapType.SQLTYPE, 'json')
        registerColumnType(JsonbMapType.SQLTYPE, 'jsonb')
    }
}
