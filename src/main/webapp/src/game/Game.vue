<script setup lang="ts">
import type { GameState } from "@/game/game.ts";
import CampView from "@/game/camp/CampView.vue";
import type { Action } from "@/game/action/Action.ts";
import { computed, ref } from "vue";
import { onKeyStroke } from "@vueuse/core";
import { vElementHover, vOnClickOutside } from "@vueuse/components";
import CardViewAction from "@/game/card/CardViewAction.vue";

const {state} = defineProps<{ state: GameState }>()
const selectedUsername = ref<string>(state.currentPlayer)
const isHandHovered = ref(false)
const isHandClicked = ref(true)
const isHandFocused = computed(() => isHandHovered.value || isHandClicked.value)

defineEmits<{
  action: [action: Action]
}>()

onKeyStroke(" ", () => {
  isHandClicked.value = !isHandClicked.value
})
</script>

<template>
  <div class="game-layout padded">
    <div>
      <div class="row gap">
      <span
        class="bordered rounded padded"
        v-for="username in Object.keys(state.camps)"
        v-text="username"
        :key="username"
        :aria-selected="selectedUsername === username"
        @click="()=>selectedUsername = username"
      />
      </div>
    </div>
    <div>
      <h2>Camp</h2>
      <CampView :camp="state.camps[selectedUsername]" @action="$emit('action',$event)"/>
    </div>
    <Teleport to="body">
      <div v-show="state.hand.length > 0" class="hand" :focused="isHandFocused">
        <!-- Spacer -->
        <div />
        <div
          class="row gap"
          v-element-hover="(v) => isHandHovered = v"
          v-on-click-outside="() => isHandClicked = false"
          @click="() => isHandClicked = true"
        >
          <CardViewAction
            v-for="{card, actions} in state.hand"
            :card
            :actions
            @action="$emit('action', $event)"
          />
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.game-layout {
  display: grid;
  grid-template-rows: auto 1fr;
  max-height: 100vh;
  overflow-y: auto;
}

.hand {
  pointer-events: none;
  position: fixed;
  display: inline grid;
  z-index: 99;
  bottom: 0;
  left: 0;
  right: 0;
  justify-content: center;
  grid-template-rows: 82% auto;

  &[focused=true] {
    grid-template-rows: 0 1fr;
  }
}

</style>
