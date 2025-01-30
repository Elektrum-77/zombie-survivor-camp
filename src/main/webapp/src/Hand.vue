<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import type { Camp } from "@/game.ts";
import HandCard from "@/HandCard.vue";
import type { HandCardAction } from "@/Action.ts";

defineProps<{ camp: Camp, hand: readonly Card[] }>()
defineEmits<{ action: [HandCardAction] }>()
</script>

<template>
  <div class="hand-layout row">
    <HandCard
      v-for="(card, index) in hand"
      :camp
      :card
      :index
      @action="$emit('action', $event)"/>
  </div>
</template>

<style scoped>
.hand-layout {
  height: 18rem;
}
</style>
