<script setup lang="ts">
import type { Camp } from "@/game/game.ts";
import type { Action } from "@/game/action/Action.ts";
import CardViewAction from "@/game/card/CardViewAction.vue";
import CampStats from "@/game/camp/CampStats.vue";
import CardView from "@/game/card/CardView.vue";
import UpgradableBuildingView from "@/game/camp/UpgradableBuildingView.vue";

defineProps<{ camp: Camp }>()
defineEmits<{ action: Action[] }>()
</script>

<template>
  <div class="camp-layout">
    <CampStats :camp/>
    <div>
      <h3>Bâtiments</h3>
      <div class="row">
        <UpgradableBuildingView
          v-for="({building, upgrades}, index) in camp.buildings"
          :building
          :upgrades
          />
<!--        <CardViewAction-->
<!--          v-for="(card, index) in camp.buildings"-->
<!--          :card="card.building"-->
<!--          :actions="[{type: 'DestroyBuilding', value: { index }}]"-->
<!--          @action="$emit('action', $event)"-->
<!--        />-->
      </div>
    </div>
    <div class="searches">
      <h3>Fouilles</h3>
      <p v-if="camp.searches.length === 0">Aucune</p>
      <div class="row">
        <CardViewAction
          v-for="(card, index) in camp.searches"
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
