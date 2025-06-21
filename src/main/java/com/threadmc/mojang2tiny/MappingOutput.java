package com.threadmc.mojang2tiny;

import java.io.*;
import java.util.*;

public class MappingOutput {
    public static void write(File dir, Intermediary intermediary, MojangMap mojang) throws IOException {
        if (dir.exists()) deleteDir(dir);
        dir.mkdirs();
        for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
            if (entry instanceof Intermediary.IntermediaryEntry.ClassEntry) {
                Intermediary.IntermediaryEntry.ClassEntry ce = (Intermediary.IntermediaryEntry.ClassEntry) entry;
                System.out.println(ce.obfName + " -> " + ce.intName);
            }
        }
        for (MojangMap.MojangMapEntry entry : mojang.entries) {
            if (entry instanceof MojangMap.MojangMapEntry.ClassEntry) {
                MojangMap.MojangMapEntry.ClassEntry ce = (MojangMap.MojangMapEntry.ClassEntry) entry;
                File classDir = new File(dir, ce.deobfName.substring(0, ce.deobfName.lastIndexOf('/') >= 0 ? ce.deobfName.lastIndexOf('/') : 0));
                classDir.mkdirs();
                String fileName = ce.deobfName.substring(ce.deobfName.lastIndexOf('/') + 1) + ".tiny";
                File outFile = new File(classDir, fileName);
                String intName = findIntName(intermediary, ce.obfName);
                if (intName != null) {
                    System.out.println(" - Class " + ce.deobfName);
                    try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
                        writer.println("CLASS " + intName + " " + ce.deobfName);
                        for (MojangMap.MojangMapEntry sub : mojang.entries) {
                            if (sub instanceof MojangMap.MojangMapEntry.FieldEntry) {
                                MojangMap.MojangMapEntry.FieldEntry fe = (MojangMap.MojangMapEntry.FieldEntry) sub;
                                if (fe.deobfClass.equals(ce.deobfName)) {
                                    String fieldIntName = findIntFieldName(intermediary, fe.obfClass, fe.obfName);
                                    if (fieldIntName != null) {
                                        System.out.println("   - Field " + fe.deobfName);
                                        JvmType intType = obfToIntType(deobfToObfType(fe.deobfType, mojang), intermediary);
                                        writer.println("\tFIELD " + fieldIntName + " " + fe.deobfName + " " + intType.toDescriptor());
                                    } else {
                                        System.err.println("Can't find intermediary name for field '" + fe.deobfClass + " " + fe.deobfName + "'");
                                    }
                                }
                            } else if (sub instanceof MojangMap.MojangMapEntry.MethodEntry) {
                                MojangMap.MojangMapEntry.MethodEntry me = (MojangMap.MojangMapEntry.MethodEntry) sub;
                                if (me.deobfClass.equals(ce.deobfName)) {
                                    String methodIntName = findIntMethodName(intermediary, me.obfClass, me.obfName);
                                    if (methodIntName != null) {
                                        System.out.println("   - Method " + me.deobfName);
                                        JvmSignature intSig = obfToIntSig(deobfToObfSig(me.deobfSig, mojang), intermediary);
                                        writer.println("\tMETHOD " + methodIntName + " " + me.deobfName + " " + intSig.toString());
                                    } else {
                                        System.err.println("Can't find intermediary name for method '" + me.deobfClass + " " + me.deobfName + me.deobfSig + "'");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("Can't find intermediary name for class '" + ce.obfName + "'!");
                }
            }
        }
    }

    private static String findIntName(Intermediary intermediary, String obfName) {
        for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
            if (entry instanceof Intermediary.IntermediaryEntry.ClassEntry) {
                Intermediary.IntermediaryEntry.ClassEntry ce = (Intermediary.IntermediaryEntry.ClassEntry) entry;
                if (ce.obfName.equals(obfName)) return ce.intName;
            }
        }
        return null;
    }
    private static String findIntFieldName(Intermediary intermediary, String obfClass, String obfName) {
        for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
            if (entry instanceof Intermediary.IntermediaryEntry.FieldEntry) {
                Intermediary.IntermediaryEntry.FieldEntry fe = (Intermediary.IntermediaryEntry.FieldEntry) entry;
                if (fe.obfClass.equals(obfClass) && fe.obfName.equals(obfName)) return fe.intName;
            }
        }
        return null;
    }
    private static String findIntMethodName(Intermediary intermediary, String obfClass, String obfName) {
        for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
            if (entry instanceof Intermediary.IntermediaryEntry.MethodEntry) {
                Intermediary.IntermediaryEntry.MethodEntry me = (Intermediary.IntermediaryEntry.MethodEntry) entry;
                if (me.obfClass.equals(obfClass) && me.obfName.equals(obfName)) return me.intName;
            }
        }
        return null;
    }

    private static JvmType deobfToObfType(JvmType t, MojangMap mojang) {
        if (t instanceof JvmType.ClassType) {
            String deobfName = ((JvmType.ClassType) t).className;
            for (MojangMap.MojangMapEntry entry : mojang.entries) {
                if (entry instanceof MojangMap.MojangMapEntry.ClassEntry) {
                    MojangMap.MojangMapEntry.ClassEntry ce = (MojangMap.MojangMapEntry.ClassEntry) entry;
                    if (ce.deobfName.equals(deobfName)) return new JvmType.ClassType(ce.obfName);
                }
            }
            return t;
        } else if (t instanceof JvmType.ArrayType) {
            return new JvmType.ArrayType(deobfToObfType(((JvmType.ArrayType) t).elementType, mojang));
        } else {
            return t;
        }
    }
    private static JvmType obfToIntType(JvmType t, Intermediary intermediary) {
        if (t instanceof JvmType.ClassType) {
            String obfName = ((JvmType.ClassType) t).className;
            for (Intermediary.IntermediaryEntry entry : intermediary.entries) {
                if (entry instanceof Intermediary.IntermediaryEntry.ClassEntry) {
                    Intermediary.IntermediaryEntry.ClassEntry ce = (Intermediary.IntermediaryEntry.ClassEntry) entry;
                    if (ce.obfName.equals(obfName)) return new JvmType.ClassType(ce.intName);
                }
            }
            return t;
        } else if (t instanceof JvmType.ArrayType) {
            return new JvmType.ArrayType(obfToIntType(((JvmType.ArrayType) t).elementType, intermediary));
        } else {
            return t;
        }
    }
    private static JvmSignature deobfToObfSig(JvmSignature sig, MojangMap mojang) {
        List<JvmType> params = new ArrayList<>();
        for (JvmType t : sig.getParams()) params.add(deobfToObfType(t, mojang));
        JvmType result = deobfToObfType(sig.getResult(), mojang);
        return JvmSignature.from(params, result);
    }
    private static JvmSignature obfToIntSig(JvmSignature sig, Intermediary intermediary) {
        List<JvmType> params = new ArrayList<>();
        for (JvmType t : sig.getParams()) params.add(obfToIntType(t, intermediary));
        JvmType result = obfToIntType(sig.getResult(), intermediary);
        return JvmSignature.from(params, result);
    }
    private static void deleteDir(File dir) throws IOException {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) deleteDir(f);
        }
        dir.delete();
    }
}
