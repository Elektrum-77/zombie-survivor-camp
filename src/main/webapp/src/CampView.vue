<script setup lang="ts">
import type { Camp } from "@/game.ts";
import ResourceBankView from "@/ResourceBankView.vue";
import CampCard from "@/CampCard.vue";
import type { Action } from "@/Action.ts";
import CardViewAction from "@/card/CardViewAction.vue";

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
          :index
          :actions="[{type: 'DestroyBuilding', value: {index}}]"
          @action="$emit('action', $event)"
        />
<!--        <CampCard-->
<!--          v-for="(card, i) in buildings"-->
<!--          :card-->
<!--          class="card"-->
<!--          @removed="$emit('action',{type: 'DestroyBuilding', value: {index: i}})"-->
<!--        />-->
      </div>
    </div>
    <div class="searches">
      <h3>Recherches</h3>
      <p v-if="searches.length === 0">Aucune</p>
      <div class="row">
        <CampCard
          v-for="(card, i) in searches"
          :card
          class="card"
          @removed="$emit('action',{type: 'CancelSearch', value: {index: i}})"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
