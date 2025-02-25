<script setup lang="ts">
import type { Card } from "@/game/card/Card.ts";
import DummyView from "@/game/card/DummyView.vue";
import BuildingView from "@/game/card/BuildingView.vue";
import { CARD_IMG } from "@/assets/cards.ts";
import { computed } from "vue";

const {card} = defineProps<{ card: Card }>()

const contentViews = {
  "Building": BuildingView,
  "Dummy": DummyView,
} as const

const imgName = computed<string>(()=>card.value.name.toLowerCase().replaceAll(' ', '_'))
</script>

<template>
  <div class="card-layout">
    <span class="title" v-text="card.value.name"/>
    <img v-if="imgName in CARD_IMG" :src="CARD_IMG[imgName]" alt="no image found"/>
    <div v-else/>
    <component :is="contentViews[card.type]" v-bind="card.value"/>
  </div>
</template>

<style scoped>
.title {
  font-weight: bold;
  font-size: 1rem;
}

img {
  max-width: 100%;
  max-height: 100%;
  border-radius: var(--radius);
  object-fit: cover;
}

.card-layout {
  --radius: 0.5rem;
  background-color: white;
  padding: 8px;
  display: grid;
  grid-template-rows: 2rem 10rem 7rem;
  width: 15rem;
  border: 1px solid #ddd;
  border-radius: var(--radius);
}
</style>
