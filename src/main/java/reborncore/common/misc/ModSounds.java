/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.RebornCore;

/**
 * @author drcrazy
 *
 */

@Mod.EventBusSubscriber(modid = RebornCore.MOD_ID)
public class ModSounds {

	public static SoundEvent BLOCK_DISMANTLE;

	@SubscribeEvent
	public static void onSoundRegistry(Register<SoundEvent> event) {
		BLOCK_DISMANTLE = createSoundEvent("block_dismantle");
		event.getRegistry().register(ModSounds.BLOCK_DISMANTLE);
	}

	private static SoundEvent createSoundEvent(String str) {
		ResourceLocation resourceLocation = new ResourceLocation(RebornCore.MOD_ID, str);
		SoundEvent soundEvent = new SoundEvent(resourceLocation);
		soundEvent.setRegistryName(resourceLocation);
		return soundEvent;
	}
}
