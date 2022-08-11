var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var AbstractInsnNode = Java.type('org.objectweb.asm.tree.AbstractInsnNode')
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')

function initializeCoreMod() {
    return {
        'EntityGetterTransformer': {
            'target': {
                'type': 'METHOD',
                'class': 'net/minecraft/world/level/EntityGetter',
                'methodName': 'getEntityCollisions',
                'methodDesc': '(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;'
            },
            'transformer': function(methodNode) {
                var targetLabel = null
                for (var it = methodNode.instructions.iterator(); it.hasNext(); ) {
                    var instruction = it.next();
                    if (targetLabel == null && instruction.getOpcode() === Opcodes.ASTORE && instruction.var === 6) {
                        targetLabel = it.next()
                        continue
                    }
                    if (instruction.getOpcode() === Opcodes.ASTORE && instruction.var === 7) {
                        var toAdd = ASMAPI.listOf(
                            new LabelNode(),
                            new VarInsnNode(Opcodes.ALOAD, 1),
                            new VarInsnNode(Opcodes.ALOAD, 2),
                            new VarInsnNode(Opcodes.ALOAD, 5),
                            new VarInsnNode(Opcodes.ALOAD, 7),
                            ASMAPI.buildMethodCall('cn/maxpixel/mods/journey/coremod/CoremodEntityGetter', 'addStructureEntityCollisions', '(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Lcom/google/common/collect/ImmutableList$Builder;Lnet/minecraft/world/entity/Entity;)Z', ASMAPI.MethodType.STATIC),
                            new JumpInsnNode(Opcodes.IFNE, targetLabel)
                        )
                        methodNode.instructions.insert(instruction, toAdd)
                        break;
                    }
                }
                return methodNode
            }
        }
    }
}