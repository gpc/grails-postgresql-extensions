package gpc.pgext.hibernate.criterion;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

/**
 * Registers the {@link PgSqlFunction}s used by the criteria and orders.
 */
public class PgSqlFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        var typeConfiguration = functionContributions.getTypeConfiguration();
        var registry = functionContributions.getFunctionRegistry();
        registry.register(PgSqlFunction.PREDICATE, PgSqlFunction.predicate(typeConfiguration));
        registry.register(PgSqlFunction.EXPRESSION, PgSqlFunction.expression(typeConfiguration));
    }
}
