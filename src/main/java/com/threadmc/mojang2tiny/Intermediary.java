package com.threadmc.mojang2tiny;

import java.io.*;
import java.util.*;

public class Intermediary {
    public final List<IntermediaryEntry> entries;

    public Intermediary(List<IntermediaryEntry> entries) {
        this.entries = entries;
    }

    public static Intermediary load(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<IntermediaryEntry> entries = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] split = line.split("\t");
            if (split[0].equals("v1")) continue;
            switch (split[0]) {
                case "CLASS":
                    entries.add(new IntermediaryEntry.ClassEntry(split[1], split[2]));
                    break;
                case "FIELD":
                    String className = split[1];
                    StringBuilder typeStr = new StringBuilder(split[2]);
                    JvmType type = JvmType.read(typeStr);
                    entries.add(new IntermediaryEntry.FieldEntry(className, type, split[3], split[4]));
                    break;
                case "METHOD":
                    className = split[1];
                    JvmSignature sig = JvmSignature.fromJvmSig(split[2]);
                    entries.add(new IntermediaryEntry.MethodEntry(className, sig, split[3], split[4]));
                    break;
                default:
                    throw new IOException("[Mojang2Tiny] Syntax error at: " + line);
            }
        }
        return new Intermediary(entries);
    }

    public static abstract class IntermediaryEntry {
        public static class ClassEntry extends IntermediaryEntry {
            public final String obfName, intName;
            public ClassEntry(String obfName, String intName) {
                this.obfName = obfName;
                this.intName = intName;
            }
        }
        public static class FieldEntry extends IntermediaryEntry {
            public final String obfClass, obfName, intName;
            public final JvmType obfType;
            public FieldEntry(String obfClass, JvmType obfType, String obfName, String intName) {
                this.obfClass = obfClass;
                this.obfType = obfType;
                this.obfName = obfName;
                this.intName = intName;
            }
        }
        public static class MethodEntry extends IntermediaryEntry {
            public final String obfClass, obfName, intName;
            public final JvmSignature obfSig;
            public MethodEntry(String obfClass, JvmSignature obfSig, String obfName, String intName) {
                this.obfClass = obfClass;
                this.obfSig = obfSig;
                this.obfName = obfName;
                this.intName = intName;
            }
        }
    }
}
