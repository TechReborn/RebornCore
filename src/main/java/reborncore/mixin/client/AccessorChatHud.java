package reborncore.mixin.client;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatHud.class)
public interface AccessorChatHud {
	@Invoker("addMessage")
	void invokeAddMessage(Text message, int messageId);
}
