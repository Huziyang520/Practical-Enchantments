// 把 #RRGGBB 转成原版尘粒参数（1.20.1 的构造器吃 Vector3f）。

package com.practicalenchantments.support;

import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

public final class Particles {
	private Particles() {
	}

	public static DustParticleOptions dust(int color, float scale) {
		float red = ((color >> 16) & 0xFF) / 255.0F;
		float green = ((color >> 8) & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		if (red == 0.0F && green == 0.0F && blue == 0.0F) {
			red = 0.01F;
		}
		return new DustParticleOptions(new Vector3f(red, green, blue), scale);
	}
}
