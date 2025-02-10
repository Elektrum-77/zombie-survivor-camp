<script setup lang="ts">
import type { Camp, Hand } from "@/game.ts";
import type { HandCardAction } from "@/Action.ts";
import CardViewAction from "@/card/CardViewAction.vue";

const {hand} = defineProps<{ camp: Camp, hand: Hand }>()
defineEmits<{ action: [HandCardAction] }>()
</script>

<template>
  <div class="hand-layout row">
    <CardViewAction
      v-for="({card, actions}, index) in hand"
      :card
      :actions
      :index
      @action="$emit('action', $event)"/>
  </div>
</template>

<style scoped>
</style>
