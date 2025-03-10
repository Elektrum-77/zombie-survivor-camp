<script setup lang="ts">
import type { ResourceBank } from "@/game/ResourceBank.ts";
import ResourceView from "@/game/ResourceView.vue";

defineProps<{
  opposite?: boolean
  bank: ResourceBank
  color?: string
  negativeColor?: string
}>()
</script>

<template>
  <div class="row">
    <div v-for="(v,r) in bank" class="row">
      <ResourceView v-if="v && v!=0" :resource="r" :count="opposite ? -v : v" :color :negativeColor />
    </div>
  </div>
</template>

<style scoped>
</style>
