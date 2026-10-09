package gpc.pgext.hibernate.utils

import groovy.transform.CompileStatic

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
                findAssociationPath(self.hibernateQuery.detachedCriteria.criteria, alias)
        associationPath ? associationPath + propertyName.substring(dot) : propertyName
    }

    private static String findAssociationPath(List<Query.Criterion> criteria, String alias) {
        for (Query.Criterion criterion : criteria) {
            if (criterion instanceof DetachedAssociationCriteria) {
                def associationCriteria = (DetachedAssociationCriteria) criterion
                if (associationCriteria.alias == alias) {
                    return associationCriteria.associationPath
                }
            } else if (criterion instanceof Query.Junction) {
                def associationPath = findAssociationPath(((Query.Junction) criterion).criteria, alias)
                if (associationPath) {
                    return associationPath
                }
            }
        }
        null
    }
}
