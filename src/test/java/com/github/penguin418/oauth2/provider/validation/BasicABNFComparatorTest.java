package com.github.penguin418.oauth2.provider.validation;

import com.github.penguin418.oauth2.provider.validation.BasicABNFComparator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BasicABNFComparatorTest {
    final String[] vschars = {" ", "!", "\"", "#", /*...*/ "{", "|", "}", "~"};
    final String[] nqchars = {"!", /*...*/ "#", "$", "%", "&", /*...*/ "X", "Y", "z", "[", "]", "^", "_", "`", /*...*/ "{", "|", "}", "~"};
    final String[] nqschars = {" ", "!", "#", "$", "%", "&", "[", "]", "^",/*...*/ "{", "|", "}", "~"};
    final String[] unicodecharnocrlf = {"\t", " ", "!", "\"", "#", /*...*/ "{", "|", "}", "~", "¡", "¢", "£"};

    @Test
    void isVSCHAR() {
        assertTrue(Arrays.stream(vschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isVSCHAR(c))));
        assertTrue(Arrays.stream(nqchars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isVSCHAR(c))));
        assertTrue(Arrays.stream(nqschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isVSCHAR(c))));
        assertFalse(Arrays.stream(unicodecharnocrlf).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isVSCHAR(c))));
    }

    @Test
    void isNQCHAR() {
        assertFalse(Arrays.stream(vschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQCHAR(c))));
        assertTrue(Arrays.stream(nqchars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQCHAR(c))));
        assertFalse(Arrays.stream(nqschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQCHAR(c))));
        assertFalse(Arrays.stream(unicodecharnocrlf).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQCHAR(c))));
    }

    @Test
    void isNQSCHAR() {
        assertFalse(Arrays.stream(vschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQSCHAR(c))));
        assertTrue(Arrays.stream(nqchars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQSCHAR(c))));
        Arrays.stream(nqschars)
                .forEach(str -> {
                    boolean res = (BasicABNFComparator.isNQSCHAR(str));
                    System.out.println(str+ "+" + res);
                });
        assertTrue(Arrays.stream(nqschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQSCHAR(c))));
        assertFalse(Arrays.stream(unicodecharnocrlf).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isNQSCHAR(c))));
    }

    @Test
    void isUNICODECHARNOCRLF() {
        assertTrue(Arrays.stream(vschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isUNICODECHARNOCRLF(c))));
        assertTrue(Arrays.stream(nqchars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isUNICODECHARNOCRLF(c))));
        assertTrue(Arrays.stream(nqschars).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isUNICODECHARNOCRLF(c))));
        assertTrue(Arrays.stream(unicodecharnocrlf).allMatch(c->Boolean.TRUE.equals(BasicABNFComparator.isUNICODECHARNOCRLF(c))));
    }
}