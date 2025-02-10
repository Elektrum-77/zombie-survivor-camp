<script setup lang="ts">
import type { GameState } from "@/game.ts";
import CampSelector from "@/CampSelector.vue";
import CampView from "@/CampView.vue";
import type {Action} from "@/Action.ts";
import HandView from "@/HandView.vue";
import {ref, shallowRef} from "vue";
import {onKeyStroke} from "@vueuse/core";

const {state} = defineProps<{ state: GameState }>()
const selectedUsername = ref<string>(state.currentPlayer)
const showHand = shallowRef(true)

defineEmits<{
  action: [action: Action]
}>()

onKeyStroke(" ", () => {
  showHand.value = !showHand.value
})
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
      <CampView v-bind="state.camps[selectedUsername]" @action="$emit('action',$event)"/>
    </div>
    <div
      v-show="state.hand.length > 0"
      class="hand-container"
      :style="{gridTemplateRows: showHand ? '0 1fr' : '80% auto'}"
    >
      <div></div>
      <HandView
        :camp="state.camps[state.currentPlayer]"
        :hand="state.hand"
        @action="$emit('action',$event)"
        style="pointer-events: auto"
      />
    </div>
  </div>
</template>

<style scoped>
.game-layout {
  display: grid;
  grid-template-rows: auto 1fr;
  max-height: 100vh;
}

.game-layout > * {
  padding: 1rem;
}

.camp-container {
  overflow: auto;
}

.hand-container {
  position: absolute;
  bottom: 0;
  left: 25%;
  display: grid;
  justify-content: center;
  transition: all 0.2s;
  pointer-events: none;
}
</style>
