package gpc.pgext.hibernate.order

import groovy.transform.CompileStatic

@CompileStatic
class OrderByRandom extends OrderBySqlFormula {

    private static final String ORDER_RANDOM = 'random()'

    protected OrderByRandom() {
        super(ORDER_RANDOM)
    }

    static OrderBySqlFormula byRandom() {
        new OrderByRandom()
    }
}
