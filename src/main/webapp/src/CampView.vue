<script setup lang="ts">
import type {Camp} from "@/game.ts";
import Card from "@/card/CardView.vue";

defineProps<Camp>()
</script>

<template>
  <div>
    <h3>Nombre de constructions maximum</h3>
    <p>{{maxBuildCount}}</p>
    <h3>Bâtiments</h3>
    <p v-if="buildings.length === 0">Aucun</p>
    <div class="row">
      <Card v-for="card in buildings" v-bind="card" />
    </div>
    <h3>Recherches</h3>
    <p v-if="searches.length === 0">Aucune</p>
    <div class="row">
      <Card v-for="card in searches" v-bind="card" />
    </div>
  </div>
</template>

<style scoped>

</style>
