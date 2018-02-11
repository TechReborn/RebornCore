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

package reborncore.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;

/**
 * Class stolen from SteamAgeRevolution, which I stole from BloodMagic, which was stolen from EnderCore, which stole the
 * idea from ExtraUtilities, who stole it from vanilla.
 *
 * Original class link:
 * https://github.com/SleepyTrousers/EnderCore/blob/master/src/main/java/com/enderio/core/common/util/ChatUtil.java
 */

public class ChatUtils {
	private static final int DELETION_ID = 1337; //MAKE THIS UNIQUE PER MOD THAT USES THIS

	public static void sendNoSpamMessages(int messageID, ITextComponent message) {
		int deleteID = DELETION_ID + messageID;
		GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		chat.printChatMessageWithOptionalDeletion(message, deleteID);
	}
}
