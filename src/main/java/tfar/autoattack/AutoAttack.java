package tfar.autoattack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;

import java.util.List;

public class AutoAttack implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickCallback.EVENT.register(mc -> {
			if (mc.options.keyAttack.isPressed() && mc.player != null
							&& mc.player.getAttackCooldownProgress(0) >= 1) {
				if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult)mc.crosshairTarget).getEntity();
					if (entity.isAlive() && entity.isAttackable()) {
						mc.interactionManager.attackEntity(mc.player, entity);
					}
				}
			}
			if (mc.options.advancedItemTooltips && mc.world != null && mc.world.getTime() % 15 == 0) {
				List<Entity> entityList = mc.world.getEntities(mc.player,new Box(mc.player.getBlockPos().add(-6,-6,-6),mc.player.getBlockPos().add(6,6,6)));
				entityList.forEach(entity -> {
					if (entity.isAttackable())
					mc.interactionManager.attackEntity(mc.player, entity);
				});
			}
		});
	}
}
