package tfar.autoattack;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoAttack implements ClientModInitializer
{
	public static final String MOD_ID = "autoattack";
	public static final String MOD_NAME = "AutoAttack";
	public static final String CONFIG_FILE_NAME = AutoAttack.MOD_ID+".json";
	private KeyBinding keyToggleEnabled;
	
	@Override
	public void onInitializeClient() 
	{
		ConfigManager.init();
		keyToggleEnabled = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.autoattack.enable", 	// The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, 		// The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_KP_ADD,		// The keycode of the key
				"key.category.autoattack" 	// The translation key of the keybinding's category.
		));
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}
	public void tick(MinecraftClient client) 
	{
		if (keyToggleEnabled.wasPressed())
		{
			if (client.player != null)
			{
				if (ConfigManager.getConfig().enabled)
				{
					ConfigManager.getConfig().enabled = false;
					client.player.sendMessage(new TranslatableText("message.disabled", MOD_NAME).formatted(Formatting.RED), true);
				}
				else 
				{
					ConfigManager.getConfig().enabled = true;
					client.player.sendMessage(new TranslatableText("message.enabled", MOD_NAME).formatted(Formatting.GREEN), true);
				}
			}
		}
		if (ConfigManager.getConfig().enabled)
		{
			if (client.options.keyAttack.isPressed() && client.player != null && client.player.getAttackCooldownProgress(0) >= 1) 
			{
				if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) 
				{
					Entity entity = ((EntityHitResult)client.crosshairTarget).getEntity();
					if (entity.isAlive() && entity.isAttackable()) 
					{
						client.interactionManager.attackEntity(client.player, entity);
					}
				}
			}
		}
	}
}