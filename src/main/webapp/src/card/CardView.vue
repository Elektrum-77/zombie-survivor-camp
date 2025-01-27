<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import DummyView from "@/card/DummyView.vue";
import BuildingView from "@/card/BuildingView.vue";

defineProps<Card>()

const contentViews = {
  "Building": BuildingView,
  "Dummy": DummyView,
} as const
</script>

<template>
  <div class="card-layout">
    <span style="font-weight: bold" v-text="value.name"/>
    <span/>
    <component :is="contentViews[type]" v-bind="value"/>
  </div>
</template>

<style scoped>
.card-layout {
  background-color: white;
  padding: 8px;
  display: grid;
  grid-template-rows: 1rem 6rem 1fr;
  min-height: 250px;
  max-width: 180px;
  border: 1px solid #ddd;
  border-radius: 8px;
}
</style>
