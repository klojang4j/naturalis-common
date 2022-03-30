package nl.naturalis.common.check;

import java.util.function.Function;

@FunctionalInterface
interface PrefabMsgFormatter extends Function<MsgArgs, String> {}
