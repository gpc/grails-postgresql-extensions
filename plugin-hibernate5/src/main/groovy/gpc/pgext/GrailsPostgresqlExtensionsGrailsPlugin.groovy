package gpc.pgext

import groovy.transform.CompileStatic

import grails.plugins.Plugin

@CompileStatic
class GrailsPostgresqlExtensionsGrailsPlugin extends Plugin {

    def grailsVersion = '7.0.0-SNAPSHOT > *'
    def title = 'Grails PostgreSQL Extensions Plugin'
    def author = 'Iván López'
    def description = 'Provides Hibernate user types supporting PostgreSQL Native Types ' +
                      'like Array, HStore, JSON, JSONB,... as well as new criteria to query ' +
                      'these native types.'
    def documentation = 'https://github.com/gpc/grails-postgresql-extensions'
    def license = 'APACHE'
    def organization = [
            name: 'GPC',
            url: 'https://github.com/gpc'
    ]
    def developers = [
            [name: 'Alonso Torres'],
            [name: 'Matt Feury'],
            [name: 'Alvaro Sanchez-Mariscal'],
            [name: 'Aaron Eischeid'],
            [name: 'Moritz Kobel'],
            [name: 'Manuel Unno Vio'],
            [name: 'David Barragán Merino'],
            [name: 'Yasuharu Nakano'],
            [name: 'Burt Beckwith'],
            [name: 'Timur Salyakhutdinov'],
            [name: 'Zach Legein'],
            [name: 'Tom Marthaler'],
            [name: 'Pablo Alba'],
            [name: 'Eamon Doyle'],
            [name: 'Adam Pounder'],
            [name: 'Gregor Petrin'],
            [name: 'Tom Potts'],
            [name: 'Sabst'],
            [name: 'Alexey Zhokov'],
            [name: 'Jakub Glapa'],
            [name: 'James Hardwick'],
            [name: 'John Keith'],
            [name: 'Eric Helgeson'],
            [name: 'albertop19'],
            [name: 'Andrey T'],
            [name: 'Mattias Reichel'],
    ]
    def issueManagement = [
            system: 'GITHUB',
            url: 'https://github.com/gpc/grails-postgresql-extensions/issues'
    ]
    def scm = [
            url: 'https://github.com/gpc/grails-postgresql-extensions'
    ]
}
