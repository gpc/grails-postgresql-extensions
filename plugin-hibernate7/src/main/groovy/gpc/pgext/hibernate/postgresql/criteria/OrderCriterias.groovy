package gpc.pgext.hibernate.postgresql.criteria

import groovy.transform.CompileStatic

import grails.orm.HibernateCriteriaBuilder
import org.grails.datastore.mapping.query.api.Criteria

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
        addToCriteria(self, order.toCriterion())
    }
}
