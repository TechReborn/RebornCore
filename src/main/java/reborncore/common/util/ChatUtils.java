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

public class ChatUtils
{
	private static final int DELETION_ID = 1337; //MAKE THIS UNIQUE PER MOD THAT USES THIS

	public static void sendNoSpamMessages(int messageID, ITextComponent message)
	{
		int deleteID = DELETION_ID+messageID;
		GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		chat.printChatMessageWithOptionalDeletion(message, deleteID);
	}
}
