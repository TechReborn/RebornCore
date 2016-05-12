package reborncore.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Class stolen from SteamAgeRevolution, which I stole from BloodMagic, which was stolen from EnderCore, which stole the
 * idea from ExtraUtilities, who stole it from vanilla.
 *
 * Original class link:
 * https://github.com/SleepyTrousers/EnderCore/blob/master/src/main/java/com/enderio/core/common/util/ChatUtil.java
 */

public class ChatUtils
{
	private static final int DELETION_ID = 2525277;
	private static int lastAdded;

	private static void sendNoSpamMessages(ITextComponent[] messages)
	{
		GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		for (int i = DELETION_ID + messages.length - 1; i <= lastAdded; i++)
		{
			chat.deleteChatLine(i);
		}
		for (int i = 0; i < messages.length; i++)
		{
			chat.printChatMessageWithOptionalDeletion(messages[i], DELETION_ID + i);
		}
		lastAdded = DELETION_ID + messages.length - 1;
	}

	public static ITextComponent wrap(String s)
	{
		return new TextComponentString(s);
	}

	public static ITextComponent[] wrap(String... s)
	{
		ITextComponent[] ret = new ITextComponent[s.length];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = wrap(s[i]);
		}
		return ret;
	}

	public static ITextComponent wrapFormatted(String s, Object... args)
	{
		return new TextComponentTranslation(s, args);
	}

	public static void sendChat(EntityPlayer player, String... lines)
	{
		sendChat(player, wrap(lines));
	}

	public static void sendChat(EntityPlayer player, ITextComponent... lines)
	{
		for (ITextComponent c : lines)
		{
			player.addChatComponentMessage(c);
		}
	}

	public static void sendNoSpamClient(String... lines)
	{
		sendNoSpamClient(wrap(lines));
	}

	public static void sendNoSpamClient(ITextComponent... lines)
	{
		sendNoSpamMessages(lines);
	}
}