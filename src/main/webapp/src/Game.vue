<script setup lang="ts">
import type { GameState } from "@/game.ts";
import CampSelector from "@/CampSelector.vue";
import CampView from "@/CampView.vue";
import type { HandCardAction } from "@/Action.ts";
import HandView from "@/HandView.vue";
import { ref } from "vue";

const {state} = defineProps<{ state: GameState }>()
const selectedUsername = ref<string>(state.currentPlayer)

defineEmits<{
  action: [action: HandCardAction]
}>()
</script>

<template>
  <div class="game-layout">
    <div>
      <CampSelector
        :usernames="Object.keys(state.camps)"
        v-model:selected="selectedUsername"/>
    </div>
    <div class="camp-container">
      <h2>Camp</h2>
      <CampView v-bind="state.camps[selectedUsername]"/>
    </div>
    <div v-show="state.hand.length > 0" class="hand-container">
      <h2>Hand</h2>
      <HandView
        :camp="state.camps[state.currentPlayer]"
        :hand="state.hand"
        @action="$emit('action',$event)"
      />
    </div>
  </div>
</template>

<style scoped>
.game-layout {
  display: grid;
  grid-template-rows: auto 1fr auto;
  max-height: 100vh;
}

.game-layout > * {
  padding: 1rem;
}

.camp-container {
  overflow: auto;
}
</style>
