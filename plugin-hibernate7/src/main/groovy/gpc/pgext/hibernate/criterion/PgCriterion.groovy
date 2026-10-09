package gpc.pgext.hibernate.criterion

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.hibernate.query.criteria.HibernateCriteriaBuilder

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
         * @return The predicate
         */
        Predicate create(AbstractQuery<?> query, From<?, ?> root, HibernateCriteriaBuilder cb, Path<?> property)
    }

    /** The property path, with any alias replaced by its association path */
    final String propertyPath

    private final PredicateFactory factory

    PgCriterion(String propertyPath, PredicateFactory factory) {
        this.propertyPath = propertyPath
        this.factory = factory
    }

    Predicate toPredicate(AbstractQuery<?> query, From<?, ?> root, HibernateCriteriaBuilder cb) {
        factory.create(query, root, cb, propertyPath ? resolvePath(root, propertyPath) : null)
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
     * Resolves a property path like {@code like.favoriteNumbers}, reusing the joins already on the query
     */
    private static Path<?> resolvePath(From<?, ?> root, String propertyPath) {
        String[] segments = propertyPath.split(/\./)
        From<?, ?> from = root
        for (int i = 0; i < segments.length - 1; i++) {
            String segment = segments[i]
            Join<?, ?> join = from.joins.find { Join<?, ?> j -> j.attribute.name == segment }
            from = join ?: from.join(segment)
        }
        from.get(segments[segments.length - 1])
    }

    @Override
    String toString() {
        "PgCriterion($propertyPath)"
    }
}
