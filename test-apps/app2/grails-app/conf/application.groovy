// GORM 8.0.0 for Hibernate 7 never creates the sequence of the default 'native' generator on PostgreSQL
// (GrailsNativeGenerator swallows the initialization of its SequenceStyleGenerator delegate), so use 'sequence'
grails.gorm.default.mapping = {
    id generator: 'sequence'
}
