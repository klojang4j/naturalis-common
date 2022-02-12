package nl.naturalis.common;

import nl.naturalis.common.check.Check;

public record Tuple2<T,U>(T one, U two) {

    public static <ONE,TWO> Tuple2<ONE,TWO> of(ONE one, TWO two) {
        return new Tuple2(one,two);
    }

    public Tuple2(T one,U two) {
        this.one= Check.notNull(one, "one").ok();
        this.two= Check.notNull(two, "two").ok();
    }
}