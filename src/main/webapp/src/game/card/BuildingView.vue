<script setup lang="ts">
import type { Building } from "@/game/card/Card.ts";
import ResourceBankView from "@/game/ResourceBankView.vue";
import { Icon } from "@iconify/vue";
import { ICON_ACTION_CONSTRUCT, ICON_ACTION_SEARCH } from "@/icon.ts";

defineProps<Building>()
</script>

<template>
  <div class="building-layout col">
    <div class="cost row">
      <Icon :icon="ICON_ACTION_CONSTRUCT" color="red" width="2rem" height="2rem"/>
      <ResourceBankView :bank="cost" opposite/>
    </div>
    <div class="production row">
      <Icon :icon="ICON_ACTION_CONSTRUCT" width="2rem" height="2rem"/>
      <ResourceBankView :bank="production"/>
    </div>
    <div class="search row">
      <Icon :icon="ICON_ACTION_SEARCH" width="2rem" height="2rem"/>
      <ResourceBankView :bank="search"/>
    </div>
  </div>
</template>

<style scoped>
.building-layout {
  gap: 0.5rem;
}
.building-layout > div {
  gap: 0;
}
.building-layout > div > *:first-child {
  border-right: dashed;
}
</style>
