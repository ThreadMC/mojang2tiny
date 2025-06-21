package com.threadmc.mojang2tiny;

import java.util.Objects;

public abstract class JvmType {
    public static JvmType fromReadable(String s) {
        if (s.endsWith("[]")) {
            return new ArrayType(fromReadable(s.substring(0, s.length() - 2)));
        }
        switch (s) {
            case "void": return new PrimitiveType(PrimitiveType.Kind.VOID);
            case "boolean": return new PrimitiveType(PrimitiveType.Kind.BOOLEAN);
            case "byte": return new PrimitiveType(PrimitiveType.Kind.BYTE);
            case "short": return new PrimitiveType(PrimitiveType.Kind.SHORT);
            case "char": return new PrimitiveType(PrimitiveType.Kind.CHAR);
            case "int": return new PrimitiveType(PrimitiveType.Kind.INT);
            case "long": return new PrimitiveType(PrimitiveType.Kind.LONG);
            case "float": return new PrimitiveType(PrimitiveType.Kind.FLOAT);
            case "double": return new PrimitiveType(PrimitiveType.Kind.DOUBLE);
            default: return new ClassType(s.replace('.', '/'));
        }
    }

    public static JvmType read(StringBuilder s) {
        char first = s.charAt(0);
        s.deleteCharAt(0);
        switch (first) {
            case 'V': return new PrimitiveType(PrimitiveType.Kind.VOID);
            case 'Z': return new PrimitiveType(PrimitiveType.Kind.BOOLEAN);
            case 'B': return new PrimitiveType(PrimitiveType.Kind.BYTE);
            case 'S': return new PrimitiveType(PrimitiveType.Kind.SHORT);
            case 'C': return new PrimitiveType(PrimitiveType.Kind.CHAR);
            case 'I': return new PrimitiveType(PrimitiveType.Kind.INT);
            case 'J': return new PrimitiveType(PrimitiveType.Kind.LONG);
            case 'F': return new PrimitiveType(PrimitiveType.Kind.FLOAT);
            case 'D': return new PrimitiveType(PrimitiveType.Kind.DOUBLE);
            case 'L': {
                int end = s.indexOf(";");
                String className = s.substring(0, end);
                s.delete(0, end + 1);
                return new ClassType(className);
            }
            case '[': return new ArrayType(read(s));
            default: throw new IllegalArgumentException("[Mojang2Tiny] Malformed JVM type: " + first);
        }
    }

    public abstract String toDescriptor();

    public static class PrimitiveType extends JvmType {
        public enum Kind {
            VOID, BOOLEAN, BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE
        }
        public final Kind kind;
        public PrimitiveType(Kind kind) { this.kind = kind; }
        @Override
        public String toDescriptor() {
            switch (kind) {
                case VOID: return "V";
                case BOOLEAN: return "Z";
                case BYTE: return "B";
                case SHORT: return "S";
                case CHAR: return "C";
                case INT: return "I";
                case LONG: return "J";
                case FLOAT: return "F";
                case DOUBLE: return "D";
                default: throw new AssertionError();
            }
        }
        @Override public boolean equals(Object o) { return o instanceof PrimitiveType && kind == ((PrimitiveType)o).kind; }
        @Override public int hashCode() { return kind.hashCode(); }
        @Override public String toString() { return toDescriptor(); }
    }

    public static class ClassType extends JvmType {
        public final String className;
        public ClassType(String className) { this.className = className; }
        @Override public String toDescriptor() { return "L" + className + ";"; }
        @Override public boolean equals(Object o) { return o instanceof ClassType && className.equals(((ClassType)o).className); }
        @Override public int hashCode() { return className.hashCode(); }
        @Override public String toString() { return toDescriptor(); }
    }

    public static class ArrayType extends JvmType {
        public final JvmType elementType;
        public ArrayType(JvmType elementType) { this.elementType = elementType; }
        @Override public String toDescriptor() { return "[" + elementType.toDescriptor(); }
        @Override public boolean equals(Object o) { return o instanceof ArrayType && elementType.equals(((ArrayType)o).elementType); }
        @Override public int hashCode() { return Objects.hash(elementType); }
        @Override public String toString() { return toDescriptor(); }
    }
}
