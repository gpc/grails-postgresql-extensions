package gpc.pgext.hibernate.criterion

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Predicate
import org.hibernate.query.criteria.HibernateCriteriaBuilder

import org.grails.datastore.mapping.query.Query
import org.grails.orm.hibernate.query.PredicateGenerator

/**
 * Lets GORM turn a {@link PgCriterion} into a JPA predicate. Registered through
 * {@code META-INF/services}.
 */
@CompileStatic
class PgCriterionHandlerProvider implements PredicateGenerator.CriterionHandlerProvider {

    @Override
    Class<? extends Query.Criterion> criterionType() {
        PgCriterion
    }

    @Override
    PredicateGenerator.CriterionHandler criterionHandler() {
        new PredicateGenerator.CriterionHandler() {
            @Override
            Predicate handle(AbstractQuery<?> query, From<?, ?> root, HibernateCriteriaBuilder cb, Query.Criterion criterion) {
                ((PgCriterion) criterion).toPredicate(query, root, cb)
            }
        }
    }
}
