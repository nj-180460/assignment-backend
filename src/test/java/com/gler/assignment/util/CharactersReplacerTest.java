package com.gler.assignment.util;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CharactersReplacerTest {

    @Test
    void shouldReplacePrefixAndSuffixWhenLengthIsGreaterThanTwo() throws BadRequestException {
        String input = "Hello";
        String expected = "Hello => *ell$";
        assertEquals(expected, CharactersReplacer.replacePrefixAndSufferLetter(input));
    }

    @Test
    void shouldReturnEmptyStringWhenLengthIsExactlyTwo() throws BadRequestException {
        String input = "Hi";
        assertEquals("", CharactersReplacer.replacePrefixAndSufferLetter(input));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenLengthIsLessThanTwo() {
        String input = "A";
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            CharactersReplacer.replacePrefixAndSufferLetter(input);
        });
        assertEquals("Text characters is less than 2", exception.getMessage());
    }
}