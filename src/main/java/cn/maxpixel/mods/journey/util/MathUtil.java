package cn.maxpixel.mods.journey.util;

import net.minecraft.core.Vec3i;

public class MathUtil {
    public static final Vec3i ONE = new Vec3i(1, 1, 1);

    public static short sclamp(short val, short min, short max) {
        return val < min ? min : (val > max ? max : val);
    }

    public static int max3(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }
}