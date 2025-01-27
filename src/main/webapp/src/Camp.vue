<script setup lang="ts">
import type {Camp} from "@/game.ts";
import Card from "@/Card.vue";

defineProps<Camp>()
</script>

<template>
  <div>
    <h3>Nombre de constructions maximum</h3>
    <p>{{maxBuildCount}}</p>
    <h3>Bâtiments</h3>
    <p v-if="buildings.length === 0">Aucun</p>
    <div class="row">
      <Card v-for="{type, value} in buildings" :type :value />
    </div>
    <h3>Recherches</h3>
    <p v-if="searches.length === 0">Aucune</p>
    <div class="row">
      <Card v-for="{type, value} in searches" :type :value />
    </div>
  </div>
</template>

<style scoped>

</style>
