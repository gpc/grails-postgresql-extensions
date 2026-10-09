package gpc.pgext.hibernate.order

import groovy.transform.CompileStatic

import org.hibernate.Criteria
import org.hibernate.HibernateException
import org.hibernate.criterion.CriteriaQuery
import org.hibernate.criterion.Order

/**
 * Extends {@link org.hibernate.criterion.Order} to allow ordering by an SQL formula passed by the user.
 * It simply appends the <code>sqlFormula</code> passed by the user to the resulting SQL query,
 * without any verification.
 *
 * From: http://blog.tremend.ro/2008/06/10/how-to-order-by-a-custom-sql-formulaexpression-when-using-hibernate-criteria-api/
 *
 */
@CompileStatic
class OrderBySqlFormula extends Order {

    private String sqlFormula

    /**
     * Constructor for Order.
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     */
    protected OrderBySqlFormula(String sqlFormula) {
        super(sqlFormula, true)
        this.sqlFormula = sqlFormula
    }

    @Override
    String toString() {
        sqlFormula
    }

    @Override
    String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        sqlFormula
    }

    /**
     * Custom order
     *
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     * @return Order
     */
    static Order sqlFormula(String sqlFormula) {
        new OrderBySqlFormula(sqlFormula)
    }
}