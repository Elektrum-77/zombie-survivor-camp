<script setup lang="ts">
import type { GameState } from "@/game.ts";
import CampSelector from "@/CampSelector.vue";
import { ref, shallowRef } from "vue";
import CampView from "@/CampView.vue";
import type { ActionType, HandCardAction } from "@/Action.ts";
import type { Card } from "@/card/Card.ts";
import Hand from "@/Hand.vue";

const {currentPlayer: username} = defineProps<GameState & { isLoading?: boolean }>()
const emit = defineEmits<{
  action: [action: HandCardAction]
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
    <div class="camp-container">
      <h2>Camp</h2>
      <CampView v-bind="camps[selectedCamp]" />
    </div>
    <div v-if="!isLoading" class="hand-container">
      <h2>Hand</h2>
      <Hand
        :camp="camps[username]"
        :hand="hand.map(c=>c.card)"
        @action="$emit('action',$event)"
      />
<!--      <div class="row">-->
<!--        <CardView-->
<!--          v-for="(card, i) in hand"-->
<!--          v-bind="card"-->
<!--          class="card"-->
<!--          @click="selectCard($event, card, i)"/>-->
<!--        <CardContextualMenu-->
<!--          v-if="selectedCard !== undefined"-->
<!--          :card="selectedCard"-->
<!--          :position="selectedCardPosition"-->
<!--          @close="closeContextualMenu()"-->
<!--          @validate="validateActionAndCloseMenu($event)"-->
<!--        />-->
<!--      </div>-->
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
