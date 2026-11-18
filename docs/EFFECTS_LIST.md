# Effect List

## At a glance

| Effect | id | Kind | Lasts | What it changes |
|---|---|---|---|---|
| Wrath | `wrath` | beneficial | 45 s · 75 s with redstone | +10% movement speed, +20% attack damage |
| Bleed | `bleed` | harmful | 5 s per stack | Damage over time that ignores armour, up to 5 stacks |
| Rot | `rot` | harmful | 25 s per stack | Damage over time that also wears armour down |

## Details


### Bleed (`practical_enchantments:bleed`)

- **Kind:** harmful, no amplifier — stacks are tracked separately from the effect itself
- **Stacks:** up to 5. Each stack deals **0.02 damage per tick** and lasts **5 seconds (2 damage)**.
- **Armour:** ignored. Bleed is a damage type that bypasses armour, so it neither is reduced nor wears gear down.
- **Blood burst:** the moment a 5th stack lands, the remaining damage of the five oldest stacks is dealt at
  once, and those five stacks are removed. A sixth stack survives the burst, so 3 stacks + 3 more stacks
  goes 3 → 4 → 5 (burst) → 1.
- **Particles:** red dust while bleeding, a larger dark-red dust burst on the blood burst.
- **Icon colour:** dark red
- **In-game name:** Bleed
- **Potion:** none

### Rot (`practical_enchantments:rot`)

- **Kind:** harmful, no amplifier — stacks are tracked separately from the effect itself
- **Stacks:** unlimited. Each stack deals **0.25 damage every 0.6 seconds** and lasts **25 seconds**.
- **Armour:** damage is reduced by armour as usual, and every damage tick also wears armour down. On top
  of vanilla's own point, a stack count of *n* adds ⌊n / 10⌋ points, plus one more point with a chance of
  (n mod 10) / 10.
- **Floor:** damage stops once the target is down to 1 health, but armour keeps wearing down.
- **Particles:** green dust on every damage tick.
- **Icon colour:** poison green
- **In-game name:** Rot
- **Potion:** none
### Wrath (`practical_enchantments:wrath`)

- **Kind:** beneficial
- **Lasts:** 45 seconds from a potion, 75 seconds from the extended brewing
- **Effect:** movement speed **+10%** and attack damage **+20%**. Both are multipliers on the final value,
  so they stack with other speed and damage bonuses.
- **Icon colour:** rust red
- **In-game name:** Wrath

## Potions

| Item | id | What it is |
|---|---|---|
| Potion of Wrath | `practical_enchantments:wrath` | Wrath for 45 seconds |
| Potion of Wrath (extended) | `practical_enchantments:long_wrath` | Wrath for 75 seconds |
| Splash Potion of Wrath | same potions, splash form | Applies Wrath to everything caught in the cloud |
| Lingering Potion of Wrath | same potions, lingering form | Leaves a cloud that keeps applying Wrath |
| Arrow of Wrath | same potions, arrow form | Applies Wrath where it lands |

### Brewing

| Base | Ingredient | Result |
|---|---|---|
| Awkward Potion | Magma Block | Potion of Wrath |
| Potion of Wrath | Redstone Dust | Potion of Wrath, 75 seconds |

Gunpowder turns either potion into its splash form and Dragon's Breath turns the splash form into the
lingering one.