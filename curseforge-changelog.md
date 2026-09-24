### 1.2.3 (2026-09-24)

- Fix Bright: the enchantment no longer overwrites or deletes night vision the player obtained from other sources (potions, `/effect give`, beacons, other mods). It now only refreshes and revokes the night vision instance it granted itself.
- Bright now grants night vision with a finite duration (20 s, refreshed once per second) instead of an infinite one, so the effect always expires cleanly and can never get stuck on the player.
- Fix Gentle Descent: same class of bug - slow falling from potions, commands or other mods is no longer overwritten or removed. Only the instance granted by the enchantment itself is refreshed while worn and revoked when sneaking or taking the boots off.
- Gentle Descent also switched to a finite, periodically refreshed duration for the same reason.
