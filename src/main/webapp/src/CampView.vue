<script setup lang="ts">
import type { Camp } from "@/game.ts";
import Card from "@/card/CardView.vue";
import ResourceBankView from "@/ResourceBankView.vue";

defineProps<Camp>()
</script>

<template>
  <div class="camp-layout">
    <div class="stats">
      <h3>Nombre de constructions: {{buildings.length}} / {{maxBuildCount}}</h3>
      <div class="row">
        <h3>Production totale : </h3>
        <ResourceBankView :bank="production"/>
      </div>
    </div>
    <div class="buildings">
      <h3>Bâtiments</h3>
      <div class="row">
        <Card v-for="card in buildings" v-bind="card"/>
      </div>
    </div>
    <div class="searches">
      <h3>Recherches</h3>
      <p v-if="searches.length === 0">Aucune</p>
      <div class="row">
        <Card v-for="card in searches" v-bind="card"/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
