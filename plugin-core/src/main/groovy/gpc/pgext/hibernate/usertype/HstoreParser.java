package gpc.pgext.hibernate.usertype;

import java.io.Serial;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.postgresql.util.PGobject;

import org.springframework.util.Assert;

/**
 * This class is an extended version of HStore.java from https://code.google.com/p/pg-spring-type-mapper/
 */
public class HstoreParser extends PGobject implements Iterable<Map.Entry<String, String>> {

    @Serial
    private static final long serialVersionUID = -2491617655490561600L;

    private int length;

    private static final String BACKSLASH = "\\";
    private static final String DOUBLE_QUOTE = "\"";

    private static final String BACKSLASH_PLACEHOLDER = "!#BS#!";
    private static final String DOUBLE_QUOTE_PLACEHOLDER = "!#DQ#!";

    public HstoreParser(String rawValue) {
        this.type = "hstore";
        setUnescapedValueAndLength(rawValue);
    }

    // To include a double quote or a backslash in a key or value, escape it with a backslash. (https://www.postgresql.org/docs/current/static/hstore.html)
    private void setUnescapedValueAndLength(String rawValue) {
        this.value = rawValue
                .replace(BACKSLASH + DOUBLE_QUOTE, DOUBLE_QUOTE_PLACEHOLDER)
                .replace(BACKSLASH + BACKSLASH, BACKSLASH_PLACEHOLDER);
        this.length = this.value == null ? 0 : this.value.length();
    }

    @Override
    public void setValue(String rawValue) {
        Assert.state("hstore".equals(type), "HStore database type name should be 'hstore'");
        setUnescapedValueAndLength(rawValue);
    }

    public Map<String, String> asMap() {
        HashMap<String, String> r = new HashMap<>();
        try {
            for (final HStoreIterator iterator = new HStoreIterator(); iterator.hasNext(); ) {
                final HStoreEntry entry = iterator.rawNext();
                r.put(replaceEscapePlaceholders(entry.key), replaceEscapePlaceholders(entry.value));
            }
        } catch (HstoreParseException e) {
            throw new IllegalStateException(e);
        }
        return r;
    }

    private String replaceEscapePlaceholders(String text) {
        return text == null ? null : text
                .replace(BACKSLASH_PLACEHOLDER, BACKSLASH)
                .replace(DOUBLE_QUOTE_PLACEHOLDER, DOUBLE_QUOTE);
    }

    private static class HStoreEntry implements Entry<String, String> {

        private final String key;
        private String value;

        HStoreEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            final String oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    private static enum ParseState {
        WaitingForKey, WaitingForEquals, WaitingForGreater, WaitingForValue, WaitingForComma
    }

    private static final char[] QUOTE = {'"', '\''};
    private static final char NO_QUOTE_CHAR = '\0';
    private static final char EQUALS = '=';
    private static final char GREATER = '>';
    private static final char COMMA = ',';
    private static final String NULL = "NULL";

    private class HStoreIterator implements Iterator<Map.Entry<String, String>> {
        private int position;
        char currentQuoteChar = '\0';
        private HStoreEntry lastReturned;
        private HStoreEntry nextEntry;

        public HStoreIterator() throws HstoreParseException {
            this.position = -1;
            advance();
        }

        @Override
        public boolean hasNext() {
            return nextEntry != null;
        }

        private HStoreEntry rawNext() throws NoSuchElementException, HstoreParseException {
            if (nextEntry == null) {
                throw new NoSuchElementException();
            }
            lastReturned = nextEntry;
            advance();
            return lastReturned;
        }

        @Override
        public Entry<String, String> next() throws NoSuchElementException, IllegalStateException {
            try {
                return rawNext();
            } catch (HstoreParseException e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * Advance in parsing the rawValue string and assign the nextValue
         * It creates a new nextElement or assigns null to it if there are no more elements
         *
         * @throws HstoreParseException
         */
        private void advance() throws HstoreParseException {
            String elementKey = null;
            String elementValue = null;
            ParseState state = ParseState.WaitingForKey;
            loop:
                while (position < length - 1) {
                    final char ch = value.charAt(++position);
                    switch (state) {
                    case WaitingForKey:
                        if (Character.isWhitespace(ch)) {
                            continue;
                        }
                        for (char q : QUOTE) {
                            if (ch == q) {
                                currentQuoteChar = q;
                                elementKey = advanceQuoted();
                            }
                        }
                        if (currentQuoteChar == NO_QUOTE_CHAR) {
                            // we have non-quote char, so start loading the key
                            elementKey = advanceWord(EQUALS);
                            // hstore does not support NULL keys, so NULLs are loaded as usual strings
                        }
                        currentQuoteChar = NO_QUOTE_CHAR;
                        state = ParseState.WaitingForEquals;
                        continue;
                    case WaitingForEquals:
                        if (Character.isWhitespace(ch)) {
                            continue;
                        }
                        if (ch == EQUALS) {
                            state = ParseState.WaitingForGreater;
                            continue;
                        }

                        throw new HstoreParseException("Expected '=>' key-value separator", position);

                    case WaitingForGreater:
                        if (ch == GREATER) {
                            state = ParseState.WaitingForValue;
                            continue;
                        }

                        throw new HstoreParseException("Expected '=>' key-value separator", position);

                    case WaitingForValue:
                        if (Character.isWhitespace(ch)) {
                            continue;
                        }
                        for (char q : QUOTE) {
                            if (ch == q) {
                                currentQuoteChar = q;
                                elementValue = advanceQuoted();
                            }
                        }
                        if (currentQuoteChar == NO_QUOTE_CHAR) {
                            // we have non-quote char, so start loading the key
                            elementValue = advanceWord(COMMA);
                            // hstore supports NULL values, so if unquoted NULL is there, it is rewritten to null
                            if (NULL.equalsIgnoreCase(elementValue)) {
                                elementValue = null;
                            }
                        }
                        currentQuoteChar = NO_QUOTE_CHAR;
                        state = ParseState.WaitingForComma;
                        continue;
                    case WaitingForComma:
                        if (Character.isWhitespace(ch)) {
                            continue;
                        }
                        if (ch == COMMA) {
                            // we are done
                            break loop;
                        }

                        throw new HstoreParseException("Cannot find comma as an end of the value", position);

                    default:
                        throw new IllegalStateException("Unknown HstoreParser state");
                    }
                } // loop
            // here we either consumed whole string or we found a comma
            if (state == ParseState.WaitingForKey) {
                // string was consumed when waiting for key, so we are done with processing
                nextEntry = null;
                return;
            }
            if (state != ParseState.WaitingForComma) {
                throw new HstoreParseException("Unexpected end of string", position);
            }
            if (elementKey == null) {
                throw new HstoreParseException("Internal parsing error", position);
            }
            // init nextValue
            nextEntry = new HStoreEntry(elementKey, elementValue);
        }

        private String advanceQuoted() throws HstoreParseException {
            final int firstQuotePosition = position;
            StringBuilder sb = null;
            boolean insideQuote = true;
            while (position < length - 1) {
                char ch = value.charAt(++position);
                if (ch == currentQuoteChar) {
                    // we saw a quote, it is either a closing quote, or it is a quoted quote
                    final int nextPosition = position + 1;
                    if (nextPosition < length) {
                        final char nextCh = value.charAt(nextPosition);
                        if (nextCh == currentQuoteChar) {
                            // it was a double quote, so we have to push a quote into the result
                            if (sb == null) {
                                sb = new StringBuilder(value.substring(firstQuotePosition + 1, nextPosition));
                            } else {
                                sb.append(currentQuoteChar);
                            }
                            position++;
                            continue;
                        }
                    }
                    // it was a closing quote as we either ware are at the end of the rawValue string
                    // or we could not find the next quote
                    insideQuote = false;
                    break;
                }

                if (sb != null) {
                    sb.append(ch);
                }
            }
            if (insideQuote) {
                throw new HstoreParseException("Quote at string position " + firstQuotePosition + " is not closed", position);
            }
            if (sb == null) {
                // we consumed the last quote
                return value.substring(firstQuotePosition + 1, position);
            }

            return sb.toString();
        }

        private String advanceWord(final char stopAtChar) throws HstoreParseException {
            final int firstWordPosition = position;
            while (position < length) {
                final char ch = value.charAt(position);
                if (ch == currentQuoteChar) {
                    throw new HstoreParseException("Unexpected quote in word", position);
                }
                if (Character.isWhitespace(ch) || ch == stopAtChar) {
                    break;
                }
                position++;
            }
            // step back as we are already one char away
            position--;
            // substring is using quite a strange way of defining end position
            return value.substring(firstWordPosition, position + 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        try {
            return new HStoreIterator();
        } catch (HstoreParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
