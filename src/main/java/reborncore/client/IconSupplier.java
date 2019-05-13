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

package reborncore.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

//TODO move to fabric api
public class IconSupplier {
	public static String armour_head_name = "reborncore:gui/slot_sprites/armour_head";
	@Environment(EnvType.CLIENT)
	public static Sprite armour_head;

	public static String armour_chest_name = "reborncore:gui/slot_sprites/armour_chest";
	@Environment(EnvType.CLIENT)
	public static Sprite armour_chest;

	public static String armour_legs_name = "reborncore:gui/slot_sprites/armour_legs";
	@Environment(EnvType.CLIENT)
	public static Sprite armour_legs;

	public static String armour_feet_name = "reborncore:gui/slot_sprites/armour_feet";
	@Environment(EnvType.CLIENT)
	public static Sprite armour_feet;

//	@Environment(EnvType.CLIENT)
//	@SubscribeEvent
//	public void preTextureStitch(TextureStitchEvent.Pre event) {
//		SpriteAtlasTexture map = event.getMap();
//		armour_head = map.getSprite(new Identifier(armour_head_name));
//		armour_chest = map.getSprite(new Identifier(armour_chest_name));
//		armour_legs = map.getSprite(new Identifier(armour_legs_name));
//		armour_feet = map.getSprite(new Identifier(armour_feet_name));
//	}

}
