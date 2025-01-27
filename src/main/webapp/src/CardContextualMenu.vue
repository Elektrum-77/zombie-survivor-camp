<script setup lang="ts">
import type {ActionType, Card} from "@/game.ts";
import CardView from "@/Card.vue";
import {onClickOutside} from "@vueuse/core";
import {ref} from "vue";

defineProps<{
  card: Card,
  position: { x: number, y: number }
}>()

const emit = defineEmits<{
  validate: [action: ActionType]
  close: []
}>()

const menu = ref<HTMLElement | null>(null)

onClickOutside(menu, () => {
  emit("close")
})
</script>

<template>
  <div>
    <div class="backdrop"></div>
    <div class="row" ref="menu" :style="{
      position: 'fixed',
      top: `${position.y}px`,
      left: `${position.x}px`,
      zIndex: 2
    }">
      <CardView :type="card.type" :value="card.value" style="transform: scale(1.05)" />
      <div class="col menu">
        <span @click="$emit('validate', 'Construct')" >Construire</span>
        <span @click="$emit('validate', 'Search')" >Rechercher</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.35);
    z-index: 1;
  }

  .menu {
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 8px;
    min-width: 150px;
    height: fit-content;
  }

  .menu > span {
    cursor: pointer;
  }

  .menu > span:hover {
    background-color: #eee;
  }
</style>
