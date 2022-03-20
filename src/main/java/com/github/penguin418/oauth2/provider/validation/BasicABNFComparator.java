package com.github.penguin418.oauth2.provider.validation;

import javax.xml.stream.events.Characters;
import java.text.Normalizer;

/**
 * Augmented Backus-Naur Form (ABNF) 문법
 */
public class BasicABNFComparator {
    public static boolean isVSCHAR(String str) {
        return str.matches("^([\\x{20}-\\x{7e}]+)$");
//        return str.chars().allMatch(c -> 0x20 <= c && c <= 0x7e);
    }

    public static boolean isNQCHAR(String str) {
        return str.matches("^([\\x{21}]+|[\\x{23}-\\x{5b}]+|[\\x{5d}-\\x{7e}]+)$");
//        return str.chars().allMatch(c -> 0x21 == c || (0x23<= c && c <=0x5b) || (0x5d<= c && c <= 0x7e));
    }

    public static boolean isNQSCHAR(String str) {
        return str.matches("^([\\x{20}-\\x{21}]+|[\\x{23}-\\x{5b}]+|[\\x{5d}-\\x{7e}]+)$");
//        return str.chars().allMatch(c -> 0x20==c || 0x21 == c || (0x23<= c && c <=0x5b) || (0x5d<= c && c <= 0x7e));
    }

    public static boolean isUNICODECHARNOCRLF(String str) {
        return str.matches("^([\\x{09}]+|[\\x{20}-\\x{7e}]+|[\\x{80}-\\x{d7ff}]+|[\\x{e000}-\\x{fffd}]+|[\\x{10000}-\\x{10ffff}]+)$");
    }

}
