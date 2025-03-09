<script setup lang="ts">

import BuildingView from "@/game/card/BuildingView.vue";
import type { UpgradableBuilding } from "@/game/game.ts";
import CardName from "@/game/card/CardName.vue";
import CardStat from "@/game/card/CardStat.vue";
import { ICON_ACTION, ICON_POWER_GENERATOR } from "@/assets/icon.ts";
import { Icon } from "@iconify/vue";

defineProps<UpgradableBuilding>()
</script>

<template>
  <div class="col" style="gap: 0">
    <BuildingView v-bind="building.value" :style="{'z-index': upgrades.length}"/>
    <div
      v-for="({value:{name, production, isPowerGenerator}}, i) in upgrades"
      class="card-bottom"
      :style="{ top: `-${(0.5*(i+1))}rem`, 'z-index': upgrades.length - i - 1 }"
    >
      <div class="row" style="justify-content: space-between;">
        <CardName :name/>
        <Icon v-if="isPowerGenerator" :icon="ICON_POWER_GENERATOR" style="--color: lightseagreen"/>
      </div>
      <CardStat
        v-if="production && Object.keys(production).length >= 1"
        :icon="ICON_ACTION['UpgradeBuilding']"
        :bank="production"
      />
    </div>
  </div>
</template>

<style scoped>
.card-bottom {
  border-radius: 0 0 0.5rem 0.5rem;
  border: 0.2rem solid var(--vt-c-indigo);
  border-top: none;
  background-color: white;
  position: relative;
  bottom: 0;
  padding: 0.5rem;
  padding-top: 1rem;
}

</style>
