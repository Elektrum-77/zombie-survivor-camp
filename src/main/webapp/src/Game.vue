<script setup lang="ts">
import type {Action, ActionType, Card, GameState} from "@/game.ts";
import CardView from "@/Card.vue";
import CampSelector from "@/CampSelector.vue";
import {ref, shallowRef} from "vue";
import Camp from "@/Camp.vue";
import CardContextualMenu from "@/CardContextualMenu.vue";

const {username} = defineProps<GameState & {username:string}>()
const emit = defineEmits<{
  action: [action: Action]
}>()

const selectedCamp = ref<string>(username)

const selectedCard = shallowRef<Card & {index: number}>()
const selectedCardPosition = ref<{x: number, y: number}>({x: 0, y: 0})

function selectCard(e: PointerEvent, c: Card, index: number) {
  const target = (e.currentTarget ?? e.target) as HTMLElement | null
  if (!target) return
  const rect = target.getBoundingClientRect()
  selectedCardPosition.value = {x: rect.left, y: rect.top}
  selectedCard.value = {...c, index}
}

function closeContextualMenu() {
  selectedCard.value = undefined
}

function validateActionAndCloseMenu(action: ActionType) {
  if (selectedCard.value === undefined) return
  emit("action", {type: action, value: {index: selectedCard.value.index}})
  closeContextualMenu()
}
</script>

<template>
  <div class="game-layout">
    <div>
      <CampSelector :camps="Object.keys(camps)" v-model:camp="selectedCamp" />
    </div>
    <div>
      <h2>Camp</h2>
      <Camp v-bind="camps[selectedCamp]" />
    </div>
    <div>
      <h2>Hand</h2>
      <div class="row">
        <CardView
          v-for="({type, value}, i) in hand"
          :type
          :value
          class="card"
          @click="selectCard($event, {type, value}, i)" />
        <CardContextualMenu
          v-if="selectedCard !== undefined"
          :card="selectedCard"
          :position="selectedCardPosition"
          @close="closeContextualMenu()"
          @validate="validateActionAndCloseMenu($event)"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.game-layout {
  display: grid;
  grid-template-rows: 5vh 1fr 25vh;
}

.card {
  cursor: pointer;
  transition: all 0.25s;
}

.card:hover {
  transform: scale(1.05);
  box-shadow: 0 0 32px rgba(0, 0, 0, 0.1);
}
</style>
