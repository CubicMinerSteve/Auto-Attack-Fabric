package code.cubicminer.autoattack;

import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

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
	public void tick(MinecraftClient Client) 
	{
		if (keyToggleEnabled.wasPressed())
		{
			if (Client.player != null)
			{
				if (ConfigManager.getConfig().enabled)
				{
					ConfigManager.getConfig().enabled = false;
					Client.player.sendMessage(new TranslatableText("message.disabled", MOD_NAME).formatted(Formatting.RED), false);
				}
				else 
				{
					ConfigManager.getConfig().enabled = true;
					Client.player.sendMessage(new TranslatableText("message.enabled", MOD_NAME).formatted(Formatting.GREEN), false);
				}
			}
		}
		if (Client.options.keyAttack.isPressed() && Client.player != null && Client.player.getAttackCooldownProgress(0) >= 1 && ConfigManager.getConfig().enabled == true) 
		{
			if (Client.crosshairTarget != null && Client.crosshairTarget.getType() == HitResult.Type.ENTITY) 
			{
				Entity entity = ((EntityHitResult)Client.crosshairTarget).getEntity();
				if (entity.isAlive() && entity.isAttackable()) 
				{
					Client.interactionManager.attackEntity(Client.player, entity);
				}
			}
		}
		if (Client.player != null)
		{
			if (Client.targetedEntity != null && Client.player != null)
			{
				double Calculated = Client.player.distanceTo(Client.targetedEntity);
				DecimalFormat Precision = new DecimalFormat("0.00");
				String Converted = Precision.format(Calculated);
				if (Calculated <= 1)
					Client.player.sendMessage(new TranslatableText("message.distance.in", Converted).formatted(Formatting.GOLD), true);
				else if (Calculated > 1 && Calculated <= 2)
					Client.player.sendMessage(new TranslatableText("message.distance.in", Converted).formatted(Formatting.YELLOW), true);
				else if (Calculated > 2 && Calculated <= 7)
					Client.player.sendMessage(new TranslatableText("message.distance.in", Converted).formatted(Formatting.GREEN), true);
			}
			else if (Client.player != null)
				Client.player.sendMessage(new TranslatableText("message.distance.out").formatted(Formatting.RED), true);
		}
	}
}
