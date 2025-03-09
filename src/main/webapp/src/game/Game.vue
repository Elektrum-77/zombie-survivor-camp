<script setup lang="ts">
import type { GameState } from "@/game/game.ts";
import CampView from "@/game/camp/CampView.vue";
import type { Action } from "@/game/action/Action.ts";
import HandView from "@/game/HandView.vue";
import { ref, shallowRef } from "vue";
import { onKeyStroke } from "@vueuse/core";
import Button from "@/shared/Button.vue";
import { vElementHover } from "@vueuse/components";

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
    <div class="row">
      <Button
        v-for="username in Object.keys(state.camps)"
        v-text="username"
        :key="username"
        :aria-selected="selectedUsername === username"
        @click="()=>selectedUsername = username"
      />
    </div>
    <div class="camp-container">
      <h2>Camp</h2>
      <CampView :camp="state.camps[selectedUsername]" @action="$emit('action',$event)"/>
    </div>
    <div
      v-show="state.hand.length > 0"
      class="hand-container"
      :style="{gridTemplateRows: showHand ? '0 1fr' : '80% auto'}"
    >
      <div></div>
      <HandView
        v-element-hover="(v)=>showHand = v"
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
