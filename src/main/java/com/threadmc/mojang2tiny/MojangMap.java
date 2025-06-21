package com.threadmc.mojang2tiny;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MojangMap {
    public final List<MojangMapEntry> entries;

    public MojangMap(List<MojangMapEntry> entries) {
        this.entries = entries;
    }

    public static MojangMap load(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Pattern classRe = Pattern.compile("^([\\w$.]+|[\\w$.]+\\.package-info) -> ([\\w$.]+):$");
        Pattern fieldRe = Pattern.compile("^\\s+([\\w$.\\[\\]]+) ([\\w$]+) -> ([\\w$]+)$");
        Pattern methodRe = Pattern.compile("^\\s+([0-9]+:[0-9]+:)?([\\w$.\\[\\]]+) ([\\w$]+|<clinit>|<init>)\\(([^)]*)\\) -> ([\\w$]+|<clinit>|<init>)$");
        List<MojangMapEntry> entries = new ArrayList<>();
        String line;
        String lastDeobfClass = null, lastObfClass = null;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) continue;
            Matcher m;
            m = classRe.matcher(line);
            if (m.matches()) {
                lastDeobfClass = m.group(1).replace('.', '/');
                lastObfClass = m.group(2).replace('.', '/');
                entries.add(new MojangMapEntry.ClassEntry(lastDeobfClass, lastObfClass));
                continue;
            }
            m = fieldRe.matcher(line);
            if (m.matches()) {
                String deobfType = m.group(1);
                String deobfName = m.group(2);
                String obfName = m.group(3);
                entries.add(new MojangMapEntry.FieldEntry(lastDeobfClass, lastObfClass, JvmType.fromReadable(deobfType), deobfName, obfName));
                continue;
            }
            m = methodRe.matcher(line);
            if (m.matches()) {
                String deobfType = m.group(2);
                String deobfName = m.group(3);
                String deobfParams = m.group(4);
                String obfName = m.group(5);
                entries.add(new MojangMapEntry.MethodEntry(lastDeobfClass, lastObfClass, JvmSignature.fromReadable(deobfType, deobfParams), deobfName, obfName));
                continue;
            }
            throw new IOException("Syntax error, ignoring: " + line);
        }
        return new MojangMap(entries);
    }

    public static MojangMap empty() {
        return new MojangMap(new ArrayList<>());
    }

    public void combine(MojangMap other) {
        this.entries.addAll(other.entries);
    }

    public static abstract class MojangMapEntry {
        public static class ClassEntry extends MojangMapEntry {
            public final String deobfName, obfName;
            public ClassEntry(String deobfName, String obfName) {
                this.deobfName = deobfName;
                this.obfName = obfName;
            }
        }
        public static class FieldEntry extends MojangMapEntry {
            public final String deobfClass, obfClass, deobfName, obfName;
            public final JvmType deobfType;
            public FieldEntry(String deobfClass, String obfClass, JvmType deobfType, String deobfName, String obfName) {
                this.deobfClass = deobfClass;
                this.obfClass = obfClass;
                this.deobfType = deobfType;
                this.deobfName = deobfName;
                this.obfName = obfName;
            }
        }
        public static class MethodEntry extends MojangMapEntry {
            public final String deobfClass, obfClass, deobfName, obfName;
            public final JvmSignature deobfSig;
            public MethodEntry(String deobfClass, String obfClass, JvmSignature deobfSig, String deobfName, String obfName) {
                this.deobfClass = deobfClass;
                this.obfClass = obfClass;
                this.deobfSig = deobfSig;
                this.deobfName = deobfName;
                this.obfName = obfName;
            }
        }
    }
}
