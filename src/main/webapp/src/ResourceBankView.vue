<script setup lang="ts">
import type { ResourceBank } from "@/ResourceBank.ts";
import ResourceView from "@/ResourceView.vue";

defineProps<{ opposite?: boolean, bank: ResourceBank }>()
</script>

<template>
  <div class="row bank-layout">
    <div v-for="(v,r) in bank" class="row" style="gap: 0">
      <ResourceView v-if="v && v!=0" :resource="r" :count="opposite ? -v : v"/>
    </div>
  </div>
</template>

<style scoped>
.bank-layout, .bank-layout > div {
  gap: 0;
}
</style>
