/**
 * A set of classes that expose their backing array (or whatever storage mechanism
 * they use) and generally don't perform range checking. Not something you would
 * expose in a public API, but useful for package-private and/or intra-modular
 * exchanges with a high number if reads and/or writes in tight loops. N.B. these
 * classes have nothing to do with {@code com.sun.misc.Unsafe}.
 *
 * @author Ayco Holleman
 */
package nl.naturalis.common.io;
