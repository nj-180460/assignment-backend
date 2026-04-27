package com.gler.assignment.controller;

import com.gler.assignment.util.CharactersReplacer;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TextReplaceController.class)
class TextReplaceControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    void testReplaceSuccess() throws Exception {
        mockMvc.perform(get("/v1/replace").param("texts", "Spring"))
                .andExpect(status().isOk())
                .andExpect(content().string("Spring => *prin$"));
    }

    @Test
    void testReplaceSuccessWithEmpyBody() throws Exception {
        mockMvc.perform(get("/v1/replace").param("texts", "ab"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testReplaceBadRequest() throws Exception {
        // Sending a string with length < 2 to trigger the catch block
        mockMvc.perform(get("/v1/replace").param("texts", "X"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Text characters is less than 2"));
    }


    /**
     * More tests cases
     */
    @ParameterizedTest
    @MethodSource("provideStringsForReplacement")
    void shouldReplacePrefixAndSuffixCorrecty(String input, String expected) throws BadRequestException {
        String result = CharactersReplacer.replacePrefixAndSufferLetter(input);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideStringsForReplacement() {
        return Stream.of(
                // Logic: Original + " => *" + (middle) + "$"
                Arguments.of("elephant", "elephant => *lephan$"),
                Arguments.of("home", "home => *om$"),
                Arguments.of("abc#20xyz", "abc#20xyz => *bc#20xy$"),
                Arguments.of("abc", "abc => *b$"),
                Arguments.of("TestingCodeAssignmentProject", "TestingCodeAssignmentProject => *estingCodeAssignmentProjec$"),
                Arguments.of("My Test Project", "My Test Project => *y Test Projec$"),
                Arguments.of("ab", "")
        );
    }
}