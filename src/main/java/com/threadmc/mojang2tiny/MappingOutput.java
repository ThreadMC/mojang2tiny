package com.threadmc.mojang2tiny;

import java.io.*;

public class MappingOutput {
    public static void write(File outFile, Intermediary intermediary, MojangMap mojang, String tinyVersion) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
            if ("v1".equalsIgnoreCase(tinyVersion)) {
                writer.println("v1\tintermediary\tnamed");
            } else {
                writer.println("v2\tintermediary\tnamed");
            }
            // Write all classes
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.ClassEntry) {
                    Intermediary.IntermediaryEntry.ClassEntry ce = (Intermediary.IntermediaryEntry.ClassEntry) entry;
                    String named = findNamedClass(mojang, ce.obfName);
                    writer.println("CLASS\t" + ce.obfName + "\t" + ce.intName + "\t" + (named != null ? named : ce.intName));
                }
            }
            // Write all fields
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.FieldEntry) {
                    Intermediary.IntermediaryEntry.FieldEntry fe = (Intermediary.IntermediaryEntry.FieldEntry) entry;
                    String named = findNamedField(mojang, fe.obfClass, fe.obfName);
                    writer.println("\tFIELD\t" + fe.obfClass + "\t" + fe.obfType.toDescriptor() + "\t" + fe.obfName + "\t" + fe.intName + "\t" + (named != null ? named : fe.intName));
                }
            }
            // Write all methods
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.MethodEntry) {
                    Intermediary.IntermediaryEntry.MethodEntry me = (Intermediary.IntermediaryEntry.MethodEntry) entry;
                    String named = findNamedMethod(mojang, me.obfClass, me.obfName, me.obfSig);
                    writer.println("\tMETHOD\t" + me.obfClass + "\t" + me.obfSig.toString() + "\t" + me.obfName + "\t" + me.intName + "\t" + (named != null ? named : me.intName));
                }
            }
        }
    }

    public static void write(File outFile, Intermediary intermediary, MojangMap mojang, String tinyVersion, ProgressCallback progressCallback) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
            if ("v1".equalsIgnoreCase(tinyVersion)) {
                writer.println("v1\tintermediary\tnamed");
            } else {
                writer.println("v2\tintermediary\tnamed");
            }
            // Write all classes
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.ClassEntry) {
                    Intermediary.IntermediaryEntry.ClassEntry ce = (Intermediary.IntermediaryEntry.ClassEntry) entry;
                    String named = findNamedClass(mojang, ce.obfName);
                    writer.println("CLASS\t" + ce.obfName + "\t" + ce.intName + "\t" + (named != null ? named : ce.intName));
                }
            }
            // Write all fields
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.FieldEntry) {
                    Intermediary.IntermediaryEntry.FieldEntry fe = (Intermediary.IntermediaryEntry.FieldEntry) entry;
                    String named = findNamedField(mojang, fe.obfClass, fe.obfName);
                    writer.println("\tFIELD\t" + fe.obfClass + "\t" + fe.obfType.toDescriptor() + "\t" + fe.obfName + "\t" + fe.intName + "\t" + (named != null ? named : fe.intName));
                }
            }
            // Write all methods
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.MethodEntry) {
                    Intermediary.IntermediaryEntry.MethodEntry me = (Intermediary.IntermediaryEntry.MethodEntry) entry;
                    String named = findNamedMethod(mojang, me.obfClass, me.obfName, me.obfSig);
                    writer.println("\tMETHOD\t" + me.obfClass + "\t" + me.obfSig.toString() + "\t" + me.obfName + "\t" + me.intName + "\t" + (named != null ? named : me.intName));
                }
            }
        }
    }

    public interface ProgressCallback {
        void onProgress(int progress, int total);
    }

    private static String findNamedClass(MojangMap mojang, String obfName) {
        for (MojangMap.MojangMapEntry entry : mojang.entries) {
            if (entry instanceof MojangMap.MojangMapEntry.ClassEntry) {
                MojangMap.MojangMapEntry.ClassEntry ce = (MojangMap.MojangMapEntry.ClassEntry) entry;
                if (ce.obfName.equals(obfName)) return ce.deobfName;
            }
        }
        return null;
    }
    private static String findNamedField(MojangMap mojang, String obfClass, String obfName) {
        for (MojangMap.MojangMapEntry entry : mojang.entries) {
            if (entry instanceof MojangMap.MojangMapEntry.FieldEntry) {
                MojangMap.MojangMapEntry.FieldEntry fe = (MojangMap.MojangMapEntry.FieldEntry) entry;
                if (fe.obfClass.equals(obfClass) && fe.obfName.equals(obfName)) return fe.deobfName;
            }
        }
        return null;
    }
    private static String findNamedMethod(MojangMap mojang, String obfClass, String obfName, JvmSignature obfSig) {
        for (MojangMap.MojangMapEntry entry : mojang.entries) {
            if (entry instanceof MojangMap.MojangMapEntry.MethodEntry) {
                MojangMap.MojangMapEntry.MethodEntry me = (MojangMap.MojangMapEntry.MethodEntry) entry;
                if (me.obfClass.equals(obfClass) && me.obfName.equals(obfName) && me.deobfSig.toString().equals(obfSig.toString())) return me.deobfName;
            }
        }
        return null;
    }
}
