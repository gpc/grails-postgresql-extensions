package test.array

import app.criteria.array.Like
import app.criteria.array.Team
import app.criteria.array.User
import spock.lang.Specification
import spock.lang.Unroll

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

@Rollback
@Integration
class PgArrayCriteriaWithAliasIntegrationSpec extends Specification {

    private static final int LEFT_JOIN = 1

    def setup() {
        Team.executeUpdate('delete from Team')
        User.executeUpdate('delete from User')
        Like.executeUpdate('delete from Like')
    }

    @Unroll
    void 'a pg criterion and a GORM criterion on the same alias of a collection apply to the same element (pg criterion first: #pgFirst)'() {
        setup:
            def three = new Like(favoriteNumbers: [3]).save(flush: true, failOnError: true)
            def five = new Like(favoriteNumbers: [5]).save(flush: true, failOnError: true)
            new Team(name: 'team').addToLikes(three).addToLikes(five).save(flush: true, failOnError: true)

        when: 'no single like both contains 3 and is the like with 5'
            def result = Team.withCriteria {
                createAlias('likes', 'l')
                if (pgFirst) {
                    pgArrayContains('l.favoriteNumbers', 3)
                    eq('l.id', five.id)
                } else {
                    eq('l.id', five.id)
                    pgArrayContains('l.favoriteNumbers', 3)
                }
            }

        then:
            result.empty

        when: 'the like with 3 both contains 3 and is the like with 3'
            result = Team.withCriteria {
                createAlias('likes', 'l')
                if (pgFirst) {
                    pgArrayContains('l.favoriteNumbers', 3)
                    eq('l.id', three.id)
                } else {
                    eq('l.id', three.id)
                    pgArrayContains('l.favoriteNumbers', 3)
                }
            }

        then: 'the team is found once'
            result*.name == ['team']

        where:
            pgFirst << [true, false]
    }

    void 'a pg criterion on an alias honours the join type of the alias'() {
        setup:
            new User(name: 'without like').save(flush: true, failOnError: true)
            new User(name: 'with 3', like: new Like(favoriteNumbers: [3])).save(flush: true, failOnError: true)
            new User(name: 'with 5', like: new Like(favoriteNumbers: [5])).save(flush: true, failOnError: true)

        when:
            def result = User.withCriteria {
                createAlias('like', 'l', LEFT_JOIN)
                or {
                    isNull('like')
                    pgArrayContains('l.favoriteNumbers', 3)
                }
            }

        then:
            result*.name.sort() == ['with 3', 'without like']
    }
}
