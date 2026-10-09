package gpc.pgext.hibernate.order

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Order

@CompileStatic
class OrderByRandom extends Order {

    private static final String ORDER_RANDOM = 'random()'

    protected OrderByRandom() {
        super(ORDER_RANDOM, true)
    }

    String toString() {
        ORDER_RANDOM
    }

    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        ORDER_RANDOM
    }

    static Order byRandom() {
        new OrderByRandom()
    }
}