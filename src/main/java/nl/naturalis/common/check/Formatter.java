package nl.naturalis.common.check;

import java.util.function.Function;

@FunctionalInterface
interface Formatter extends Function<MsgArgs, String> {}
