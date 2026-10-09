package gpc.pgext.hibernate

import groovy.transform.CompileStatic

import org.hibernate.boot.model.TypeContributions
import org.hibernate.dialect.PostgreSQLDialect
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.descriptor.java.JavaType
import org.hibernate.type.descriptor.jdbc.JdbcType
import org.hibernate.type.descriptor.sql.DdlType

import gpc.pgext.hibernate.usertype.ArrayType
import gpc.pgext.hibernate.usertype.HstoreMapType
import gpc.pgext.hibernate.usertype.JsonMapType
import gpc.pgext.hibernate.usertype.JsonbMapType

@CompileStatic
class PostgresqlExtensionsDialect extends PostgreSQLDialect {

    private static final Map<Integer, String> COLUMN_TYPES = [
            (ArrayType.LONG_ARRAY)        : '_int8',
            (ArrayType.INTEGER_ARRAY)     : '_int4',
            (ArrayType.ENUM_INTEGER_ARRAY): '_int4',
            (ArrayType.STRING_ARRAY)      : '_varchar',
            (ArrayType.DOUBLE_ARRAY)      : '_float8',
            (ArrayType.FLOAT_ARRAY)       : '_float4',
            (ArrayType.UUID_ARRAY)        : '_uuid',
            (HstoreMapType.SQLTYPE)       : 'hstore',
            (JsonMapType.SQLTYPE)         : 'json',
            (JsonbMapType.SQLTYPE)        : 'jsonb',
    ] as Map<Integer, String>

    /**
     * Register the column types of the postgresql user types
     */
    @Override
    void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry)
        def ddlTypeRegistry = typeContributions.typeConfiguration.ddlTypeRegistry
        COLUMN_TYPES.each { Integer sqlType, String typeName ->
            ddlTypeRegistry.addDescriptor(new FixedDdlType(sqlType, typeName))
        }
    }

    /**
     * The column type of a user type, which is always the same type name
     */
    private static class FixedDdlType implements DdlType {

        private static final long serialVersionUID = 1L

        private final int sqlTypeCode
        private final String typeName

        FixedDdlType(int sqlTypeCode, String typeName) {
            this.sqlTypeCode = sqlTypeCode
            this.typeName = typeName
        }

        @Override
        int getSqlTypeCode() {
            sqlTypeCode
        }

        @Override
        String getRawTypeName() {
            typeName
        }

        @Override
        String getTypeName(Long size, Integer precision, Integer scale) {
            typeName
        }

        @Override
        String getCastTypeName(JdbcType jdbcType, JavaType<?> javaType) {
            typeName
        }

        @Override
        String getCastTypeName(JdbcType jdbcType, JavaType<?> javaType, Long length, Integer precision, Integer scale) {
            typeName
        }
    }
}
