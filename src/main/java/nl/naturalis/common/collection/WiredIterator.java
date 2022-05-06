package nl.naturalis.common.collection;

import java.util.Iterator;

public sealed interface WiredIterator<E> extends Iterator<E> permits WiredList.WiredIteratorImpl {
  
}
