<script setup lang="ts">

import { ICON_RESOURCES } from "@/assets/icon.ts";
import { Icon } from "@iconify/vue";
import type { Resource } from "@/game/ResourceBank.ts";
import { computed } from "vue";

const {count = 1, color, negativeColor = 'red'} = defineProps<{
  resource: Resource
  count?: number
  color?: string
  negativeColor?: string
}>()
const iconColor = computed(() => count < 0 ? negativeColor : color)
</script>

<template>
  <div class="row resource-view">
    <Icon
      :style="iconColor ? {'--color': iconColor} : undefined"
      :icon="ICON_RESOURCES[resource]"
      inline
    />
    <span
      v-if="Math.abs(count)!=1"
      v-text="`x${Math.abs(count)}`"
    />
  </div>
</template>

<style scoped>
span {
  font-weight: bold;
}
</style>
