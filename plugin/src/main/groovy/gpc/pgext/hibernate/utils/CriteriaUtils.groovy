package gpc.pgext.hibernate.utils

import java.lang.reflect.Method

import groovy.transform.CompileStatic

import org.hibernate.criterion.Criterion

import grails.orm.HibernateCriteriaBuilder
import org.grails.orm.hibernate.query.AbstractHibernateCriteriaBuilder

@CompileStatic
class CriteriaUtils {

    static void validateExpression(HibernateCriteriaBuilder self, String methodName, String propertyName, Object propertyValue) {
        if (!validateSimpleExpression(self)) {
            throwRuntimeException(
                    self,
                    "Call to [$methodName] with propertyName [$propertyName] and value [$propertyValue] is not allowed here."
            )
        }
    }

    static boolean validateSimpleExpression(HibernateCriteriaBuilder target) {
        makeMethodAccessible(AbstractHibernateCriteriaBuilder, 'validateSimpleExpression').invoke(target, new Class<?>[]{}) as boolean
    }

    static void throwRuntimeException(HibernateCriteriaBuilder target, String message) {
        makeMethodAccessible(AbstractHibernateCriteriaBuilder, 'throwRuntimeException', RuntimeException).invoke(
                target,
                new IllegalArgumentException(message)
        )
    }

    static Method makeMethodAccessible(Class clazz, String methodName, Class<?>... parameterTypes) {
        clazz.getDeclaredMethod(methodName, parameterTypes).tap {
            accessible = true
        }
    }

    static Criterion addToCriteria(HibernateCriteriaBuilder self, String methodName, Class criterionClass, String propertyName, Object propertyValue, String operator) {
        validateExpression(self, methodName, propertyName, propertyValue)
        addToCriteria(
                self,
                (criterionClass as Class<Criterion>).getDeclaredConstructor(new Class[] {String, Object, String}).newInstance(propertyName, propertyValue, operator)
        )
    }

    static Criterion addToCriteria(HibernateCriteriaBuilder self, String methodName, Class criterionClass, String propertyName, Object propertyValue) {
        validateExpression(self, methodName, propertyName, propertyValue)
        addToCriteria(
                self,
                (criterionClass as Class<Criterion>).getDeclaredConstructor(new Class[] {String, Object}).newInstance(propertyName, propertyValue)
        )
    }

    static Criterion addToCriteria(HibernateCriteriaBuilder target, Criterion criterion) {
        makeMethodAccessible(AbstractHibernateCriteriaBuilder, 'addToCriteria', Criterion).invoke(
                target,
                criterion
        ) as Criterion
    }

    static String calculatePropertyName(HibernateCriteriaBuilder target, String propertyName) {
        makeMethodAccessible(AbstractHibernateCriteriaBuilder, 'calculatePropertyName', String).invoke(
                target,
                propertyName
        ) as String
    }

    static Object calculatePropertyValue(HibernateCriteriaBuilder target, Object propertyValue) {
        makeMethodAccessible(AbstractHibernateCriteriaBuilder, 'calculatePropertyValue', Object).invoke(
                target,
                propertyValue
        )
    }
}
