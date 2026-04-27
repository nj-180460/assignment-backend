package com.gler.assignment.util;

import org.apache.coyote.BadRequestException;

public final class CharactersReplacer {

    private CharactersReplacer () {
        throw new UnsupportedOperationException("Cannot be instantiated. Use static methods only.");
    }


    public static String replacePrefixAndSufferLetter(String texts) throws BadRequestException {
        int length = texts.length();
        if(length == 2) {
            return "";
        } else if (length < 2) {
            throw new BadRequestException("Text characters is less than 2");
        }
        return texts + " => *" + texts.substring(1, length-1) + "$";
    }

}
