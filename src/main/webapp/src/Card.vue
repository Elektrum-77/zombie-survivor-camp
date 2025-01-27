<script setup lang="ts">
import type { Card } from "@/game.ts";

defineProps<Card>()
</script>

<template>
  <div class="card-layout">
    <span style="font-weight: bold">{{value.name}}</span>
    <span></span>
    <p>production: {{value.production}}</p>
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
