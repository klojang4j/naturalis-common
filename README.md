# naturalis-common

Naturalis Java Language Extensions Library (Java)

A Java module containing basic language extensions and utility classes. Design goals:

- Small footprint.
- Self-contained. Zero dependencies outside the `java.*` namespace.
- Avoid duplication of functionality that is already present in libraries such as Apache Commons and Google Guava. However, since it is also meant to be self-contained, some overlap is inevitable. For example, this library does contain a dressed-down ["string utils"](src/main/java/nl/naturalis/common/StringMethods.java) class, because it's needed by the other classes.



