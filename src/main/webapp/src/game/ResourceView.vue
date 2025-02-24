<script setup lang="ts">

import { ICON_RESOURCES } from "@/icon.ts";
import { Icon } from "@iconify/vue";
import type { Resource } from "@/game/ResourceBank.ts";

defineProps<{ resource: Resource, count: number }>()
</script>

<template>
  <div class="row" style="gap: 0">
    <!--
    <Icon
      v-for="_ in Math.abs(v ?? 0)"
      :color="(v??0)<0 ?'red' : undefined"
      :icon="ICON_RESOURCES[r]"
      width="2rem"
      height="2rem"
      inline
    />
    -->
    <Icon
      :color="(count)<0 ?'red' : undefined"
      :icon="ICON_RESOURCES[resource]"
      width="2rem"
      height="2rem"
      inline
    />
    <span
      v-if="Math.abs(count)!=1"
      v-text="`x${Math.abs(count)}`"
    />
  </div>
</template>

<style scoped>

</style>
