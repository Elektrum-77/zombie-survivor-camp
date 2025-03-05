<script setup lang="ts">
import type { Upgrade } from "@/game/card/Card.ts";
import ResourceBankView from "@/game/ResourceBankView.vue";
import { ICON_ACTION_CONSTRUCT } from "@/assets/icon.ts";
import { Icon } from "@iconify/vue";

defineProps<Upgrade>()
</script>

<template>
  <div class="upgrade-layout col">
    <div class="row">
      <Icon :icon="ICON_ACTION_CONSTRUCT" color="red" width="2rem" height="2rem"/>
      <ResourceBankView :bank="cost" opposite/>
    </div>
    <div class="row">
      <Icon :icon="ICON_ACTION_CONSTRUCT" width="2rem" height="2rem"/>
      <ResourceBankView :bank="production"/>
    </div>
    <div class="row">
      <Icon v-if="isPowerGenerator" icon="mdi:electricity-generator" width="2rem" height="2rem"/>
    </div>
  </div>
</template>

<style scoped>
.upgrade-layout {
  gap: 0.5rem;
}

.upgrade-layout > div {
  gap: 0;
}

.upgrade-layout > div > *:first-child {
  border-right: dashed;
}

</style>
