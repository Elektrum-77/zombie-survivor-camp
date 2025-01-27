<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import type { Camp } from "@/game.ts";
import { type Action, isBuildable, isSearchable } from "@/Action.ts";

defineProps<{ camp: Camp, card: Card, index: number }>()
defineEmits<{ action: [Action] }>()
</script>

<template>
  <div class="row">
    <span
      v-if="isBuildable(camp, card)"
      @click="$emit('action', { type: 'Construct', value: { index } })">
      Construire
    </span>
    <span
      v-if="isSearchable(camp, card)"
      @click="$emit('action', { type: 'Search', value: { index } })">
      Fouiller
    </span>
  </div>
</template>

<style scoped>
div {
  align-items: stretch;
  justify-content: space-around;
}
</style>
