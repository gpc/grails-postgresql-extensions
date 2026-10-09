package gpc.pgext.hibernate.criterion;

import java.util.List;

import org.hibernate.metamodel.model.domain.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Literal;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Renders a PostgreSQL SQL fragment given as the first (literal) argument. Every {@code ?} in the fragment
 * is replaced by the rendering of the next argument, which is either a property path (rendered as its
 * column) or a bound parameter. A {@code ?} inside quotes is not a placeholder.
 */
public class PgSqlFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

    /** Renders a fragment that is a predicate, used by the criteria */
    public static final String PREDICATE = "pgext_sql_predicate";

    /** Renders a fragment that is an expression, used by the orders */
    public static final String EXPRESSION = "pgext_sql_expression";

    private final boolean predicate;

    public PgSqlFunction(String name, BasicTypeReference<?> returnType, boolean predicate, TypeConfiguration typeConfiguration) {
        super(
                name,
                StandardArgumentsValidators.min(1),
                StandardFunctionReturnTypeResolvers.invariant(typeConfiguration.getBasicTypeRegistry().resolve(returnType)),
                null
        );
        this.predicate = predicate;
    }

    static PgSqlFunction predicate(TypeConfiguration typeConfiguration) {
        return new PgSqlFunction(PREDICATE, StandardBasicTypes.BOOLEAN, true, typeConfiguration);
    }

    static PgSqlFunction expression(TypeConfiguration typeConfiguration) {
        return new PgSqlFunction(EXPRESSION, StandardBasicTypes.OBJECT_TYPE, false, typeConfiguration);
    }

    @Override
    public boolean isPredicate() {
        return predicate;
    }

    @Override
    public void render(
            SqlAppender sqlAppender,
            List<? extends SqlAstNode> arguments,
            ReturnableType<?> returnType,
            SqlAstTranslator<?> walker) {
        if (!(arguments.get(0) instanceof Literal literal) || !(literal.getLiteralValue() instanceof String sql)) {
            throw new IllegalArgumentException("The first argument of " + getName() + " must be the SQL as a literal");
        }
        // A predicate is wrapped so it combines safely with other predicates. An order formula is not,
        // as it may end with its own direction, like "(data->'name') desc"
        if (predicate) {
            sqlAppender.append('(');
        }
        int argument = 1;
        boolean quoted = false;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '\'') {
                quoted = !quoted;
            }
            if (c == '?' && !quoted) {
                if (argument >= arguments.size()) {
                    throw new IllegalArgumentException("Missing argument for placeholder " + argument + " in: " + sql);
                }
                arguments.get(argument++).accept(walker);
            } else {
                sqlAppender.append(c);
            }
        }
        if (argument != arguments.size()) {
            throw new IllegalArgumentException("Expected " + (argument - 1) + " arguments, got " + (arguments.size() - 1) + " in: " + sql);
        }
        if (predicate) {
            sqlAppender.append(')');
        }
    }
}
