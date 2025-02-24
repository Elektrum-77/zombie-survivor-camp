<script setup lang="ts">
import type { Camp } from "@/game/game.ts";
import ResourceBankView from "@/game/ResourceBankView.vue";
import type { Action } from "@/game/Action.ts";
import CardViewAction from "@/game/card/CardViewAction.vue";

defineProps<Camp>()
defineEmits<{ action: Action[] }>()
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
        <CardViewAction
          v-for="(card, index) in buildings"
          :card
          :actions="[{type: 'DestroyBuilding', value: {index}}]"
          @action="$emit('action', $event)"
        />
      </div>
    </div>
    <div class="searches">
      <h3>Recherches</h3>
      <p v-if="searches.length === 0">Aucune</p>
      <div class="row">
        <CardViewAction
          v-for="(card, index) in searches"
          :card
          :actions="[{type: 'CancelSearch', value: {index}}]"
          @action="$emit('action', $event)"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
