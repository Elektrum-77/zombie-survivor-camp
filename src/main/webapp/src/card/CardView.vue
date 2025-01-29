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
    <span class="title" v-text="value.name"/>
    <span/>
    <component :is="contentViews[type]" v-bind="value"/>
  </div>
</template>

<style scoped>
.title {
  font-weight: bold;
  font-size: 1rem;
}
.card-layout {
  background-color: white;
  padding: 8px;
  display: grid;
  grid-template-rows: 2rem 10rem 7rem;
  width: 15rem;
  border: 1px solid #ddd;
  border-radius: 0.5rem;
}
</style>
