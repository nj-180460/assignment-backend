package com.gler.assignment.controller;

import com.gler.assignment.util.CharactersReplacer;
import org.apache.coyote.BadRequestException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class TextReplaceController {


    @GetMapping("/replace")
    public ResponseEntity<String> textReplacer(
            @Param("texts") String texts
    ) {
        String response = "";
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        try {
            response = CharactersReplacer.replacePrefixAndSufferLetter(texts);
        } catch (BadRequestException badRequestException) {
            response = badRequestException.getMessage();
            httpStatusCode = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity
                .status(httpStatusCode)
                .body(response);
    }

}
