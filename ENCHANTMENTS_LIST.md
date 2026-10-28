# Enchantment List

## At a glance

| Enchantment | id | Applies to | Levels | Obtained by | Conflicts with |
|---|---|---|---|---|---|
| Aerial Haste | `aerial_haste` | Helmets | single | table · trade · loot | — |
| Beheading | `beheading` | Swords, axes | Ⅰ–Ⅲ | trade · loot | — |
| Bright | `bright` | Helmets | single | table · trade · loot | — |
| Cat Attraction | `cat_attraction` | Leggings | single | trade · loot | — |
| Cat Charm | `cat_charm` | Helmets | single | trade · loot | — |
| Chinese | `chinese` | Chestplates | single | loot | — |
| Demonic Pact | `demonic_pact` | Chestplates | Ⅰ–Ⅲ | loot | Undying Drop |
| Destruction | `destruction` | Mining tools | single | trade · loot | Fortune, Silk Touch |
| Disguise | `disguise` | Heads (7 kinds) | single | trade · loot | — |
| Execute | `execute` | Swords, axes | single | not obtainable | — |
| Frenzy | `frenzy` | Swords, axes | Ⅰ–Ⅱ | trade · loot | — |
| Gentle Descent | `gentle_descent` | Boots | single | not obtainable | Feather Falling |
| Incinerate | `incinerate` | Swords, axes | single | trade · loot | Looting |
| Life Steal | `life_steal` | Swords, axes | Ⅰ–Ⅴ | table · trade · loot | — |
| Lumberjack | `lumberjack` | Axes | single | table · trade · loot | Fortune, Silk Touch |
| Penetration | `penetration` | Tridents | Ⅰ–Ⅴ | table · loot | Riptide |
| Powerful | `powerful` | Crossbows | Ⅰ–Ⅴ | table · trade · loot | Multishot |
| Smashing | `smashing` | Tridents | Ⅰ–Ⅴ | table · loot | — |
| Soul Siphon | `soul_siphon` | Swords, axes | Ⅰ–Ⅴ | table · trade · loot | — |
| Undying Drop | `undying_drop` | Armour (any piece) | Ⅰ–Ⅲ | not obtainable | Demonic Pact |
| Venom | `venom` | Swords | Ⅰ–Ⅱ | table · trade · loot | Wither Aspect, Fire Aspect |
| Wither Aspect | `wither_aspect` | Swords | Ⅰ–Ⅱ | trade · loot | Venom, Fire Aspect |
| Ember Quench | `tf_quench` | Tools | single | Twilight Forest loot | — |
| Twilight Slayer | `tf_night_slayer` | Swords, axes | Ⅰ–Ⅴ | Twilight Forest loot | — |
| Godwood Aura | `aether_godwood` | Weapons, tools | single | Aether loot | — |
| Sky Slayer | `aether_sky_slayer` | Bows | Ⅰ–Ⅴ | Aether loot | — |
| Provocation | `provocation` | Swords, axes | Ⅰ–Ⅲ | trade · loot | — |
| Heavy Blow | `heavy_blow` | Swords, axes | single | trade · loot | Sweeping Edge |
| Invisible Air | `invisible_air` | Swords | single | mob drop | — |
| Obsidian Walker | `obsidian_walker` | Boots | single | End city, bastion treasure | Frost Walker, Depth Strider |

## Details

### Aerial Haste (`practical_enchantments:aerial_haste`)

- **Applies to:** helmets · **Levels:** single
- **Obtained by:** enchanting table, librarian trades, ruined portal 30%, shipwreck treasure 20%, end city 10%, ancient city 8%
- **Effect:** removes the airborne mining penalty — mining in mid-air is as fast as mining on the ground. Underwater mining is not affected.
- **In-game description:** Removes the mining speed penalty while airborne

### Beheading (`practical_enchantments:beheading`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** librarian trades, woodland mansion 12%, pillager outpost 8%
- **Effect:** killed mobs are more likely to drop their head: **+2.5 % per level**. Only mobs that own a head item are affected.
- **In-game description:** Increases the chance for mobs to drop their heads

### Bright (`practical_enchantments:bright`)

- **Applies to:** helmets · **Levels:** single
- **Obtained by:** enchanting table, librarian trades, armorer trade (18 emeralds for an enchanted diamond helmet), abandoned mineshaft 10%, dungeon 8%, shipwreck treasure 7%, igloo 6%
- **Effect:** permanent Night Vision while worn. The helmet loses one point of durability every 13.75 seconds during the night and stops doing so during the day; taking the helmet off ends the effect at once.
- **In-game description:** Grants permanent Night Vision

### Cat Attraction (`practical_enchantments:cat_attraction`)

- **Applies to:** leggings · **Levels:** single
- **Obtained by:** librarian trades, igloo 10%, shipwreck supply 10%, dungeon 6%, abandoned mineshaft 6%
- **Effect:** cats and ocelots no longer run away from you — they can be fed and tamed normally.
- **In-game description:** The 'cats' have lowered their guard around you

### Cat Charm (`practical_enchantments:cat_charm`)

- **Applies to:** helmets · **Levels:** single
- **Obtained by:** librarian trades, jungle temple 10%, shipwreck treasure 10%, desert pyramid 8%, dungeon 6%
- **Effect:** creepers and phantoms keep their distance and actively move away from you.
- **In-game description:** Like a little cat staying by your side~

### Chinese (`practical_enchantments:chinese`)

- **Applies to:** chestplates · **Levels:** single
- **Obtained by:** ancient city 10% (nothing else)
- **Effect:** unlocks creative flight while the chestplate is worn. Nothing else from creative mode is unlocked, and the flight ends the moment the chestplate comes off. Durability is spent three times as fast while you are hit in mid-air.
- **In-game description:** https://youtu.be/9pGHMwTDgaA?si=klCUKUcBkWB3kGA1

### Demonic Pact (`practical_enchantments:demonic_pact`)

- **Applies to:** chestplates · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** ancient city 14%, buried treasure 10%
- **Conflicts with:** Undying Drop
- **Effect:** on lethal damage, emeralds are taken from your inventory to keep you alive, exactly like a totem of undying, and you are given Darkness.
  The price is **64 − 8 × (level − 1)** emeralds (Ⅰ 64, Ⅱ 56, Ⅲ 48) — never less than 16 —
  the Darkness lasts **20 − 4 × (level − 1)** seconds, and the enchantment then goes on a **60 second
  cooldown**. Too few emeralds means no rescue and a normal death.
- **In-game description:** Pays emeralds to a demon to avoid death, but with a corresponding punishment...

### Destruction (`practical_enchantments:destruction`)

- **Applies to:** mining tools · **Levels:** single
- **Obtained by:** librarian trades, nether fortress 20%, woodland mansion 15%, bastion remnant 10%, pillager outpost 10%
- **Conflicts with:** Fortune, Silk Touch
- **Effect:** blocks you mine drop no items; you get experience instead — 2 points per block, 5 for ores. Only blocks the tool could actually mine are affected.
- **In-game description:** Destroys mined blocks and produces experience

### Disguise (`practical_enchantments:disguise`)

- **Applies to:** skeleton, wither skeleton, zombie, creeper, piglin, dragon and player heads · **Levels:** single
- **Obtained by:** librarian trades (24 emeralds for the book), woodland mansion 12%, bastion treasure 10%, stronghold library 10%, end city 8%, pillager outpost 8%
- **Effect:** wearing a head stops the matching mobs from attacking you on their own. A piglin head also covers piglin brutes; a wither skeleton head does **not** stop the wither. Attacking a mob still provokes it — the disguise hides you, it does not make you immune.
- **In-game description:** Mobs corresponding to the head no longer attack you on their own

### Execute (`practical_enchantments:execute`)

- **Applies to:** swords and axes · **Levels:** single
- **Obtained by:** ancient city 20%, end city 15%, woodland mansion 10%
- **Effect:** extra damage against targets that are already hurt. Between 10 % and 35 % health the
  damage is **×1.5**, below 10 % it is **×3** — and **×2 / ×4** when the weapon is an axe. Above 35 %
  nothing changes. The multiplier applies to the final damage, so it stacks with Sharpness.
- **In-game description:** Deals more damage to low-health targets

### Frenzy (`practical_enchantments:frenzy`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅱ
- **Obtained by:** librarian trades, woodland mansion 18%, pillager outpost 14%
- **Effect:** killing something grants Speed — Ⅱ for 6 seconds at level Ⅰ, Ⅲ for 8 seconds at level Ⅱ.
  On an axe the duration is doubled. Each kill restarts the timer.
- **In-game description:** Grants a Speed effect after killing an enemy

### Gentle Descent (`practical_enchantments:gentle_descent`)

- **Applies to:** boots · **Levels:** single
- **Obtained by:** not obtainable in survival (creative / commands)
- **Conflicts with:** Feather Falling
- **Effect:** permanent Slow Falling while worn, with the fall damage that comes with it. Sneaking
  switches it off, so a quick descent is one key away.
- **In-game description:** Grants permanent Slow Falling, canceled by sneaking

### Incinerate (`practical_enchantments:incinerate`)

- **Applies to:** swords and axes · **Levels:** single
- **Obtained by:** librarian trades, jungle temple 35%, desert pyramid 20%, large underwater ruin 20%, woodland mansion 10%
- **Conflicts with:** Looting
- **Effect:** mobs you kill drop nothing and give **double experience**. Players are not affected.
- **In-game description:** Completely destroys mobs, dropping more experience

### Life Steal (`practical_enchantments:life_steal`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, librarian trades, stronghold library 18%
- **Effect:** every hit heals you for **4 % × level** of the health the target *actually lost* — hitting
  a 2-health mob with a 6-damage weapon heals as if you had dealt 2.
- **In-game description:** Restores a small amount of health based on the target's actual health lost after hitting

### Lumberjack (`practical_enchantments:lumberjack`)

- **Applies to:** axes · **Levels:** single
- **Obtained by:** enchanting table, librarian trades, village toolsmith 8%, abandoned mineshaft 6%, pillager outpost 5%, dungeon 4%
- **Conflicts with:** Fortune, Silk Touch
- **Effect:** felling one log breaks the whole connected tree — logs and leaves — up to **64 blocks**
  per swing. Leaves drop saplings and apples at vanilla rates. With Destruction on the same axe the
  tree drops nothing and pays experience instead. Durability is spent per block broken.
- **In-game description:** Breaks the entire tree

### Penetration (`practical_enchantments:penetration`)

- **Applies to:** tridents · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, shipwreck treasure 22%
- **Conflicts with:** Riptide
- **Effect:** a thrown trident deals **1.5 + 0.5 × (level − 1)** extra damage. With Loyalty as well,
  the creature the trident hit is dragged back to you; the ender dragon and the wither are immune.
- **In-game description:** Thrown trident attacks deal more damage

### Powerful (`practical_enchantments:powerful`)

- **Applies to:** crossbows · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, librarian trades, pillager outpost 12%, village fletcher 8%, dungeon 6%, bastion remnant 5%
- **Conflicts with:** Multishot
- **Effect:** crossbow bolts deal **+25 % damage per level**, following the same curve as vanilla
  Power. Bows are not affected.
- **In-game description:** Increases crossbow damage

### Smashing (`practical_enchantments:smashing`)

- **Applies to:** tridents · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, large underwater ruin 20%, shipwreck treasure 14%, abandoned mineshaft 12%
- **Effect:** a thrown trident breaks the block it hits. The level decides which pickaxe it counts as —
  Ⅰ wood, Ⅱ stone, Ⅲ iron, Ⅳ diamond, Ⅴ netherite — and blocks above that tier are not broken.
  Unbreakable blocks such as bedrock are never touched. With
  Penetration **and** Loyalty the drops fly back to you.
- **In-game description:** Thrown trident attacks can break blocks; higher levels break harder blocks

### Soul Siphon (`practical_enchantments:soul_siphon`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, librarian trades, stronghold library 24%
- **Effect:** each hit produces experience orbs worth **damage × 0.2 × level**, capped at
  **2 × level** per hit. The base is the damage before armour reduction.
- **In-game description:** Converts damage about to be dealt into experience

### Undying Drop (`practical_enchantments:undying_drop`)

- **Applies to:** any armour piece · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** not obtainable in survival (creative / commands)
- **Conflicts with:** Demonic Pact
- **Effect:** on lethal damage the totem of undying save triggers, but **every item in your inventory
  and armour slots is dropped on the ground and the enchanted armour piece is destroyed**. Darkness
  lasts **20 − 4 × (level − 1)** seconds.
- **In-game description:** Trades dropping all inventory items for one chance to escape death

### Venom (`practical_enchantments:venom`)

- **Applies to:** swords · **Levels:** Ⅰ–Ⅱ
- **Obtained by:** enchanting table, librarian trades, jungle temple 28%, shipwreck treasure 16%
- **Conflicts with:** Wither Aspect, Fire Aspect
- **Effect:** hits poison the target — level Ⅰ for 6 seconds, level Ⅱ for 12. Undead are immune, as with
  every poison in the game.
- **In-game description:** Poisons the target

### Wither Aspect (`practical_enchantments:wither_aspect`)

- **Applies to:** swords · **Levels:** Ⅰ–Ⅱ
- **Obtained by:** librarian trades, nether fortress 22%, bastion remnant 15%, ancient city 12%
- **Conflicts with:** Venom, Fire Aspect
- **Effect:** hits wither the target — 6 seconds at level Ⅰ, 12 at level Ⅱ — at the cost of weapon
  damage: **−1** at level Ⅰ and **−0.5** at level Ⅱ. Wither hurts everything, undead included, and can
  kill.
- **In-game description:** Inflicts Wither on the target

### Ember Quench (`practical_enchantments:tf_quench`)

- **Applies to:** tools · **Levels:** single
- **Obtained by:** Twilight Forest loot only
- **Effect:** blocks you break drop what a furnace would give for them.
- **In-game description:** Automatically smelts

### Twilight Slayer (`practical_enchantments:tf_night_slayer`)

- **Applies to:** swords, axes · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** Twilight Forest loot only
- **Effect:** in a dimension whose clock says night, hits deal **2 + 1.5 × (level − 1)** more damage.
- **In-game description:** Deals more damage in the dark

### Godwood Aura (`practical_enchantments:aether_godwood`)

- **Applies to:** weapons, tools · **Levels:** single
- **Obtained by:** Aether loot only; not offered by the enchanting table and not sold by librarians
- **Effect:** kills drop twice the loot; breaking a block has a chance to drop **1–2** extra God Shards.
- **In-game description:** A mysterious power from the Aether: kills drop twice the loot, and breaking blocks may drop extra God Shards

### Sky Slayer (`practical_enchantments:aether_sky_slayer`)

- **Applies to:** bows · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** Aether loot only
- **Effect:** hits on airborne targets deal **1 + level** more damage and slow them for 5 seconds — level Ⅰ slows nothing, level Ⅴ slows Ⅳ.
- **In-game description:** Deals more damage to airborne targets

### Provocation (`practical_enchantments:provocation`)

- **Applies to:** swords, axes · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** librarian trades, chest loot
- **Effect:** a hit on a target that is not angered yet angers it for 10 seconds and deals only **10%** of
  the damage; hits on an angered target deal **× (1.15 + 0.20 × level)** the damage — 135% at Ⅰ, 155% at Ⅱ,
  175% at Ⅲ.
- **In-game description:** Angers the target; angered targets take much more damage

### Heavy Blow (`practical_enchantments:heavy_blow`)

- **Applies to:** swords, axes · **Levels:** single
- **Obtained by:** librarian trades, chest loot · **Conflicts with:** Sweeping Edge
- **Effect:** every swing lands a critical hit. A swing that connects shortens the attack cooldown to
  **90%**; one that misses lengthens it to **160%**. Every landing hit costs one extra point of durability.
- **In-game description:** Swing with full force and always land a critical hit

### Invisible Air (`practical_enchantments:invisible_air`)

- **Applies to:** swords · **Levels:** single
- **Obtained by:** carried by mobs that spawn with it — it drops with the sword they hold
- **Effect:** layers of wind bend light around the blade, so it disappears from the hand while the item
  itself stays visible in the inventory. Hits deal **twice** the damage, sweeping attacks deal twice as
  much, and entity interaction range grows by **50%**.
- **In-game description:** Layers of wind bend light around the blade, hiding it while it strikes twice as hard

### Obsidian Walker (`practical_enchantments:obsidian_walker`)

- **Applies to:** boots · **Levels:** single
- **Obtained by:** end city treasure, bastion treasure
- **Effect:** lava becomes walkable. Each step leaves **flawed obsidian** behind, which cracks through
  three stages roughly a second apart and then melts back into lava; breaking it yourself turns it into
  lava as well.
- **In-game description:** Lets you walk across lava, leaving flawed obsidian behind

#### Flawed Obsidian

| Item | id | What it is |
|---|---|---|
| **Flawed Obsidian** | `practical_enchantments:flawed_obsidian` | The block Obsidian Walker leaves on lava. It cracks through three stages about a second apart and then melts back into lava, and mining it turns it into lava too. |

## Order

New enchantments go at the end of the table and at the end of the details, in this file and in
`ENCHANTMENTS_LIST_CN.md` alike, with the same row order in both. Entries are appended, never inserted
in the middle, so the order stays stable and a diff shows only what was added.
