package test.array

import groovy.transform.CompileStatic

@CompileStatic
class UuidBuilder {

    static UUID createUUID(String name) {
        UUID.nameUUIDFromBytes(name.bytes)
    }

    static List<UUID> createUUIDs(List<String> names) {
        names.collect { createUUID(it) }
    }
}
