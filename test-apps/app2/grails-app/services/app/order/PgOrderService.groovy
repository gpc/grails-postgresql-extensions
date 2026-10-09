package app.order

import app.json.TestMapJsonb

import static gpc.pgext.hibernate.order.OrderByRandom.byRandom
import static gpc.pgext.hibernate.order.OrderBySqlFormula.sqlFormula

class PgOrderService {

    List<TestMapJsonb> orderByJson() {
        TestMapJsonb.withCriteria {
            order(sqlFormula('(data->\'name\') desc'))
        } as List<TestMapJsonb>
    }

    List<TestMapJsonb> orderByRandom() {
        TestMapJsonb.withCriteria {
            order(byRandom())
        } as List<TestMapJsonb>
    }
}
