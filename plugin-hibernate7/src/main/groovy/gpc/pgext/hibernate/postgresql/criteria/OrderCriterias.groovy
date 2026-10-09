package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import grails.orm.HibernateCriteriaBuilder
import org.grails.datastore.mapping.query.Query
import org.grails.datastore.mapping.query.api.Criteria

import gpc.pgext.hibernate.criterion.PgCriterion
import gpc.pgext.hibernate.order.OrderBySqlFormula

import static gpc.pgext.hibernate.utils.CriteriaUtils.addToCriteria

@CompileStatic
class OrderCriterias {

    /**
     * Orders by an SQL formula, created with {@code sqlFormula('...')} or {@code byRandom()}
     * @param order The order
     * @return The criteria
     */
    static Criteria order(HibernateCriteriaBuilder self, OrderBySqlFormula order) {
        def detachedCriteria = self.hibernateQuery.detachedCriteria
        int position = detachedCriteria.orders.size() + countOrders(detachedCriteria.criteria)
        addToCriteria(self, order.toCriterion(position))
    }

    private static int countOrders(List<Query.Criterion> criteria) {
        (int) criteria.sum(0) { Query.Criterion criterion ->
            criterion instanceof PgCriterion && ((PgCriterion) criterion).order ? 1 :
                criterion instanceof Query.Junction ? countOrders(((Query.Junction) criterion).criteria) : 0
        }
    }
}
