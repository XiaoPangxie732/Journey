var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')

function initializeCoreMod() {
    return {
        'EntityGetterTransformer': {
            'target': {
                'type': 'METHOD',
                'class': 'net/minecraft/world/level/EntityGetter',
                'methodName': 'm_183134_',
                'methodDesc': '(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;'
            },
            'transformer': function(methodNode) {
                var first = true
                for (var it = methodNode.instructions.iterator(); it.hasNext(); ) {
                    var instruction = it.next();
                    if (instruction.getOpcode() === Opcodes.INVOKESTATIC && instruction.owner === 'java/util/List' &&
                        instruction.name === 'of' && instruction.desc === '()Ljava/util/List;' && instruction.itf) {
                        if (first) {
                            first = false
                            continue
                        }
                        methodNode.instructions.insert(instruction, ASMAPI.listOf(
                            new LabelNode(),
                            new VarInsnNode(Opcodes.ALOAD, 0),
                            new VarInsnNode(Opcodes.ALOAD, 1),
                            new VarInsnNode(Opcodes.ALOAD, 2),
                            ASMAPI.buildMethodCall('cn/maxpixel/mods/journey/coremod/CoremodEntityGetter', 'addStructureEntityCollisions', '(Ljava/util/List;Lnet/minecraft/world/level/EntityGetter;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;', ASMAPI.MethodType.STATIC)
                        ))
                    }
                    if (instruction.getOpcode() === Opcodes.ASTORE && instruction.var === 5) {
                        methodNode.instructions.insert(instruction, ASMAPI.listOf(
                            new LabelNode(),
                            new VarInsnNode(Opcodes.ALOAD, 0),
                            new VarInsnNode(Opcodes.ALOAD, 1),
                            new VarInsnNode(Opcodes.ALOAD, 2),
                            new VarInsnNode(Opcodes.ALOAD, 5),
                            new VarInsnNode(Opcodes.ALOAD, 4),
                            ASMAPI.buildMethodCall('cn/maxpixel/mods/journey/coremod/CoremodEntityGetter', 'addStructureEntityCollisions', '(Lnet/minecraft/world/level/EntityGetter;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Lcom/google/common/collect/ImmutableList$Builder;Ljava/util/List;)V', ASMAPI.MethodType.STATIC)
                        ))
                        break
                    }
                }
                return methodNode
            }
        }
    }
}