package com.threadmc.mojang2tiny;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JvmSignature {
    private final List<JvmType> params;
    private final JvmType result;

    public JvmSignature(List<JvmType> params, JvmType result) {
        this.params = Collections.unmodifiableList(new ArrayList<>(params));
        this.result = result;
    }

    public static JvmSignature from(List<JvmType> params, JvmType result) {
        return new JvmSignature(params, result);
    }

    public static JvmSignature fromJvmSig(String s) {
        StringBuilder sb = new StringBuilder(s);
        if (sb.charAt(0) != '(') throw new IllegalArgumentException("Expected '('");
        sb.deleteCharAt(0);
        List<JvmType> params = new ArrayList<>();
        while (sb.charAt(0) != ')') {
            params.add(JvmType.read(sb));
        }
        sb.deleteCharAt(0);
        JvmType result = JvmType.read(sb);
        if (sb.length() != 0) throw new IllegalArgumentException("Expected end of signature");
        return new JvmSignature(params, result);
    }

    public static JvmSignature fromReadable(String result, String params) {
        JvmType resultType = JvmType.fromReadable(result);
        List<JvmType> paramTypes = new ArrayList<>();
        if (!params.isEmpty()) {
            for (String s : params.split(",")) {
                paramTypes.add(JvmType.fromReadable(s.trim()));
            }
        }
        return new JvmSignature(paramTypes, resultType);
    }

    public List<JvmType> getParams() { return params; }
    public JvmType getResult() { return result; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (JvmType t : params) sb.append(t.toDescriptor());
        sb.append(')').append(result.toDescriptor());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JvmSignature)) return false;
        JvmSignature other = (JvmSignature)o;
        return params.equals(other.params) && result.equals(other.result);
    }
    @Override
    public int hashCode() { return Objects.hash(params, result); }
}
