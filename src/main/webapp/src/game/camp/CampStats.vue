<script setup lang="ts">
import type { Camp } from "@/game/game.ts";
import ResourceBankView from "@/game/ResourceBankView.vue";

defineProps<{camp: Camp}>()
</script>

<template>
<div>
  <h3>Nombre de constructions: {{camp.buildings.length}} / {{camp.maxBuildCount}}</h3>
  <div class="row">
    <h3>Production totale : </h3>
    <ResourceBankView :bank="camp.production"/>
  </div>
</div>
</template>

<style scoped>

</style>
