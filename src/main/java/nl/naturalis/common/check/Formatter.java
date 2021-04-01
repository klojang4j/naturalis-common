package nl.naturalis.common.check;

import java.util.function.Function;

interface Formatter extends Function<MessageData, String> {
  // ...
}
