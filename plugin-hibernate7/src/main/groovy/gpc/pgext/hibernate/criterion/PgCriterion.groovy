package gpc.pgext.hibernate.criterion

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.hibernate.query.criteria.HibernateCriteriaBuilder
import org.hibernate.query.sqm.tree.from.SqmFrom

import org.grails.datastore.mapping.query.Query

/**
 * A GORM criterion that is turned into a JPA predicate by {@link PgCriterionHandlerProvider}.
 *
 * Hibernate 7 has no Criteria API to extend, so the criteria of this plugin are added to the GORM query
 * and rendered as PostgreSQL SQL fragments through {@link PgSqlFunction}.
 */
@CompileStatic
class PgCriterion implements Query.Criterion {

    /**
     * Creates the predicate of a criterion
     */
    interface PredicateFactory {

        /**
         * @param query The query the predicate is created for
         * @param root The root (or association join) of the query
         * @param cb The criteria builder
         * @param property The path of the property of the criterion, or null if it has none
         * @return The predicate, or null if the criterion does not restrict the query
         */
        Predicate create(AbstractQuery<?> query, From<?, ?> root, HibernateCriteriaBuilder cb, Path<?> property)
    }

    /** The property path, with any alias replaced by its association path */
    final String propertyPath

    /** Whether the criterion is an order by an SQL formula, see {@code OrderBySqlFormula} */
    final boolean order

    /** The join type of the alias the property is on, or null if it is not on an alias */
    JoinType joinType

    private final PredicateFactory factory

    PgCriterion(String propertyPath, PredicateFactory factory) {
        this(propertyPath, false, factory)
    }

    private PgCriterion(String propertyPath, boolean order, PredicateFactory factory) {
        this.propertyPath = propertyPath
        this.order = order
        this.factory = factory
    }

    /**
     * Creates a criterion that orders the query, and returns no predicate
     */
    static PgCriterion order(PredicateFactory factory) {
        new PgCriterion(null, true, factory)
    }

    Predicate toPredicate(AbstractQuery<?> query, From<?, ?> root, HibernateCriteriaBuilder cb) {
        factory.create(query, root, cb, propertyPath ? resolvePath(root, propertyPath, joinType) : null)
    }

    /**
     * Creates a predicate from a PostgreSQL SQL fragment, where each {@code ?} is replaced by the next argument
     */
    static Predicate sql(HibernateCriteriaBuilder cb, String sql, Expression<?>... arguments) {
        List<Expression<?>> functionArguments = [cb.literal(sql)] as List<Expression<?>>
        functionArguments.addAll(arguments)
        cb.isTrue(cb.function(PgSqlFunction.PREDICATE, Boolean, functionArguments as Expression<?>[]))
    }

    /**
     * Binds the value as text, or as SQL NULL when it is null, like Hibernate 5 did
     */
    static Expression<String> text(HibernateCriteriaBuilder cb, Object value) {
        value == null ? cb.nullLiteral(String) : cb.value(value.toString())
    }

    /**
     * Resolves a property path like {@code like.favoriteNumbers}, reusing the joins already on the query.
     * GORM may join an association more than once (for {@code createAlias} and for the alias itself), so the
     * latest join with the join type of the alias is used (see {@code CriteriaUtils.addToCriteria})
     */
    private static Path<?> resolvePath(From<?, ?> root, String propertyPath, JoinType joinType) {
        String[] segments = propertyPath.split(/\./)
        From<?, ?> from = root
        for (int i = 0; i < segments.length - 1; i++) {
            String segment = segments[i]
            Join<?, ?> join = joinsInCreationOrder(from).reverse().find { Join<?, ?> j ->
                j.attribute?.name == segment && (joinType == null || j.joinType == joinType)
            }
            from = join ?: from.join(segment, joinType ?: JoinType.INNER)
        }
        from.get(segments[segments.length - 1])
    }

    private static List<Join<?, ?>> joinsInCreationOrder(From<?, ?> from) {
        // The joins of a JPA From are a Set, the joins of a Hibernate SqmFrom are kept in creation order
        Collection<?> joins = from instanceof SqmFrom ? ((SqmFrom<?, ?>) from).sqmJoins : from.joins
        joins.findAll { it instanceof Join } as List<Join<?, ?>>
    }

    @Override
    String toString() {
        "PgCriterion($propertyPath)"
    }
}
