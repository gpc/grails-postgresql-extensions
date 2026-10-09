package gpc.pgext.hibernate.order

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Order
import jakarta.persistence.criteria.Path
import org.hibernate.query.criteria.HibernateCriteriaBuilder as JpaCriteriaBuilder

import gpc.pgext.hibernate.criterion.PgCriterion
import gpc.pgext.hibernate.criterion.PgSqlFunction

/**
 * Allows ordering by an SQL formula passed by the user, with {@code order(sqlFormula('...'))} in a criteria.
 * It simply appends the <code>sqlFormula</code> passed by the user to the resulting SQL query,
 * without any verification.
 *
 * GORM for Hibernate 7 only orders by properties, so the order is added to the query as a {@link PgCriterion}
 * that inserts the formula among the orders of the query when the criteria is turned into SQL. This relies on GORM
 * adding the property orders to the query before its predicates, which the order tests of the test apps check.
 * See https://github.com/apache/grails-core/issues/16563
 */
@CompileStatic
class OrderBySqlFormula {

    final String sqlFormula

    /**
     * Constructor for Order.
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     */
    protected OrderBySqlFormula(String sqlFormula) {
        this.sqlFormula = sqlFormula
    }

    @Override
    String toString() {
        sqlFormula
    }

    /**
     * @param position The position of this order among the orders of the criteria
     * @return The criterion that adds this order to the query
     */
    PgCriterion toCriterion(int position) {
        String formula = sqlFormula
        PgCriterion.order({ AbstractQuery<?> query, From<?, ?> root, JpaCriteriaBuilder cb, Path<?> property ->
            // Only queries for the entity are ordered, not count queries or other projections
            if (query instanceof CriteriaQuery && ((CriteriaQuery<?>) query).resultType == root.javaType) {
                def criteriaQuery = (CriteriaQuery<?>) query
                List<Order> orders = new ArrayList<>(criteriaQuery.orderList)
                orders.add(Math.min(position, orders.size()), cb.asc(cb.function(PgSqlFunction.EXPRESSION, Object, cb.literal(formula))))
                criteriaQuery.orderBy(orders)
            }
            // No predicate, so the order does not change the result of an or {} or not {} it is called in
            null
        } as PgCriterion.PredicateFactory)
    }

    /**
     * Custom order
     *
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     * @return Order
     */
    static OrderBySqlFormula sqlFormula(String sqlFormula) {
        new OrderBySqlFormula(sqlFormula)
    }
}
