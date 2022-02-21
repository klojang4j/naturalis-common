package nl.naturalis.common;

public record IntPair(int one,int two) {

    public static IntPair of(int one,int two) {
        return new IntPair(one, two);
    }

 }
