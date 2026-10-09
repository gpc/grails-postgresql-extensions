package gpc.pgext.hibernate.utils

import groovy.transform.CompileStatic

import jakarta.persistence.criteria.JoinType

import grails.orm.HibernateCriteriaBuilder
import org.grails.datastore.gorm.query.criteria.DetachedAssociationCriteria
import org.grails.datastore.mapping.query.Query
import org.grails.datastore.mapping.query.api.Criteria

import gpc.pgext.hibernate.criterion.PgCriterion

@CompileStatic
class CriteriaUtils {

    /**
     * Adds the criterion to the query of the builder
     */
    static Criteria addToCriteria(HibernateCriteriaBuilder self, PgCriterion criterion) {
        self.hibernateQuery.add(criterion)
        self
    }

    /**
     * Adds the criterion on the property to the query of the builder.
     *
     * GORM resolves an alias created with {@code createAlias} to a join of its own, which a criterion handler
     * cannot look up. When the property is on an alias with an inner join, an always true restriction on the
     * alias is added before the criterion, so GORM creates that join before the criterion uses it, and the
     * criteria of GORM and of this plugin on the alias apply to the same join.
     *
     * GORM 8.0.0 always creates the join of an alias as an inner join, so for an alias with an outer join the
     * criterion uses the join of the association with the join type of the alias instead.
     *
     * See https://github.com/apache/grails-core/issues/16562 and https://github.com/apache/grails-core/issues/16563
     */
    static Criteria addToCriteria(HibernateCriteriaBuilder self, String propertyName, PgCriterion criterion) {
        def association = findAssociation(self, propertyName)
        if (!association) {
            return addToCriteria(self, criterion)
        }
        criterion.joinType = self.hibernateQuery.detachedCriteria.joinTypes[association.associationPath] ?: JoinType.INNER
        if (criterion.joinType != JoinType.INNER) {
            return addToCriteria(self, criterion)
        }
        String identity = "${association.alias}.${association.association.associatedEntity.identity.name}"
        def resolveAlias = new Query.Disjunction([new Query.IsNull(identity), new Query.IsNotNull(identity)] as List<Query.Criterion>)
        self.hibernateQuery.add(new Query.Conjunction([resolveAlias, criterion] as List<Query.Criterion>))
        self
    }

    /**
     * Replaces an alias created with {@code createAlias} at the start of the property name by its association path,
     * so {@code l.favoriteNumbers} becomes {@code like.favoriteNumbers}
     */
    static String calculatePropertyName(HibernateCriteriaBuilder self, String propertyName) {
        int dot = propertyName.indexOf('.')
        if (dot < 0) {
            return propertyName
        }
        String alias = propertyName.substring(0, dot)
        String associationPath = self.hibernateQuery.aliases.find { it.alias() == alias }?.path() ?:
                findAssociation(self, propertyName)?.associationPath
        associationPath ? associationPath + propertyName.substring(dot) : propertyName
    }

    private static DetachedAssociationCriteria findAssociation(HibernateCriteriaBuilder self, String propertyName) {
        int dot = propertyName.indexOf('.')
        dot < 0 ? null : findAssociation(self.hibernateQuery.detachedCriteria.criteria, propertyName.substring(0, dot))
    }

    private static DetachedAssociationCriteria findAssociation(List<Query.Criterion> criteria, String alias) {
        for (Query.Criterion criterion : criteria) {
            if (criterion instanceof DetachedAssociationCriteria) {
                def associationCriteria = (DetachedAssociationCriteria) criterion
                if (associationCriteria.alias == alias) {
                    return associationCriteria
                }
            } else if (criterion instanceof Query.Junction) {
                def associationCriteria = findAssociation(((Query.Junction) criterion).criteria, alias)
                if (associationCriteria) {
                    return associationCriteria
                }
            }
        }
        null
    }
}
