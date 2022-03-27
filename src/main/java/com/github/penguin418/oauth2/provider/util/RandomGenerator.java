package com.github.penguin418.oauth2.provider.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomGenerator {
    private static final String[] alpha = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","W","x","y","z", "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private static final String[] number = new String[]{"0","1","2","3","4","5","6","7","8","9"};
    private static final Random random = new Random();

    /**
     *
     * VSCHAR
     * 1*( ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/" ) *"="
     */
    public static String generateAccessToken(int length){
        List<String> vschar = new ArrayList<String>(){{
            addAll(Arrays.asList(alpha));
            addAll(Arrays.asList(number));
            addAll(Arrays.asList("-",".","_","~","+","/"));
        }};

        int frontPart = random.nextInt(length-1);
        StringBuilder sb = new StringBuilder();
        IntStream.range(0,frontPart).forEach(i-> sb.append(vschar.get(random.nextInt(vschar.size()))));
        IntStream.range(frontPart,length).forEach(i-> sb.append("="));
        return sb.toString();
    }
}
