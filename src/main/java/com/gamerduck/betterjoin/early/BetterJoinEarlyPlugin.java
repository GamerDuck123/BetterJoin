package com.gamerduck.betterjoin.early;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import org.objectweb.asm.*;

public class BetterJoinEarlyPlugin implements ClassTransformer {

    @Override
    public byte[] transform(String name, String path, byte[] bytes) {
        if (name.equals("com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems$PlayerRemovedSystem")) {
            return transformClass(bytes);
        }
        return bytes;
    }

    private byte[] transformClass(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (name.equals("onEntityRemoved")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        private boolean inBroadcastCall = false;
                        private int stackDepth = 0;

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            if (!inBroadcastCall && name.equals("broadcastMessageToPlayers") && owner.contains("PlayerUtil")) {
                                inBroadcastCall = true;
                                Label skipLabel = new Label();
                                Label endLabel = new Label();
                                mv.visitInsn(Opcodes.ICONST_0);
                                mv.visitJumpInsn(Opcodes.IFEQ, skipLabel);
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                                mv.visitLabel(skipLabel);
                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                                mv.visitInsn(Opcodes.POP);
                                mv.visitInsn(Opcodes.POP);
                                mv.visitInsn(Opcodes.POP);
                                mv.visitLabel(endLabel);
                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            } else {
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            }
                        }
                    };
                }
                return mv;
            }
        };
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
}
