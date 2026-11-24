// 缴械：命中必给慌乱，概率把主手或随机装备槽的一件物品打落在地。

package com.practicalenchantments.enchantment.weapon;

import com.huziyang520.merlinlib.event.BuiltInEvents;
import com.huziyang520.merlinlib.event.EnchantmentContext;
import com.huziyang520.merlinlib.event.EnchantmentEventRegistrar;
import com.practicalenchantments.PracticalEnchantments;
import com.practicalenchantments.advancement.PeAdvancementTriggers;
import com.practicalenchantments.config.Tunables;
import com.practicalenchantments.content.PeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class DisarmEnchantment {
	public static final String ID = PracticalEnchantments.MOD_ID + ":disarm";
	private static final EquipmentSlot[] ARMOR = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static void registerCallbacks(EnchantmentEventRegistrar registrar, HolderLookup.Provider registries) {
		Holder<Enchantment> holder = PracticalEnchantments.resolveEnchantment(registries, ID);
		registrar.register(holder, BuiltInEvents.POST_ATTACK, DisarmEnchantment::onAttack);
	}

	private static void onAttack(BuiltInEvents.PostAttackEvent event, EnchantmentContext ctx) {
		LivingEntity target = event.target();
		if (target.isDeadOrDying()) {
			return;
		}
		int enchantLevel = ctx.level();
		BuiltInRegistries.MOB_EFFECT.get(PeEffects.PANIC).ifPresent(holder -> target.addEffect(
				new MobEffectInstance(holder, Tunables.secondsToTicks(Tunables.disarmPanicSeconds(enchantLevel)),
						0, false, true)));
		if (target instanceof ServerPlayer player && player.isCreative()) {
			return;
		}
		double chance = Tunables.disarmDropChancePercent(enchantLevel);
		if (event.attacker().getRandom().nextDouble() * 100.0D >= chance) {
			return;
		}
		if (Tunables.disarmBossImmune() && isBoss(target)) {
			return;
		}
		boolean mainhand = event.attacker().getRandom().nextDouble() * 100.0D
				< Tunables.disarmMainhandChancePercent();
		ItemStack dropped = mainhand ? takeMainhand(target) : takeRandom(target, event.attacker().getRandom());
		if (dropped.isEmpty()) {
			dropped = takeRandom(target, event.attacker().getRandom());
		}
		if (dropped.isEmpty()) {
			return;
		}
		ServerLevel serverLevel = event.level();
		ItemEntity entity = new ItemEntity(serverLevel, target.getX(), target.getY() + 0.6D, target.getZ(), dropped.copy());
		entity.setPickUpDelay(10);
		serverLevel.addFreshEntity(entity);
		if (event.attacker() instanceof ServerPlayer player) {
			PeAdvancementTriggers.disarmDrop(player);
		}
	}

	private static ItemStack takeMainhand(LivingEntity target) {
		if (target instanceof Player player) {
			Inventory inventory = player.getInventory();
			int selected = inventory.getSelectedSlot();
			ItemStack stack = inventory.getItem(selected);
			if (!stack.isEmpty()) {
				inventory.setItem(selected, ItemStack.EMPTY);
				return stack;
			}
			return ItemStack.EMPTY;
		}
		ItemStack stack = target.getMainHandItem();
		if (!stack.isEmpty()) {
			target.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	private static ItemStack takeRandom(LivingEntity target, net.minecraft.util.RandomSource random) {
		if (target instanceof Player player) {
			Inventory inventory = player.getInventory();
			int size = inventory.getContainerSize();
			int start = random.nextInt(size);
			for (int offset = 0; offset < size; offset++) {
				int index = (start + offset) % size;
				ItemStack stack = inventory.getItem(index);
				if (!stack.isEmpty()) {
					inventory.setItem(index, ItemStack.EMPTY);
					return stack;
				}
			}
			return ItemStack.EMPTY;
		}
		int start = random.nextInt(ARMOR.length + 2);
		for (int offset = 0; offset < ARMOR.length + 2; offset++) {
			int index = (start + offset) % (ARMOR.length + 2);
			EquipmentSlot slot = index == ARMOR.length ? EquipmentSlot.MAINHAND
					: index == ARMOR.length + 1 ? EquipmentSlot.OFFHAND : ARMOR[index];
			ItemStack stack = target.getItemBySlot(slot);
			if (!stack.isEmpty()) {
				target.setItemSlot(slot, ItemStack.EMPTY);
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private static boolean isBoss(LivingEntity target) {
		return target instanceof WitherBoss || target instanceof EnderDragon || target instanceof Warden;
	}
}
